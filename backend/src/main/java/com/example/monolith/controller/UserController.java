package com.example.monolith.controller;

import static com.example.monolith.dto.ApiDtos.*;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.example.monolith.service.PersistenceService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:5173")
public class UserController {
    private final PersistenceService service;
    public UserController(PersistenceService service) { this.service = service; }

    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public UserView create(@Valid @RequestBody CreateUser input) { return service.createUser(input); }
    @GetMapping public List<UserView> list() { return service.listUsers(); }
    @GetMapping("/{id}") public UserView get(@PathVariable Long id) { return service.getUser(id); }
    @PutMapping("/{id}") public UserView update(@PathVariable Long id, @Valid @RequestBody UpdateUser input) { return service.updateUser(id, input); }
}
