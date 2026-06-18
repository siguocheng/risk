package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.IbOrderMapper;
import com.riskcontrol.domain.IbOrder;
import com.riskcontrol.service.IIbOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * IB TWS 期权/股票订单Service业务层处理
 *
 * @author zpc
 * @date 2026-06-17
 */
@Slf4j
@Service
public class IbOrderServiceImpl extends ServiceImpl<IbOrderMapper, IbOrder> implements IIbOrderService {

    @Override
    public boolean saveOrUpdateByPermId(IbOrder ibOrder) {
        LambdaQueryWrapper<IbOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(IbOrder::getPermId, ibOrder.getPermId());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(ibOrder, queryWrapper);
        } else {
            return this.save(ibOrder);
        }
    }
}