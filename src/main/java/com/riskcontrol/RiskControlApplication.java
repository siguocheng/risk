package com.riskcontrol;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;

@SpringBootApplication
public class RiskControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskControlApplication.class, args);
    }
}
