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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
        ParameterizedTypeReference<Collection<Cover>> typeReference =
                new ParameterizedTypeReference<Collection<Cover>>() { };
        ResponseEntity<Collection<Cover>> responseEntity = this.restTemplate.exchange(uri,
                HttpMethod.GET, null, typeReference);
        Collection<Cover> covers = responseEntity.getBody();

        model.addAttribute("covers", covers);
		return "index";
	}

	String getCoversFallbackMethod(Model model) {
        model.addAttribute("covers", new Cover("Default Cover"));
        return "index";
    }
}


class Cover {

    private Long id;

    private String coverName;

    public Cover(String coverName) {
        this.coverName = coverName;
    }

    public Cover() { }

    public Long getId() {
        return id;
    }

    public String getCoverName() {
        return coverName;
    }
}
