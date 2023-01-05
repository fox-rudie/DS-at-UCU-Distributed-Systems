package com.rudie.replication.service.main;

import com.rudie.replication.model.Message;
import com.rudie.replication.registry.main.NodeManager;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
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
    RetryTemplate retryTemplate;

    AtomicLong idGenerator = new AtomicLong(1);

    @SneakyThrows
    public void replicateWithRetry(Message message,
                                   int writeConcert) {
        List<LogRepository> repositories = nodeManager.getHealthyRepositories();

        if (repositories.size() < writeConcert) {
            throw new RuntimeException("There is no quorum (only read-mode). " +
                    "Not enough nodes. Write concern: " + writeConcert + ", healthy nodes count: " + repositories.size());
        }

        if (Objects.isNull(message.getId())) {
            message.setId(idGenerator.getAndIncrement());
        }

        CountDownLatch countDown = new CountDownLatch(writeConcert);
        log.debug("[REPLICATION] Init minimum {} write concerns.", writeConcert);

        repositories.stream().parallel()
                .forEach(repository -> CompletableFuture
                        .runAsync(() -> retryTemplate.execute((ctx) -> {
                            if (ctx.getRetryCount() > 1) {
                                log.debug("[REPLICATION] Trying to save {} {} time.", message, ctx.getRetryCount());
                            }
                            repository.save(message);

                            log.debug("[REPLICATION] Counting down...");

                            countDown.countDown();
                            return true;
                        }))
                );

        countDown.await();
        log.debug("[REPLICATION] Count down log released.");
    }

    public Set<Message> getLogMessages() {
        return nodeManager.getHealthyRepositories()
                .get(0)
                .getAll();
    }

}
