package com.iplion.films;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.jms.annotation.EnableJms;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableJms
public class FilmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(FilmsApplication.class, args);
    }

}
