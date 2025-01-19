package com.example.demo.service;

import com.example.demo.config.DataSourceConfigProperties;
import com.example.demo.config.DataSourceConfigProperties.Mapping;
import com.example.demo.config.DataSourceStrategy;
import com.example.demo.config.MultipleDataSourceProperties;
import com.example.demo.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@SpringBootTest
@Testcontainers
class UserServiceIntegrationTest {

    @Container
    @SuppressWarnings({"resource"})
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @Container
    @SuppressWarnings("resource")
    private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpassword");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data-sources[0].name", () -> "db-1");
        registry.add("spring.data-sources[0].strategy", () -> "mysql");
        registry.add("spring.data-sources[0].url", mySQLContainer::getJdbcUrl);
        registry.add("spring.data-sources[0].user", mySQLContainer::getUsername);
        registry.add("spring.data-sources[0].password", mySQLContainer::getPassword);
        registry.add("spring.data-sources[0].table", () -> "users");
        registry.add("spring.data-sources[0].mapping.id", () -> "user_id");
        registry.add("spring.data-sources[0].mapping.username", () -> "login");
        registry.add("spring.data-sources[0].mapping.name", () -> "first_name");
        registry.add("spring.data-sources[0].mapping.surname", () -> "last_name");

        registry.add("spring.data-sources[1].name", () -> "db-2");
        registry.add("spring.data-sources[1].strategy", () -> "postgres");
        registry.add("spring.data-sources[1].url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.data-sources[1].user", postgreSQLContainer::getUsername);
        registry.add("spring.data-sources[1].password", postgreSQLContainer::getPassword);
        registry.add("spring.data-sources[1].table", () -> "user_table");
        registry.add("spring.data-sources[1].mapping.id", () -> "ldap_login");
        registry.add("spring.data-sources[1].mapping.username", () -> "ldap_login");
        registry.add("spring.data-sources[1].mapping.name", () -> "name");
        registry.add("spring.data-sources[1].mapping.surname", () -> "surname");
    }

    @Autowired
    private UserService userService;

    @Autowired
    private Map<String, JdbcTemplate> jdbcTemplates;

    @Autowired
    private MultipleDataSourceProperties multipleDataSourceProperties;

    @Test
    public void testGetUsers() {
        User user1 = User.builder()
                .id("db-1-user-id-1")
                .username("user-1")
                .name("User")
                .surname("Userenko")
                .build();
        createAndInsertUser(user1, multipleDataSourceProperties.getDataSources().get(0));
        User user2 = User.builder()
                .id("db-2-user-id-user-2")
                .username("user-2")
                .name("Testuser")
                .surname("Testov")
                .build();
        createAndInsertUser(user2, multipleDataSourceProperties.getDataSources().get(1));

        List<User> users = userService.getUsers();

        assertThat(users, containsInAnyOrder(user1, user2));
    }

    private void createAndInsertUser(User user, DataSourceConfigProperties properties) {
        String name = properties.getName();
        JdbcTemplate jdbcTemplate = jdbcTemplates.get(name);

        String createTableQuery = createTableQuery(properties.getStrategy(), properties.getTable(), properties.getMapping());
        jdbcTemplate.execute(createTableQuery);

        String insertUserQuery = insertUserQuery(properties.getTable(), properties.getMapping());
        jdbcTemplate.update(insertUserQuery, user.getUsername(), user.getName(), user.getSurname());
    }

    private String insertUserQuery(String table, Mapping mapping) {
        return String.format("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)",
                table, mapping.getUsername(), mapping.getName(), mapping.getSurname());
    }

    private String createTableQuery(DataSourceStrategy strategy, String table, Mapping mapping) {
        String queryString = "";
        if (DataSourceStrategy.POSTGRES.equals(strategy))
        {
            queryString = """
                    CREATE TABLE IF NOT EXISTS %s (
                        id SERIAL PRIMARY KEY,
                        %s VARCHAR(50),
                        %s VARCHAR(100),
                        %s VARCHAR(100)
                    )
                """;
        }
        else if (DataSourceStrategy.MYSQL.equals(strategy))
        {
            queryString = """
                    CREATE TABLE IF NOT EXISTS %s (
                        user_id INT PRIMARY KEY AUTO_INCREMENT,
                        %s VARCHAR(50),
                        %s VARCHAR(100),
                        %s VARCHAR(100)
                )
            """;
        }
        return String.format(queryString,
                table,
                mapping.getUsername(),
                mapping.getName(),
                mapping.getSurname());
    }

}