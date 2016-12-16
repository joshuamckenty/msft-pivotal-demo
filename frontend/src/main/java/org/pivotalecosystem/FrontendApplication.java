package org.pivotalecosystem;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Collection;

@EnableCircuitBreaker
@SpringBootApplication
@EnableDiscoveryClient
public class FrontendApplication {
    @Bean
    @LoadBalanced
    public RestTemplate rest(RestTemplateBuilder builder) {
        return builder.build();
    }

	public static void main(String[] args) {
		SpringApplication.run(FrontendApplication.class, args);
	}
}


@Controller
class FrontendController {

    private final String fallbackCoverTypes;
    private final String coverServiceLogicalName;
    private final String coverTypesEndpoint;
    private final RestTemplate restTemplate;

    public FrontendController(@Value("${cover.client.failsafe.cover-types:NotConfigured}") String fallbackCoverTypes,
                                 @Value("${cover.client.coverServiceLogicalName:cover-server}") String coverServiceLogicalName,
                                 @Value("${cover.client.coverTypesEndpoint:covers}") String coverTypesEndpoint,
                                 RestTemplate restTemplate) {
        this.fallbackCoverTypes = fallbackCoverTypes;
        this.coverServiceLogicalName = coverServiceLogicalName;
        this.coverTypesEndpoint = coverTypesEndpoint;
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getCoversFallbackMethod")
	@RequestMapping("/")
	String index(Model model) {
        URI uri = URI.create("//" + coverServiceLogicalName + "/" + coverTypesEndpoint);
        Collection<org.pivotalecosystem.Cover> covers = this.restTemplate.getForObject(uri, Collection.class);
        model.addAttribute("covers", covers);
		return "index";
	}

	String getCoversFallbackMethod(Model model) {
        model.addAttribute("covers", new Cover("Default Cover"));
        return "index";
    }
}