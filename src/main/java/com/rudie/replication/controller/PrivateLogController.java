package com.rudie.replication.controller;

import com.rudie.replication.model.Message;
import com.rudie.replication.service.main.ReplicationService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/private/log")
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class PrivateLogController {

    ReplicationService replicationService;

    @PostMapping
    public ResponseEntity<Void> appendLogs(@Valid @RequestBody Message message,
                                           @RequestParam (defaultValue = "1") int writeConcert) {
        replicationService.replicateWithRetry(message, writeConcert);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<Set<Message>> getLogs() {
        Set<Message> logMessages = replicationService.getLogMessages().stream()
                .sorted(Comparator.comparingLong(Message::getId))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return ResponseEntity.ok(logMessages);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}
