package com.rudie.replication.service.main;

import com.rudie.replication.model.LogMessage;
import com.rudie.replication.registry.main.NodeManager;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ReplicationService {

    RestTemplate restTemplate;
    NodeManager nodeManager;

    public void replicate(LogMessage logMessage) {
        List<LogRepository> repositories = nodeManager.getRepositories();
        repositories.stream().parallel().forEach(repository -> repository.save(logMessage));
    }

    public List<LogMessage> getLogMessages() {
        return nodeManager.getRepositories()
                .get(0)
                .getAll();
    }

}
