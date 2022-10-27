package com.rudie.replication;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ConfigurationPropertiesScan
@ComponentScan(basePackages = {
		"com.rudie.replication.configuration",
		"com.rudie.replication.controller",
		"com.rudie.replication.registry",
		"com.rudie.replication.service"
})
public class ReplicationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReplicationApplication.class, args);
	}

}
