package com.rudie.replication.controller;

import com.rudie.replication.model.Node;
import com.rudie.replication.registry.main.MainNodeRegistrationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/registration")
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
@ConditionalOnProperty(prefix = "replication", name = "main", havingValue = "true")
public class RegistrationController {
    MainNodeRegistrationService mainNodeRegistrationService;

    @PostMapping
    public void register(@RequestBody Node node) {
        mainNodeRegistrationService.register(node);
    }
}
