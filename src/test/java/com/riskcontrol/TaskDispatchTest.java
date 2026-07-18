package com.riskcontrol;

import com.riskcontrol.task.TaskDispatch;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TaskDispatchTest {

    @Resource
    TaskDispatch taskDispatch;

    @Test
    public void test(){
        taskDispatch.execute();
    }

    public static void main(String[] args) {
        String a = "2025-09-29";
        String b = "2026-07-02";

        System.out.println(a.compareTo(b));
    }
}
