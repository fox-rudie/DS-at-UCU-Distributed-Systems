package com.rudie.replication.registry.secondary;

import com.rudie.replication.configuration.ReplicationLogProperties;
import com.rudie.replication.model.Node;
import com.rudie.replication.registry.RegistrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "replication", name = "main", havingValue = "false")
public class SecondaryNodeRegistrationService implements CommandLineRunner, RegistrationService {

    @Value("${server.port}")
    int port;
    protected final RestTemplate restTemplate;
    protected final ReplicationLogProperties replicationLogProperties;

    @Override
    public void run(String... args) {
        try {
            register(new Node("http://" + InetAddress.getLocalHost().getHostAddress(), port));
        } catch (UnknownHostException e) {
            log.error("[SECONDARY] Cannot obtain valid hostname for secondary node: {}", e.getMessage());
        } catch (RestClientException e) {
            log.error("[SECONDARY] Shutting down secondary node...");
            throw e;
        }
    }

    public void register(Node node) {
        log.debug("[SECONDARY] Trying to register secondary node {} in main node hostname - {}", node, replicationLogProperties.getMainHostname());
        try {
            String url = UriComponentsBuilder.fromHttpUrl(replicationLogProperties.getMainHostname())
                    .path("/api/private/registration")
                    .build().toUriString();
            restTemplate.postForEntity(url, node, Void.class);
        } catch (RestClientException e) {
            log.error("[SECONDARY] Failed to register secondary node...");
            throw e;
        }
    }
}
