package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.TraderCapital;

import java.math.BigDecimal;

public interface ITraderCapitalService extends IService<TraderCapital> {

    BigDecimal getCapitalByTraderDate(String traderName, String dailyDate);

    void saveOrUpdateTraderCapital(TraderCapital traderCapital);
}