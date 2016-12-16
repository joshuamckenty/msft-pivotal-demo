package org.pivotalecosystem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;


interface CoverRepository extends JpaRepository<Cover, Long> {
}

@SpringBootApplication
@EnableDiscoveryClient
public class CoverServerApplication implements CommandLineRunner {

    private final CoverRepository coverRepository;

    public CoverServerApplication(CoverRepository coverRepository,
                                  @Value("${test.env.variable:NotConfigured}") String testEnv) {
        LoggerFactory.getLogger(this.getClass()).info("The TEST_ENV_VARIABLE is set to '{}'", testEnv);
        this.coverRepository = coverRepository;
    }

    public static void main(String[] args) {
        SpringApplication.run(CoverServerApplication.class, args);
    }

    @Override
    public void run(String... strings) throws Exception {
        coverRepository.deleteAll();
        Stream.of("No Cover", "Auto Cover", "Home Cover", "Holiday Cover", "Pet Cover",
                "Duvet Cover").forEach(n -> coverRepository.save(new Cover(n)));
    }
}

@RefreshScope
@RestController
class CoverServerRestController {

    private static final Logger LOG = LoggerFactory.getLogger(CoverServerRestController.class);

    @Autowired
    private CoverRepository coverRepository;

    @Value("${cover.service.random-delay:false}")
    private boolean addRandomDelay;

    @GetMapping(value = "/covers")
    public Collection<Cover> getCovers() {

        if (addRandomDelay) {
            long random = (new Double(Math.random() * 1000)).longValue();
            try {
                TimeUnit.MILLISECONDS.sleep(random);
            } catch (InterruptedException ie) {
                LOG.trace("The sleep for {} milliseconds was interrupted: {} ", random, ie.getMessage());
            }
        }

        return this.coverRepository.findAll();
    }
}

