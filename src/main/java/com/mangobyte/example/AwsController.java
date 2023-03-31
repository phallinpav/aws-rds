package com.mangobyte.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/")
public class AwsController {

    @Autowired
    private AwsRDSService awsRDSService;

    @PostMapping("/mysql/database")
    public String createMySqlDatabase() {
        return awsRDSService.createMySqlDatabase();
    }

    @PostMapping("/postgre/database")
    public String createPostgreDatabase() {
        return awsRDSService.createPostgreDatabase();
    }

    @DeleteMapping("/mysql/database")
    public String deleteMySqlDatabase() {
        return awsRDSService.deleteMySqlDb();
    }

    @DeleteMapping("/postgre/database")
    public String deletePostgreDatabase() {
        return awsRDSService.deletePostgreDb();
    }

    @GetMapping("/mysql/database/status")
    public String mysqlStatus() {
        return awsRDSService.getMySqlInstanceStatus();
    }

    @GetMapping("/postgre/database/status")
    public String postgreStatus() {
        return awsRDSService.getPostgreInstanceStatus();
    }

    @PostMapping("/mysql/database/query")
    public String executeMySqlQuery(@RequestParam Optional<String> type, @RequestBody String sqlQuery) {
        List<List<String>> result = awsRDSService.executeMysqlQuery(sqlQuery);
        if (type.isPresent()) {
            return CommonUtils.convertList(result, type.get());
        }
        return result.toString();
    }

    @PostMapping("/postgre/database/query")
    public String executePostgreQuery(@RequestParam Optional<String> type, @RequestBody String sqlQuery) {
        List<List<String>> result = awsRDSService.executePostgreQuery(sqlQuery);
        if (type.isPresent()) {
            return CommonUtils.convertList(result, type.get());
        }
        return result.toString();
    }

    @PostMapping("/mysql/database/execute")
    public String executeMysqlExecute(@RequestBody String sqlExecute) {
        return awsRDSService.executeMysqlUpdate(sqlExecute);
    }

    @PostMapping("/postgre/database/execute")
    public String executePostgreExecute(@RequestBody String sqlExecute) {
        return awsRDSService.executePostgreUpdate(sqlExecute);
    }
}
