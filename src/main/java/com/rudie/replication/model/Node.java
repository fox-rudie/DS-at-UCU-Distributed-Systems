package com.rudie.replication.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@Getter
@ToString
@AllArgsConstructor
public class Node {
    String ipAddress;
    int port;
}
