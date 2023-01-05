package com.rudie.replication.service.main;

import com.rudie.replication.model.Message;
import com.rudie.replication.model.Node;
import com.rudie.replication.model.Status;
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
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class RemoteRepository implements LogRepository {
    protected final Node node;
    protected final String url;
    protected final RestTemplate restTemplate;

    public RemoteRepository(Node node, RestTemplate restTemplate) {
        this.node = node;
        this.restTemplate = restTemplate;
        this.url = UriComponentsBuilder.fromHttpUrl(node.getIpAddress())
                .port(node.getPort())
                .path("/api/private/log")
                .build().toUriString();
    }

    @Override
    public boolean save(Message message) {
        log.debug("[MAIN] Replicating log message with id {} to node {}", message.getId(), node.getIpAddress());

        Instant start = Instant.now();
        restTemplate.postForEntity(url, message, Void.class);
        log.debug("[MAIN] Successfully replicated message with id {} to node {}. Time elapsed: {}",
                message.getId(),
                node.getIpAddress(),
                Duration.between(start, Instant.now()).toMillis());

        return true;
    }

    @Override
    public Status getStatus() {
        String healthCheckUrl = UriComponentsBuilder.fromHttpUrl(node.getIpAddress())
                .port(node.getPort())
                .path("/health")
                .build().toUriString();

        ResponseEntity<Void> response = restTemplate.getForEntity(healthCheckUrl, Void.class);
        if (response.getStatusCode().is2xxSuccessful()) {
            return Status.HEALTHY;
        }
        return Status.UNHEALTHY;
    }

    @Override
    public Set<Message> getAll() {
        log.debug("[MAIN] Trying to fetch logs from remote log repository on node {}", node.getIpAddress());

        Instant start = Instant.now();

        ResponseEntity<Message[]> remoteLogs = restTemplate.getForEntity(url, Message[].class);
        Set<Message> messages = Stream.of(Objects.requireNonNull(remoteLogs.getBody(),
                "[MAIN] Failed to receive logs from remote repository on node " + node.getIpAddress())).collect(Collectors.toSet());

        log.debug("[MAIN] Successfully fetched {} logs from node {}. Time elapsed: {}",
                messages.size(),
                node.getIpAddress(),
                Duration.between(start, Instant.now()).get(ChronoUnit.MILLIS));

        return messages;
    }
}
