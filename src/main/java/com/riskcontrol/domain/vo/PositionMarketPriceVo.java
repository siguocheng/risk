package com.riskcontrol.domain.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PositionMarketPriceVo {

    private String dailyDate;

    private int conid;

    private String symbol;

    private String secType;

    private BigDecimal marketPrice;
}
