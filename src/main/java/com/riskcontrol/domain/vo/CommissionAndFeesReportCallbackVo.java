package com.riskcontrol.domain.vo;

import com.ib.client.CommissionAndFeesReport;
import lombok.Data;

@Data
public class CommissionAndFeesReportCallbackVo {

    private String execId;

    // 佣金及各项费用
    private double commissionAndFees;

    // 结算币种
    private String currency;

    // 已实现盈亏
    private double realizedPNL;

    // 收益率
    private double yield;

    // 收益兑付日期
    private int yieldRedemptionDate;


    public CommissionAndFeesReportCallbackVo(){

    }

    public CommissionAndFeesReportCallbackVo(CommissionAndFeesReport commissionAndFeesRepor){
        this.execId = commissionAndFeesRepor.execId();
        this.commissionAndFees = commissionAndFeesRepor.commissionAndFees();
        this.currency = commissionAndFeesRepor.currency();
        this.realizedPNL = commissionAndFeesRepor.realizedPNL();
        this.yield = commissionAndFeesRepor.yield();
        this.yieldRedemptionDate = commissionAndFeesRepor.yieldRedemptionDate();
    }
}
