package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TradeCalendarMapper;
import com.riskcontrol.domain.TradeCalendar;
import com.riskcontrol.service.ITradeCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 美股交易日历Service业务层处理
 *
 * @author zpc
 * @date 2026-07-15
 */
@Slf4j
@Service
public class TradeCalendarServiceImpl extends ServiceImpl<TradeCalendarMapper, TradeCalendar> implements ITradeCalendarService {

    @Override
    public String getPreTradeDate(String tradeDate) {
        TradeCalendar current = this.getOne(new LambdaQueryWrapper<TradeCalendar>()
                .eq(TradeCalendar::getTradeDate, tradeDate));
        if (current == null) {
            return null;
        }
        Long preId = current.getPreId();
        if (preId == null) {
            return null;
        }
        TradeCalendar pre = this.getById(preId);
        return pre != null ? pre.getTradeDate() : null;
    }

    @Override
    public void saveOrUpdateTradeCalendar(TradeCalendar tradeCalendar) {
        LambdaQueryWrapper<TradeCalendar> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TradeCalendar::getType, tradeCalendar.getType());
        queryWrapper.eq(TradeCalendar::getTradeDate, tradeCalendar.getTradeDate());
        List<TradeCalendar> list = this.list(queryWrapper);
        if (list.size() == 0) {
            this.save(tradeCalendar);
        }

    }
}