package fr.aumjaud.antoine.services.home.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan("fr.aumjaud.antoine.services")
public class LaunchServer {
    private static final Logger LOGGER = LoggerFactory.getLogger(LaunchServer.class);

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LaunchServer.class, args);
        LOGGER.info("Started");
    }
}
