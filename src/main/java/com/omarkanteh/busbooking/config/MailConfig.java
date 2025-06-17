package com.omarkanteh.busbooking.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.mail")
public class MailConfig {
    private  String username;
    private long verificationTokenExpiration = 86400;


}
