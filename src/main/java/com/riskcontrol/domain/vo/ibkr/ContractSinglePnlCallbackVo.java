package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.Decimal;
import lombok.Data;

@Data
public class ContractSinglePnlCallbackVo {

    private Decimal pos;
    private double dailyPnL;
    private double unrealizedPnL;
    private double realizedPnL;
    private double value;
}
