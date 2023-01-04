package com.rudie.replication.service.main;

import com.rudie.replication.model.Message;
import com.rudie.replication.registry.main.NodeManager;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ReplicationService {

    RestTemplate restTemplate;
    NodeManager nodeManager;

    AtomicLong idGenerator = new AtomicLong(1);


    @SneakyThrows
    public void replicate(Message message,
                          int writeConcert) {
        List<LogRepository> repositories = nodeManager.getRepositories();

        if (Objects.isNull(message.getId())) {
            message.setId(idGenerator.getAndIncrement());
        }

        CountDownLatch countDown = new CountDownLatch(writeConcert);
        log.debug("[REPLICATION] Init minimum {} write concerns.", writeConcert);

        repositories.stream().parallel()
                .forEach(repository -> CompletableFuture
                        .runAsync(() -> {
                            repository.save(message);
                            log.debug("[REPLICATION] Counting down...");
                            countDown.countDown();
                        }).whenComplete((result, exception) -> {
                            if (exception != null) {
                                log.error("[REPLICATION] Error while saving {}", exception.getMessage());
                            }
                        }));

        countDown.await();
        log.debug("[REPLICATION] Count down log released.");
    }

    public Set<Message> getLogMessages() {
        return nodeManager.getRepositories()
                .get(0)
                .getAll();
    }

}
