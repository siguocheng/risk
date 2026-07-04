package com.riskcontrol.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.riskcontrol.domain.PositionExecution;
import com.riskcontrol.service.IPositionExecutionService;
import com.riskcontrol.service.IPositionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CalPositionExecutionTask {

    @Resource
    IPositionExecutionService positionExecutionService;

    public void cal(){
        // 取得未核算的交易信息
        List<PositionExecution> positionExecutions = this.listPositionExecution();

        //

    }

    private List<PositionExecution> listPositionExecution(){
        LambdaQueryWrapper<PositionExecution> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecution::getStatus, 0);

        queryWrapper.orderByAsc(PositionExecution::getTime);

        return positionExecutionService.list(queryWrapper);
    }
}
