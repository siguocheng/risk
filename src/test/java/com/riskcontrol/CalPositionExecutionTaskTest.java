package com.riskcontrol;

import com.riskcontrol.task.CalPositionExecutionTask;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CalPositionExecutionTaskTest {

    @Resource
    CalPositionExecutionTask calPositionExecutionTask;

    @Test
    public void execute(){
        calPositionExecutionTask.cal();
    }
}
