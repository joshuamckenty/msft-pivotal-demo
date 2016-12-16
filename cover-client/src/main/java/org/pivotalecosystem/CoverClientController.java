package org.pivotalecosystem;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.net.URI;


@RefreshScope
@RestController
class CoverClientController {

    private final String fallbackCoverTypes;
    private final String coverServiceLogicalName;
    private final String coverTypesEndpoint;
    private final RestTemplate restTemplate;

    public CoverClientController(@Value("${cover.client.failsafe.cover-types:NotConfigured}") String fallbackCoverTypes,
                                            @Value("${cover.client.coverServiceLogicalName:cover-server}") String coverServiceLogicalName,
                                            @Value("${cover.client.coverTypesEndpoint:covers}") String coverTypesEndpoint,
                                            RestTemplate restTemplate) {
        this.fallbackCoverTypes = fallbackCoverTypes;
        this.coverServiceLogicalName = coverServiceLogicalName;
        this.coverTypesEndpoint = coverTypesEndpoint;
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getCoversFallbackMethod")
    @GetMapping("/covers")
    String index() {
        URI uri = URI.create("//" + coverServiceLogicalName + "/" + coverTypesEndpoint);
        return this.restTemplate.getForObject(uri, String.class);
    }

    String getCoversFallbackMethod() {
        return this.fallbackCoverTypes;
    }
}
