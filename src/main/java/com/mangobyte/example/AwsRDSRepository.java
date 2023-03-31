package com.mangobyte.example;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressRequest;
import com.amazonaws.services.ec2.model.AuthorizeSecurityGroupIngressResult;
import com.amazonaws.services.ec2.model.CreateSecurityGroupRequest;
import com.amazonaws.services.ec2.model.CreateSecurityGroupResult;
import com.amazonaws.services.ec2.model.IpPermission;
import com.amazonaws.services.ec2.model.IpRange;
import com.amazonaws.services.ec2.model.SecurityGroup;
import com.amazonaws.services.rds.AmazonRDS;
import com.amazonaws.services.rds.AmazonRDSClientBuilder;
import com.amazonaws.services.rds.model.CreateDBInstanceRequest;
import com.amazonaws.services.rds.model.DBInstance;
import com.amazonaws.services.rds.model.DeleteDBInstanceRequest;
import com.mangobyte.example.exception.AwsErrorException;
import com.mangobyte.example.exception.CustomErrorException;
import com.mangobyte.example.properties.DbProperties;
import com.mangobyte.example.properties.DbSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class AwsRDSRepository {

    private final AmazonRDS amazonRDS;
    private final AmazonEC2 amazonEC2;

    public AwsRDSRepository(@Value("${aws.accessKey}") String accessKey,
                            @Value("${aws.secretKey}") String secretKey) {
        AWSCredentialsProvider credentials = new AWSStaticCredentialsProvider(new
                BasicAWSCredentials(accessKey,
                secretKey));
        amazonRDS = AmazonRDSClientBuilder.standard().withCredentials(credentials)
                .withRegion(Regions.US_EAST_1).build();
        amazonEC2 = AmazonEC2ClientBuilder.standard().withCredentials(credentials)
                .withRegion(Regions.US_EAST_1).build();
    }

    public Optional<DBInstance> getDbInstance(DbProperties dbProperties) {
        return amazonRDS.describeDBInstances().getDBInstances().stream()
                .filter(db -> db.getDBInstanceIdentifier().equals(dbProperties.getIdentifier())).findFirst();
    }

    public void createDatabase(String securityGroupId, DbProperties dbProperties) {
        if (getDbInstance(dbProperties).isPresent()) {
            log.info("db instance already exist");
            return;
        }

        log.info("start create db instance");
        CreateDBInstanceRequest request = new CreateDBInstanceRequest();
        request.setDBInstanceIdentifier(dbProperties.getIdentifier());
        request.setDBInstanceClass(dbProperties.getInstanceClass());
        request.setEngine(dbProperties.getEngine());
        request.setMultiAZ(dbProperties.isMultiAZ());
        request.setMasterUsername(dbProperties.getMasterUserName());
        request.setMasterUserPassword(dbProperties.getMasterPassword());
        request.setDBName(dbProperties.getDbName());
        request.setStorageType(dbProperties.getStorageType());
        request.setAllocatedStorage(dbProperties.getAllocatedStorage());
        List<String> groupIds = new ArrayList();
        groupIds.add(securityGroupId);
        request.setVpcSecurityGroupIds(groupIds);
        try {
            amazonRDS.createDBInstance(request);
        } catch (Exception e) {
            throw new CustomErrorException(e);
        }
        log.info("finish create db instance");
    }

    public void deleteDatabase(String dbIdentifier) {
        log.info("start delete db instance");
        DeleteDBInstanceRequest request = new DeleteDBInstanceRequest();
        request.setDBInstanceIdentifier(dbIdentifier);
        request.setSkipFinalSnapshot(true);
        try {
            DBInstance instance = amazonRDS.deleteDBInstance(request);
        } catch (Exception e) {
            throw new CustomErrorException(e);
        }
        log.info("finish delete db instance");
    }

    public String getSecurityGroupId(DbSecurityProperties dbSecurityProperties) {
        Optional<SecurityGroup> group = amazonEC2.describeSecurityGroups().getSecurityGroups().stream()
                .filter(g -> g.getGroupName().equals(dbSecurityProperties.getName())).findFirst();
        if (group.isPresent()) {
            return group.get().getGroupId();
        }

        // Create security group if not exist
        CreateSecurityGroupRequest groupRequest = new CreateSecurityGroupRequest()
                .withGroupName(dbSecurityProperties.getName())
                .withDescription(dbSecurityProperties.getDescription());
        CreateSecurityGroupResult createSecurityGroupResult;
        try {
            createSecurityGroupResult = amazonEC2.createSecurityGroup(groupRequest);
        } catch (Exception e) {
            throw new CustomErrorException(e);
        }

        // Set inbound permission to access vpc
        // 0.0.0.0/0 = all IpV4 will be able to access
        IpRange ip_range = new IpRange()
                .withCidrIp(dbSecurityProperties.getIpAddress());
        IpPermission ip_perm = new IpPermission()
                .withIpProtocol(dbSecurityProperties.getIpProtocol())
                .withIpv4Ranges(ip_range);
        AuthorizeSecurityGroupIngressRequest auth_request = new AuthorizeSecurityGroupIngressRequest()
                .withGroupName(dbSecurityProperties.getName())
                .withIpPermissions(ip_perm);

        try {
            AuthorizeSecurityGroupIngressResult auth_response =
                    amazonEC2.authorizeSecurityGroupIngress(auth_request);
        } catch (Exception e) {
            throw new CustomErrorException(e);
        }

        return createSecurityGroupResult.getGroupId();
    }

    public int executeUpdate(DbProperties dbProperties, String sql) {
        String db_username = dbProperties.getMasterUserName();
        String db_password = dbProperties.getMasterPassword();
        String url = getUrl(dbProperties);
        try {
            Connection conn = DriverManager.getConnection(url, db_username, db_password);
            Statement statement = conn.createStatement();
            return statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new CustomErrorException(e);
        }
    }

    public ResultSet executeQuery(DbProperties dbProperties, String sql) {
        String db_username = dbProperties.getMasterUserName();
        String db_password = dbProperties.getMasterPassword();
        String url = getUrl(dbProperties);
        try {
            Connection conn = DriverManager.getConnection(url, db_username, db_password);
            Statement statement = conn.createStatement();
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            throw new CustomErrorException(e);
        }
    }

    private String getUrl(DbProperties dbProperties) {
        Optional<DBInstance> dbInstance = getDbInstance(dbProperties);
        if (dbInstance.isEmpty()) {
            throw new AwsErrorException("cannot find aws db instance");
        } else {
            if (!dbInstance.get().getDBInstanceStatus().equals("available")) {
                throw new AwsErrorException("db instance is not available");
            }
        }

        String db_hostname = dbInstance.get().getEndpoint().getAddress();
        String db_database = dbProperties.getDbName();
        int db_port = dbInstance.get().getEndpoint().getPort();
        return String.format("jdbc:%s://%s:%d/%s", dbProperties.getType(), db_hostname, db_port, db_database);
    }
}
