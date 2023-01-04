package com.rudie.replication.service;

import com.rudie.replication.model.Message;

import java.util.Set;

public interface LogRepository {
    boolean save(Message message);
    Set<Message> getAll();
}
