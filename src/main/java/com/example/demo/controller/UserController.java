package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Tag(name = "User API", description = "API for managing users")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users from all data sources", description = "Retrieve a list of all users")
    public List<User> getAllUsers() {
        return userService.getUsers();
    }
}
