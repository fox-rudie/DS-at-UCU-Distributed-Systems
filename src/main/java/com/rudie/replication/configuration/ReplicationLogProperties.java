package com.rudie.replication.configuration;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "replication")
@FieldDefaults(makeFinal = true, level = AccessLevel.PROTECTED)
public class ReplicationLogProperties {
    boolean main;
    String mainHostname;
    @PositiveOrZero
    int delayInSeconds;
    @Positive
    int writeConcert;
}
