package com.rudie.replication.service.main;

import com.rudie.replication.configuration.ReplicationLogProperties;
import com.rudie.replication.model.Message;
import com.rudie.replication.model.Status;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class LocalRepository implements LogRepository {
    private static final Set<Message> logs = new LinkedHashSet<>();
    ReplicationLogProperties properties;

    @Override
    @SneakyThrows
    public boolean save(Message message) {
        if (logs.add(message)) {
            log.debug("[REPOSITORY] Saved log message {}", message);
            delayIfPresent();
            return true;
        }
        log.error("[REPOSITORY] Duplicate ids are not allowed");
        return false;
    }

    @Override
    public Set<Message> getAll() {
        log.debug("[REPOSITORY] Returning {} log records", logs.size());
        return logs;
    }

    @Override
    public Status getStatus() {
        return Status.HEALTHY;
    }

    public void saveAll(List<Message> messages) {
        log.debug("[REPOSITORY] Recovering messages {}...", messages);
        logs.addAll(messages);
    }

    @SneakyThrows
    private void delayIfPresent() {
        int delayInSeconds = properties.getDelayInSeconds();
        if(delayInSeconds != 0) {
            log.debug("Artificial delay {}s found. Going to sleep...", delayInSeconds);
            TimeUnit.SECONDS.sleep(delayInSeconds);
        }
    }
}
