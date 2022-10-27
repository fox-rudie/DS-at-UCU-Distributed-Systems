package com.rudie.replication.registry.main;

import com.rudie.replication.model.Node;
import com.rudie.replication.service.LogRepository;
import com.rudie.replication.service.main.RemoteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class NodeManager implements CommandLineRunner {
    private static final List<Node> SECONDARY_NODES = new ArrayList<>();
    private static final List<LogRepository> REPOSITORIES = new LinkedList<>();
    RestTemplate restTemplate;
    LogRepository logRepository;


    public void register(Node node) {
        log.debug("[MAIN] Trying to register secondary node {}", node);

        if (!isHealthy(node)) {
            log.debug("[MAIN] The node is not healthy {}", node);
            return;
        }

        SECONDARY_NODES.add(node);
        REPOSITORIES.add(new RemoteRepository(node, restTemplate));
        log.debug("[MAIN] Registered a new secondary node {}", node);
        log.debug("[MAIN] {} secondary nodes registered {}", SECONDARY_NODES.size(), SECONDARY_NODES);
    }

    protected boolean isHealthy(Node node) {
        String url = UriComponentsBuilder.fromHttpUrl(node.getIpAddress())
                .port(node.getPort())
                .path("/health")
                .build().toUriString();
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (RestClientException exception) {
            return false;
        }
    }

    public List<LogRepository> getRepositories() {
        return REPOSITORIES;
    }

    @Override
    public void run(String... args) {
        REPOSITORIES.add(logRepository);
        log.debug("Registered default local repository");
    }
}
