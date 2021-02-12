package com.proj.msorder.config;

import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("serv-discovery")
@EnableEurekaClient
@Configuration
public class DiscoveryConfig {
}
