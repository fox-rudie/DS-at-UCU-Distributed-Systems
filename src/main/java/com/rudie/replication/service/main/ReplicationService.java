package com.rudie.replication.service.main;

import com.rudie.replication.model.LogMessage;
import com.rudie.replication.registry.main.NodeManager;
import com.rudie.replication.service.LogRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ReplicationService {

    RestTemplate restTemplate;
    NodeManager nodeManager;


    @SneakyThrows
    public void replicate(LogMessage logMessage,
                          int writeConcert,
                          HttpServletResponse response) {
//        AtomicInteger realConcert = new AtomicInteger(0);
        List<LogRepository> repositories = nodeManager.getRepositories();

        CountDownLatch countDown = new CountDownLatch(writeConcert);
        log.debug("[REPLICATION] Init minimum {} write concerns.", writeConcert);

        repositories.stream().parallel()
                .forEach(repository -> CompletableFuture
                        .runAsync(() -> {
                            repository.save(logMessage);
                            log.debug("[REPLICATION] Counting down...");
                            countDown.countDown();
                        }));

        countDown.await();
        log.debug("[REPLICATION] Count down log released.");
    }

    public List<LogMessage> getLogMessages() {
        return nodeManager.getRepositories()
                .get(0)
                .getAll();
    }

}
