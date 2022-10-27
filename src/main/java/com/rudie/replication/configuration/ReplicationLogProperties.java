package com.rudie.replication.configuration;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "replication")
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ReplicationLogProperties {
    boolean main;
    String mainHostname;
    int delayInSeconds;
}
