package com.example.demo.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashMap;
import java.util.Map;

@Configuration
@AllArgsConstructor
public class DataSourcesConfig {

    private final MultipleDataSourceProperties yamlConfig;

    @Bean
    public Map<String, JdbcTemplate> jdbcTemplates() {
        Map<String, JdbcTemplate> templates = new HashMap<>();
        yamlConfig.getDataSources().forEach(properties -> {
            HikariDataSource dataSource = new HikariDataSource();
            dataSource.setDriverClassName(properties.getStrategy().getDriverClassName());
            dataSource.setJdbcUrl(properties.getUrl());
            dataSource.setUsername(properties.getUser());
            dataSource.setPassword(properties.getPassword());
            templates.put(properties.getName(), new JdbcTemplate(dataSource));
        });
        return templates;
    }

}
