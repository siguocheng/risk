package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.TradeCalendar;

/**
 * 美股交易日历Service接口
 *
 * @author zpc
 * @date 2026-07-15
 */
public interface ITradeCalendarService extends IService<TradeCalendar> {

    String getPreTradeDate(String tradeDate);

    void saveOrUpdateTradeCalendar(TradeCalendar tradeCalendar);

    String getLastTradeDate();
}