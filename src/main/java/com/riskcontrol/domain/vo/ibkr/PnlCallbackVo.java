package com.riskcontrol.domain.vo.ibkr;

import lombok.Data;

@Data
public class PnlCallbackVo {

    private double dailyPnL;
    private double unrealizedPnL;
    private double realizedPnL;

}
