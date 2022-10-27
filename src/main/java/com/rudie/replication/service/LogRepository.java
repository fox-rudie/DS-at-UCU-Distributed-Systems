package com.rudie.replication.service;

import com.rudie.replication.model.LogMessage;

import java.util.List;

public interface LogRepository {
    boolean save(LogMessage logMessage);
    List<LogMessage> getAll();
}
