package com.riskcontrol;

import com.riskcontrol.service.impl.MethodTest;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

@SpringBootTest
public class MethodTestTest {

    @Resource
    MethodTest methodTest;

    @Test
    public void reqMktData() throws IOException {
        methodTest.reqMktData();

        System.in.read();
    }
}
