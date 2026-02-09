package com.adoptme;

import com.adoptme.config.DotenvLoader;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AdoptMeApplication {
    public static void main(String[] args) {
        DotenvLoader.load();
        SpringApplication.run(AdoptMeApplication.class, args);
    }
}
