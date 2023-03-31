package com.mangobyte.example.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties("db.postgre")
@PropertySource("classpath:db.properties")
public class DbPostgreProperties implements DbProperties {
    private String type;
    private String identifier;
    private String instanceClass;
    private String engine;
    private boolean multiAZ;
    private String masterUserName;
    private String masterPassword;
    private String dbName;
    private String storageType;
    private int allocatedStorage;
}
