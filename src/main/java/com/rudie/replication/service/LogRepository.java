package com.rudie.replication.service;

import com.rudie.replication.model.Message;
import com.rudie.replication.model.Status;

import java.util.Set;

public interface LogRepository {
    boolean save(Message message);

    Status getStatus();
    Set<Message> getAll();
}
