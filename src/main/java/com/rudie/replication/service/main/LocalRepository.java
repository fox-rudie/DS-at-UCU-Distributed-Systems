package com.rudie.replication.service.main;

import com.rudie.replication.configuration.ReplicationLogProperties;
import com.rudie.replication.model.LogMessage;
import com.rudie.replication.service.LogRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class LocalRepository implements LogRepository {
    private static final List<LogMessage> logs = new LinkedList<>();
    ReplicationLogProperties properties;

    @Override
    @SneakyThrows
    public boolean save(LogMessage logMessage) {
        logs.add(logMessage);
        log.debug("[REPOSITORY] Saved log message {}", logMessage);
        delayIfPresent();
        return true;
    }

    @Override
    public List<LogMessage> getAll() {
        log.debug("[REPOSITORY] Returning {} log records", logs.size());
        return logs;
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
