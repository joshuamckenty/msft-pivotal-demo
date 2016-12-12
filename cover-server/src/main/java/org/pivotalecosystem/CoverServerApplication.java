package org.pivotalecosystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;


@SpringBootApplication
@EnableDiscoveryClient
public class CoverServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CoverServerApplication.class, args);
	}

	public CoverServerApplication(@Value("${test.env.variable:NotConfigured}") String testEnv) {
		LoggerFactory.getLogger(this.getClass()).info("The TEST_ENV_VARIABLE is set to '{}'", testEnv);
	}
}

@RefreshScope
@RestController
class CoverServiceController {

	private static final Logger LOG = LoggerFactory.getLogger(CoverServiceController.class);

	@Value("${cover.service.cover-types:NotConfigured}")
	private String covers;

	@Value("${cover.service.random-delay:false}")
	private boolean addRandomDelay;

	@GetMapping(value = "/covers")
	public String getCovers() {

		if (addRandomDelay) {
			long random = (new Double(Math.random() * 1000)).longValue();
			try {
				TimeUnit.MILLISECONDS.sleep(random);
			} catch (InterruptedException ie) {
				LOG.trace("The sleep for {} milliseconds was interrupted: {} ", random, ie.getMessage());
			}
		}

		return covers;
	}
}
