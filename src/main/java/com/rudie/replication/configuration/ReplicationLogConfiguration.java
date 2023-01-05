package com.rudie.replication.configuration;

import com.rudie.replication.exception.ReplicationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.ExponentialRandomBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@EnableRetry
@Configuration
public class ReplicationLogConfiguration {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public RetryTemplate retryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        ExponentialRandomBackOffPolicy expRandomBackOffPolicy = new ExponentialRandomBackOffPolicy();
        expRandomBackOffPolicy.setInitialInterval(1000);
        expRandomBackOffPolicy.setMaxInterval(16000);
        expRandomBackOffPolicy.setMultiplier(2);

        retryTemplate.setBackOffPolicy(expRandomBackOffPolicy);

        Map<Class<? extends Throwable>, Boolean> includeExceptions = new HashMap<>();
        includeExceptions.put(ReplicationException.class, true);
        includeExceptions.put(RestClientException.class, true);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy(6, includeExceptions);
        retryTemplate.setRetryPolicy(retryPolicy);

        return retryTemplate;
    }

}
