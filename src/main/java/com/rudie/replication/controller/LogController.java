package com.rudie.replication.controller;

import com.rudie.replication.model.LogMessage;
import com.rudie.replication.service.main.ReplicationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/log")
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class LogController {

    ReplicationService replicationService;

    @PostMapping
    public ResponseEntity<Void> appendLogs(@RequestBody LogMessage logMessage) {
        replicationService.replicate(logMessage);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<LogMessage>> getLogs() {
        return ResponseEntity.ok(replicationService.getLogMessages());
    }

}
