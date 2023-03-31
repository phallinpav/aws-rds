package com.mangobyte.example;

import com.amazonaws.services.rds.model.DBInstance;
import com.mangobyte.example.exception.CustomErrorException;
import com.mangobyte.example.properties.DbMySqlProperties;
import com.mangobyte.example.properties.DbPostgreProperties;
import com.mangobyte.example.properties.DbSecurityProperties;
import com.mockrunner.mock.jdbc.MockResultSet;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AwsRDSServiceTest {

    @InjectMocks
    private AwsRDSService awsRDSService;

    @Mock
    private DbMySqlProperties dbMySqlProperties;

    @Mock
    private DbPostgreProperties dbPostgreProperties;

    @Mock
    private DbSecurityProperties dbSecurityProperties;

    @Mock
    private AwsRDSRepository awsRDSRepository;

    @Test
    void createMySqlDatabase() {
        when(awsRDSRepository.getSecurityGroupId(any(DbSecurityProperties.class))).thenReturn("1");
        doNothing().when(awsRDSRepository).createDatabase(eq("1"), any(DbMySqlProperties.class));
        assertEquals(awsRDSService.createMySqlDatabase(), "success");
    }

    @Test
    void createMySqlDatabase_error() {
        when(awsRDSRepository.getSecurityGroupId(any(DbSecurityProperties.class))).thenReturn("1");
        doThrow(new CustomErrorException(""))
                .when(awsRDSRepository).createDatabase(eq("1"), any(DbMySqlProperties.class));
        assertThrows(CustomErrorException.class, () -> awsRDSService.createMySqlDatabase());
    }

    @Test
    void createPostgreDatabase() {
        when(awsRDSRepository.getSecurityGroupId(any(DbSecurityProperties.class))).thenReturn("1");
        doNothing().when(awsRDSRepository).createDatabase(eq("1"), any(DbPostgreProperties.class));
        assertEquals(awsRDSService.createPostgreDatabase(), "success");
    }

    @Test
    void createPostgreDatabase_error() {
        when(awsRDSRepository.getSecurityGroupId(any(DbSecurityProperties.class))).thenReturn("1");
        doThrow(new CustomErrorException(""))
                .when(awsRDSRepository).createDatabase(eq("1"), any(DbPostgreProperties.class));
        assertThrows(CustomErrorException.class, () -> awsRDSService.createPostgreDatabase());
    }

    @Test
    void deleteMySqlDb() {
        when(dbMySqlProperties.getIdentifier()).thenReturn("mysql-identifier");
        doNothing().when(awsRDSRepository).deleteDatabase("mysql-identifier");
        assertEquals(awsRDSService.deleteMySqlDb(), "success");
    }

    @Test
    void deletePostgreDb() {
        when(dbPostgreProperties.getIdentifier()).thenReturn("postgre-identifier");
        doNothing().when(awsRDSRepository).deleteDatabase("postgre-identifier");
        assertEquals(awsRDSService.deletePostgreDb(), "success");
    }

    @Test
    void getMySqlInstanceStatus_notExist() {
        when(awsRDSRepository.getDbInstance(any(DbMySqlProperties.class))).thenReturn(Optional.empty());
        assertEquals(awsRDSService.getMySqlInstanceStatus(), "not exist");
    }

    @Test
    void getMySqlInstanceStatus_not() {
        DBInstance dbInstance = new DBInstance();
        dbInstance.setDBInstanceStatus("available");
        when(awsRDSRepository.getDbInstance(any(DbMySqlProperties.class))).thenReturn(Optional.of(dbInstance));
        assertEquals(awsRDSService.getMySqlInstanceStatus(), "available");
    }

    @Test
    void getPostgreInstanceStatus_notExist() {
        when(awsRDSRepository.getDbInstance(any(DbPostgreProperties.class))).thenReturn(Optional.empty());
        assertEquals(awsRDSService.getPostgreInstanceStatus(), "not exist");
    }

    @Test
    void getPostgreInstanceStatus_not() {
        DBInstance dbInstance = new DBInstance();
        dbInstance.setDBInstanceStatus("available");
        when(awsRDSRepository.getDbInstance(any(DbPostgreProperties.class))).thenReturn(Optional.of(dbInstance));
        assertEquals(awsRDSService.getPostgreInstanceStatus(), "available");
    }

    @Test
    void executeMysqlUpdate() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbMySqlProperties.class), eq(sql))).thenReturn(1);
        assertEquals(awsRDSService.executeMysqlUpdate(sql), "done");
    }

    @Test
    void executeMysqlUpdate_fail() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbMySqlProperties.class), eq(sql))).thenReturn(-1);
        assertEquals(awsRDSService.executeMysqlUpdate(sql), "fail");
    }

    @Test
    void executeMysqlUpdate_error() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbMySqlProperties.class), eq(sql))).thenThrow(new CustomErrorException(""));
        assertThrows(CustomErrorException.class, () -> awsRDSService.executeMysqlUpdate(sql));
    }

    @Test
    void executeMysqlQuery() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeQuery(any(DbMySqlProperties.class), eq(sql))).thenReturn(getResultSetSample());
        assertEquals(awsRDSService.executeMysqlQuery(sql), getListSample());
    }

    @Test
    void executeMysqlQuery_error() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeQuery(any(DbMySqlProperties.class), eq(sql))).thenThrow(new CustomErrorException(""));
        assertThrows(CustomErrorException.class, () -> awsRDSService.executeMysqlQuery(sql));
    }

    @Test
    void executePostgreQuery() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeQuery(any(DbPostgreProperties.class), eq(sql))).thenReturn(getResultSetSample());
        assertEquals(awsRDSService.executePostgreQuery(sql), getListSample());
    }

    @Test
    void executePostgreQuery_error() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeQuery(any(DbPostgreProperties.class), eq(sql))).thenThrow(new CustomErrorException(""));
        assertThrows(CustomErrorException.class, () -> awsRDSService.executePostgreQuery(sql));
    }

    @Test
    void executePostgreUpdate() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbPostgreProperties.class), eq(sql))).thenReturn(1);
        assertEquals(awsRDSService.executePostgreUpdate(sql), "done");
    }

    @Test
    void executePostgreUpdate_fail() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbPostgreProperties.class), eq(sql))).thenReturn(-1);
        assertEquals(awsRDSService.executePostgreUpdate(sql), "fail");
    }

    @Test
    void executePostgreUpdate_error() {
        String sql = "sample-test-sql";
        when(awsRDSRepository.executeUpdate(any(DbPostgreProperties.class), eq(sql))).thenThrow(new CustomErrorException(""));
        assertThrows(CustomErrorException.class, () -> awsRDSService.executePostgreUpdate(sql));
    }


    private MockResultSet getResultSetSample() {
        MockResultSet resultSet = new MockResultSet("mock");
        resultSet.addColumn("id", new Integer[]{1, 2, 3});
        resultSet.addColumn("name", new String[]{"John", "Dave", "Monica"});
        return resultSet;
    }

    private List<List<String>> getListSample() {
        List<List<String>> data = new ArrayList();
        data.add(Arrays.asList("id", "name"));
        data.add(Arrays.asList("1", "John"));
        data.add(Arrays.asList("2", "Dave"));
        data.add(Arrays.asList("3", "Monica"));
        return data;
    }
}
