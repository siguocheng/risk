package com.riskcontrol.domain.vo.ibkr;

import com.ib.client.Contract;
import lombok.Data;

@Data
public class PositionItem {

    private String account;
    private String modelCode;
    private Contract contract;
    private String position;
    private Double avgCost;
    private Double unrealizedPnl;
}
