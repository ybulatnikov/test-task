package com.example.demo.service;

import com.example.demo.config.DataSourceConfigProperties;
import com.example.demo.config.DataSourceConfigProperties.Mapping;
import com.example.demo.config.MultipleDataSourceProperties;
import com.example.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class UserService {

    private MultipleDataSourceProperties multipleDataSourceProperties;

    private Map<String, JdbcTemplate> jdbcTemplates;

    public List<User> getUsers() {
        return aggregateDataInParallel();
    }

    private List<User> aggregateDataInParallel() {
        return multipleDataSourceProperties.getDataSources()
                .parallelStream()
                .map(entry -> CompletableFuture.supplyAsync(() -> getUsers(entry)))
                .map(CompletableFuture::join)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    private List<User> getUsers(DataSourceConfigProperties entry) {
        String name = entry.getName();
        String selectUsersQuery = selectUsersQuery(name, entry.getMapping(), entry.getTable());

        JdbcTemplate jdbcTemplate = jdbcTemplates.get(name);
        try {
            return jdbcTemplate.query(selectUsersQuery, new DataClassRowMapper<>(User.class));
        } catch (Exception e) {
            log.error("DB: {}, Error: {}", name, e.getMessage());
            return new ArrayList<>();
        }
    }

    private String selectUsersQuery(String dbName, Mapping mapping, String table) {
        return String.format(
                "SELECT CONCAT('%s-user-id-', %s) AS id, %s AS username, %s AS name, %s AS surname " +
                "FROM %s ",
                dbName, mapping.getId(), mapping.getUsername(), mapping.getName(), mapping.getSurname(),
                table);
    }

}
