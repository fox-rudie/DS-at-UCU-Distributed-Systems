package com.rudie.replication.service.main;

import com.rudie.replication.model.LogMessage;
import com.rudie.replication.model.Node;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class RemoteRepository implements LogRepository {
    protected final Node node;
    protected final RestTemplate restTemplate;
    protected final String url;

    public RemoteRepository(Node node, RestTemplate restTemplate) {
        this.node = node;
        this.restTemplate = restTemplate;
        this.url = UriComponentsBuilder.fromHttpUrl(node.getIpAddress())
                .port(node.getPort())
                .path("/api/log")
                .build().toUriString();
    }

    @Override
    public boolean save(LogMessage logMessage) {
        log.debug("[MAIN] Replicating log message with id {} to node {}", logMessage.getId(), node.getIpAddress());

        Instant start = Instant.now();
        restTemplate.postForEntity(url, logMessage, Void.class);
        log.debug("[MAIN] Successfully replicated message with id {} to node {}. Time elapsed: {}",
                logMessage.getId(),
                node.getIpAddress(),
                Duration.between(start, Instant.now()).toMillis());

        return true;
    }

    @Override
    public List<LogMessage> getAll() {
        log.debug("[MAIN] Trying to fetch logs from remote log repository on node {}", node.getIpAddress());

        Instant start = Instant.now();

        ResponseEntity<LogMessage[]> remoteLogs = restTemplate.getForEntity(url, LogMessage[].class);
        List<LogMessage> logMessages = Arrays.asList(Objects.requireNonNull(remoteLogs.getBody(),
                "[MAIN] Failed to receive logs from remote repository on node " + node.getIpAddress()));

        log.debug("[MAIN] Successfully fetched {} logs from node {}. Time elapsed: {}",
                logMessages.size(),
                node.getIpAddress(),
                Duration.between(start, Instant.now()).get(ChronoUnit.MILLIS));

        return logMessages;
    }
}
