package com.example.demo.config;

public enum DataSourceStrategy {
    POSTGRES,
    MYSQL;

    String getDriverClassName() {
        return switch (this) {
            case POSTGRES -> "org.postgresql.Driver";
            case MYSQL -> "com.mysql.cj.jdbc.Driver";
        };
    }
}
