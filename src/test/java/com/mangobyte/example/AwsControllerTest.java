package com.mangobyte.example;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


import com.mangobyte.example.exception.AwsErrorException;
import com.mangobyte.example.exception.CustomErrorException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

@WebMvcTest(AwsController.class)
public class AwsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AwsRDSService service;

    @Test
    void createMysql() throws Exception {
        when(service.createMySqlDatabase()).thenReturn("success");
        mockMvc.perform(post("/mysql/database")).andExpect(content().string("success"));
    }

    @Test
    void createMysqlFail() throws Exception {
        when(service.createMySqlDatabase()).thenThrow(new CustomErrorException("error"));
        mockMvc.perform(post("/mysql/database")).andExpect(status().isConflict()).andExpect(content().string("error"));
    }

    @Test
    void createPostgre() throws Exception {
        when(service.createPostgreDatabase ()).thenReturn("success");
        mockMvc.perform(post("/postgre/database")).andExpect(content().string("success"));
    }

    @Test
    void createPostgreFail() throws Exception {
        when(service.createPostgreDatabase()).thenThrow(new CustomErrorException("error"));
        mockMvc.perform(post("/postgre/database")).andExpect(status().isConflict()).andExpect(content().string("error"));
    }

    @Test
    void deleteMysql() throws Exception {
        when(service.deleteMySqlDb()).thenReturn("success");
        mockMvc.perform(delete("/mysql/database")).andExpect(content().string("success"));
    }

    @Test
    void deleteMysqlError() throws Exception {
        when(service.deleteMySqlDb()).thenThrow(new CustomErrorException("error"));
        mockMvc.perform(delete("/mysql/database")).andExpect(status().isConflict()).andExpect(content().string("error"));
    }

    @Test
    void deletePostgre() throws Exception {
        when(service.deletePostgreDb()).thenReturn("success");
        mockMvc.perform(delete("/postgre/database")).andExpect(content().string("success"));
    }

    @Test
    void deletePostgreError() throws Exception {
        when(service.deletePostgreDb()).thenThrow(new CustomErrorException("error"));
        mockMvc.perform(delete("/postgre/database")).andExpect(status().isConflict()).andExpect(content().string("error"));
    }

    @Test
    void getMysqlStatus() throws Exception {
        when(service.getMySqlInstanceStatus()).thenReturn("available");
        mockMvc.perform(get("/mysql/database/status")).andExpect(content().string("available"));
    }

    @Test
    void getPostgreStatus() throws Exception {
        when(service.getPostgreInstanceStatus()).thenReturn("available");
        mockMvc.perform(get("/postgre/database/status")).andExpect(content().string("available"));
    }

    @Test
    void executeMysqlQuery() throws Exception {
        List<List<String>> lists = getSampleList();
        when(service.executeMysqlQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/mysql/database/query").content("testing")).andExpect(content().string(lists.toString()));
    }

    @Test
    void executeMysqlQueryTypeText() throws Exception {
        List<List<String>> lists = getSampleList();
        when(service.executeMysqlQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/mysql/database/query").content("testing").param("type", "text")).andExpect(content().string(lists.toString()));
    }

    @Test
    void executeMysqlQueryTypeHtml() throws Exception {
        List<List<String>> lists = getSampleList();
        String html = CommonUtils.convertListToTableHtml(lists);
        when(service.executeMysqlQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/mysql/database/query").content("testing").param("type", "html")).andExpect(content().string(html));
    }

    @Test
    void executeMysqlQueryTypeMarkdown() throws Exception {
        List<List<String>> lists = getSampleList();
        String markdown = CommonUtils.convertListToTableMarkdown(lists);
        when(service.executeMysqlQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/mysql/database/query").content("testing").param("type", "markdown")).andExpect(content().string(markdown));
    }

    @Test
    void executeMysqlQueryTypeInvalid() throws Exception {
        List<List<String>> lists = getSampleList();
        String error = "Invalid type.\nPlease input 'text' or 'html' or 'markdown'.";
        when(service.executeMysqlQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/mysql/database/query").content("testing").param("type", "aaa")).andExpect(content().string(error));
    }

    @Test
    void executePostgreQuery() throws Exception {
        List<List<String>> lists = getSampleList();
        when(service.executePostgreQuery( "testing")).thenReturn(lists);
        mockMvc.perform(post("/postgre/database/query").content("testing")).andExpect(content().string(lists.toString()));
    }

    @Test
    void executePostgreQueryTypeText() throws Exception {
        List<List<String>> lists = getSampleList();
        when(service.executePostgreQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/postgre/database/query").content("testing").param("type", "text")).andExpect(content().string(lists.toString()));
    }

    @Test
    void executePostgreQueryTypeHtml() throws Exception {
        List<List<String>> lists = getSampleList();
        String html = CommonUtils.convertListToTableHtml(lists);
        when(service.executePostgreQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/postgre/database/query").content("testing").param("type", "html")).andExpect(content().string(html));
    }

    @Test
    void executePostgreQueryTypeMarkdown() throws Exception {
        List<List<String>> lists = getSampleList();
        String markdown = CommonUtils.convertListToTableMarkdown(lists);
        when(service.executePostgreQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/postgre/database/query").content("testing").param("type", "markdown")).andExpect(content().string(markdown));
    }

    @Test
    void executePostgreQueryTypeInvalid() throws Exception {
        List<List<String>> lists = getSampleList();
        String error = "Invalid type.\nPlease input 'text' or 'html' or 'markdown'.";
        when(service.executePostgreQuery("testing")).thenReturn(lists);
        mockMvc.perform(post("/postgre/database/query").content("testing").param("type", "aaa")).andExpect(content().string(error));
    }

    @Test
    void executeMysqlUpdate() throws Exception {
        when(service.executeMysqlUpdate("testing")).thenReturn("done");
        mockMvc.perform(post("/mysql/database/execute").content("testing")).andExpect(content().string("done"));
    }

    @Test
    void executeMysqlUpdateFail() throws Exception {
        when(service.executeMysqlUpdate("testing")).thenThrow(new AwsErrorException("cannot find aws db instance"));
        mockMvc.perform(post("/mysql/database/execute").content("testing"))
                .andExpect(content().string("cannot find aws db instance"))
                .andExpect(status().isNotFound());
    }

    @Test
    void executePostgreUpdate() throws Exception {
        when(service.executePostgreUpdate("testing")).thenReturn("done");
        mockMvc.perform(post("/postgre/database/execute").content("testing")).andExpect(content().string("done"));
    }

    @Test
    void executePostgreUpdateFail() throws Exception {
        when(service.executePostgreUpdate("testing")).thenThrow(new AwsErrorException("cannot find aws db instance"));
        mockMvc.perform(post("/postgre/database/execute").content("testing"))
                .andExpect(content().string("cannot find aws db instance"))
                .andExpect(status().isNotFound());
    }

    private List<List<String>> getSampleList() {
        List<List<String>> lists = new ArrayList();
        List<String> list = new ArrayList();
        list.add("header1");
        list.add("header2");
        list.add("header3");
        lists.add(list);

        list = new ArrayList();
        list.add("data1-1");
        list.add("data2-1");
        list.add("data3-1");
        lists.add(list);

        list = new ArrayList();
        list.add("data2-1");
        list.add("data2-2");
        list.add("data2-3");
        lists.add(list);

        return lists;
    }
}
