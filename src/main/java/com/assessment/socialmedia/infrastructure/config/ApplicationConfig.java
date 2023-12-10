package com.assessment.socialmedia.infrastructure.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;

@EnableScheduling
@Configuration
public class ApplicationConfig {

    @Value("${database.url}")
    private String databaseUrl;

    @Value("${database.username}")
    private String databaseUsername;

    @Value("${database.password}")
    private String databasePassword;

    @Value("${database.driver-class-name}")
    private String databaseDriverName;

    @Bean
    public HikariDataSource hikariDataSource() {
        HikariConfig config = new HikariConfig();
        config.addDataSourceProperty("autoReconnect",true);
        config.addDataSourceProperty("maxReconnects",10);
        config.setMaximumPoolSize(10); // DEFAULT IS 10
        config.setPoolName("SOCIALMEDIADBPool");
        config.setUsername(databaseUsername);
        config.setPassword(databasePassword);
        config.setDriverClassName(databaseDriverName);
        config.setJdbcUrl(databaseUrl);
        return new HikariDataSource(config);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        String SALT = "fhsjdhk12h3kkslkdsndwe"; //The encryption salt
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }
}
