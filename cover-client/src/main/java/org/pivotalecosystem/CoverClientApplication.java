package org.pivotalecosystem;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@EnableCircuitBreaker
@SpringBootApplication
public class CoverClientApplication {

    @Bean
    @LoadBalanced
    public RestTemplate rest(RestTemplateBuilder builder) {
        return builder.build();
    }

    public static void main(String[] args) {
        SpringApplication.run(CoverClientApplication.class, args);
    }
}

@RefreshScope
@RestController
class CoverServiceClientRestController {

    private final String fallbackCoverTypes;
    private final String coverServiceLogicalName;
    private final String coverTypesEndpoint;
    private final RestTemplate restTemplate;

    public CoverServiceClientRestController(@Value("${cover.client.failsafe.cover-types:NotConfigured}") String fallbackCoverTypes,
                                            @Value("${cover.client.coverServiceLogicalName:cover-server}") String coverServiceLogicalName,
                                            @Value("${cover.client.coverTypesEndpoint:covers}") String coverTypesEndpoint,
                                            RestTemplate restTemplate) {
        this.fallbackCoverTypes = fallbackCoverTypes;
        this.coverServiceLogicalName = coverServiceLogicalName;
        this.coverTypesEndpoint = coverTypesEndpoint;
        this.restTemplate = restTemplate;
    }

    @GetMapping("/covers")
    @HystrixCommand(fallbackMethod = "getCoversFallbackMethod")
    public String covers() {
        URI uri = URI.create("//" + coverServiceLogicalName + "/" + coverTypesEndpoint);
        return this.restTemplate.getForObject(uri, String.class);
    }

    public String getCoversFallbackMethod() {
        return this.fallbackCoverTypes;
    }
}