package com.rudie.replication.registry.main;

import com.rudie.replication.model.Node;
import com.rudie.replication.service.LogRepository;
import com.rudie.replication.service.main.RemoteRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

        if (!SECONDARY_NODES.contains(node)) {
            SECONDARY_NODES.add(node);
            REPOSITORIES.add(new RemoteRepository(node, restTemplate));
        }

        log.debug("[MAIN] Registered a new secondary node {}", node);
        log.debug("[MAIN] {} secondary nodes registered {}", SECONDARY_NODES.size(), SECONDARY_NODES);
    }

    public List<LogRepository> getHealthyRepositories() {
        return REPOSITORIES;
    }

    @Override
    public void run(String... args) {
        REPOSITORIES.add(logRepository);
        log.debug("Registered default local repository");
    }
}
