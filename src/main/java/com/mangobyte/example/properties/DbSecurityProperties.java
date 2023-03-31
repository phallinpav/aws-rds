package com.mangobyte.example.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@Configuration
@ConfigurationProperties("group")
@PropertySource("classpath:securityGroup.properties")
public class DbSecurityProperties {
    private String name;
    private String description;
    private String ipProtocol;
    private String ipAddress;
}
