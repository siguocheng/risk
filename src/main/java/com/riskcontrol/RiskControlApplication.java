package com.riskcontrol;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ServletComponentScan
@MapperScan(basePackages={"com.riskcontrol.dao"})
//@EnableScheduling
public class RiskControlApplication {

    public static void main(String[] args) {
        SpringApplication.run(RiskControlApplication.class, args);
    }
}
