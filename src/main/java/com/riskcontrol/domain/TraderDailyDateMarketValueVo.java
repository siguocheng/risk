package com.riskcontrol.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TraderDailyDateMarketValueVo {

    private String traderName;

    private String dailyDate;

    private BigDecimal marketValue;

}
