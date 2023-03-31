package com.mangobyte.example;

import com.amazonaws.services.rds.model.DBInstance;
import com.mangobyte.example.exception.CustomErrorException;
import com.mangobyte.example.properties.DbMySqlProperties;
import com.mangobyte.example.properties.DbPostgreProperties;
import com.mangobyte.example.properties.DbSecurityProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AwsRDSService {

    @Autowired
    private DbMySqlProperties dbMySqlProperties;

    @Autowired
    private DbPostgreProperties dbPostgreProperties;

    @Autowired
    private DbSecurityProperties dbSecurityProperties;

    @Autowired
    private AwsRDSRepository awsRDSRepository;

    public String createMySqlDatabase() {
        awsRDSRepository.createDatabase(awsRDSRepository.getSecurityGroupId(dbSecurityProperties), dbMySqlProperties);
        return "success";
    }

    public String createPostgreDatabase() {
        awsRDSRepository.createDatabase(awsRDSRepository.getSecurityGroupId(dbSecurityProperties), dbPostgreProperties);
        return "success";
    }

    public String deleteMySqlDb() {
        awsRDSRepository.deleteDatabase(dbMySqlProperties.getIdentifier());
        return "success";
    }

    public String deletePostgreDb() {
        awsRDSRepository.deleteDatabase(dbPostgreProperties.getIdentifier());
        return "success";
    }

    public String getMySqlInstanceStatus() {
        Optional<DBInstance> dbInstance = awsRDSRepository.getDbInstance(dbMySqlProperties);
        if (dbInstance.isPresent()) {
            return dbInstance.get().getDBInstanceStatus();
        }
        return "not exist";
    }

    public String getPostgreInstanceStatus() {
        Optional<DBInstance> dbInstance = awsRDSRepository.getDbInstance(dbPostgreProperties);
        if (dbInstance.isPresent()) {
            return dbInstance.get().getDBInstanceStatus();
        }
        return "not exist";
    }

    public List<List<String>> executeMysqlQuery(String query) {
        ResultSet rs = awsRDSRepository.executeQuery(dbMySqlProperties, query);
        return convertResultSetToList(rs);
    }

    public List<List<String>> executePostgreQuery(String query) {
        ResultSet rs = awsRDSRepository.executeQuery(dbPostgreProperties, query);
        return convertResultSetToList(rs);
    }

    public String executeMysqlUpdate(String sql) {
        return awsRDSRepository.executeUpdate(dbMySqlProperties, sql) >= 0 ? "done" : "fail";
    }

    public String executePostgreUpdate(String sql) {
        return awsRDSRepository.executeUpdate(dbPostgreProperties, sql) >= 0 ? "done" : "fail";
    }

    private List<List<String>> convertResultSetToList(ResultSet rs) {
        List<String> columnName = new ArrayList();
        List<String> rows = new ArrayList();
        List<List<String>> result = new ArrayList();
        try {
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                columnName.add(rsmd.getColumnName(i));
            }
            result.add(columnName);
            while (rs.next()) {
                for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                    rows.add(rs.getString(i));
                }
                result.add(rows);
                rows = new ArrayList<>();
            }

        } catch (SQLException exception) {
            throw new CustomErrorException(exception);
        }
        return result;
    }
}
