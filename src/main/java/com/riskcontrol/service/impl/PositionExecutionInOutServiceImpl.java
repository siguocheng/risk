package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionExecutionInOutMapper;
import com.riskcontrol.domain.Contract;
import com.riskcontrol.domain.PositionExecutionInOut;
import com.riskcontrol.service.IPositionExecutionInOutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 交易出入库Service业务层处理
 *
 * @author zpc
 * @date 2026-07-05
 */
@Slf4j
@Service
public class PositionExecutionInOutServiceImpl extends ServiceImpl<PositionExecutionInOutMapper, PositionExecutionInOut> implements IPositionExecutionInOutService {

    @Override
    public Boolean saveOrUpdatePositionExecutionInOut(PositionExecutionInOut positionExecutionInOut) {

        LambdaQueryWrapper<PositionExecutionInOut> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PositionExecutionInOut::getPositionExecutionInId, positionExecutionInOut.getPositionExecutionInId());
        queryWrapper.eq(PositionExecutionInOut::getPositionExecutionOutId, positionExecutionInOut.getPositionExecutionOutId());
        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(positionExecutionInOut, queryWrapper);
        } else {
            return this.save(positionExecutionInOut);
        }

    }
}