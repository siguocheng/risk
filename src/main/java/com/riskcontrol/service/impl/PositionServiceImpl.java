package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PositionMapper;
import com.riskcontrol.domain.AccountSummaryCurrency;
import com.riskcontrol.domain.Position;
import com.riskcontrol.service.IPositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 持仓列表Service业务层处理
 *
 * @author zpc
 * @date 2026-06-10
 */
@Slf4j
@Service
public class PositionServiceImpl extends ServiceImpl<PositionMapper, Position> implements IPositionService {


    @Override
    public boolean saveOrUpdatePosition(Position position) {
        LambdaQueryWrapper<Position> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.eq(Position::getAccountCode, position.getAccountCode());
        queryWrapper.eq(Position::getConId, position.getConId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            // 存在则更新
            return this.update(position, queryWrapper);
        } else {
            // 不存在则新增
            return this.save(position);
        }
    }
}
