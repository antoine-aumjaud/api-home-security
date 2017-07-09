package fr.aumjaud.antoine.services.home.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@EnableAutoConfiguration
public class LaunchServer {

	private static final Logger LOGGER = LoggerFactory.getLogger(LaunchServer.class);


    @RequestMapping("/")
    String home() {
        return "Hello World!";
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(LaunchServer.class, args);
        LOGGER.info("Started");
    }

}