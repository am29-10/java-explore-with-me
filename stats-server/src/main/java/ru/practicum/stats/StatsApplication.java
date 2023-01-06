package ru.practicum.stats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StatsApplication {

    public static void main(String[] args) {
        System.setProperty("server.port", "9090");
        SpringApplication.run(StatsApplication.class, args);
    }

}
