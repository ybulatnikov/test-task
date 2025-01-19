package com.example.demo.config;

import lombok.Data;

@Data
public class DataSourceConfigProperties {
    private String name;
    private DataSourceStrategy strategy;
    private String url;
    private String user;
    private String password;
    private String table;
    private Mapping mapping;

    @Data
    public static class Mapping {
        private String id;
        private String username;
        private String name;
        private String surname;
    }
}
