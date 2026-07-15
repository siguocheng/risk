package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.TradeCalendarMapper;
import com.riskcontrol.domain.TradeCalendar;
import com.riskcontrol.service.ITradeCalendarService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 美股交易日历Service业务层处理
 *
 * @author zpc
 * @date 2026-07-15
 */
@Slf4j
@Service
public class TradeCalendarServiceImpl extends ServiceImpl<TradeCalendarMapper, TradeCalendar> implements ITradeCalendarService {

}