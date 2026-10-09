package com.readercafeproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReaderCafeProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReaderCafeProjectApplication.class, args);
    }

}
