package com.riskcontrol.domain.vo;

import com.ib.client.*;
import com.riskcontrol.util.TradeTimeConvertUtil;
import lombok.Data;

@Data
public class ExecutionCallbackVo {

    private int conid;
    private String symbol;
    private int orderId;
    private int clientId;
    private String execId;
    private String time;
    private String executionTime;
    private String acctNumber;
    private String exchange;
    private String side;
    private Decimal shares;
    private double price;
    private long permId;
    private int liquidation;
    private Decimal cumQty;
    private double avgPrice;
    private String orderRef;
    private String evRule;
    private double evMultiplier;
    private String modelCode;
    private Liquidities lastLiquidity;
    private boolean pendingPriceRevision;
    private String submitter;
    private String secType;
    private String multiplier;
    private OptionExerciseType optExerciseOrLapseType;

    public ExecutionCallbackVo(){

    }

    public ExecutionCallbackVo(Contract contract, Execution execution){
        this.conid = contract.conid();
        this.symbol = contract.symbol();
        this.secType = contract.getSecType();
        this.multiplier = contract.multiplier();
        this.orderId = execution.orderId();
        this.orderId = execution.orderId();
        this.clientId = execution.clientId();
        this.execId = execution.execId();
        this.time = TradeTimeConvertUtil.convertToUsEasternStr(execution.time());
        this.executionTime = execution.time();
        this.acctNumber = execution.acctNumber();
        this.exchange = execution.exchange();
        this.side = execution.side();
        this.shares = execution.shares();
        this.price = execution.price();
        this.permId = execution.permId();
        this.liquidation = execution.liquidation();
        this.cumQty = execution.cumQty();
        this.avgPrice = execution.avgPrice();
        this.orderRef = execution.orderRef();
        this.evRule = execution.evRule();
        this.evMultiplier = execution.evMultiplier();
        this.modelCode = execution.modelCode();
        this.lastLiquidity = execution.lastLiquidity();
        this.pendingPriceRevision = execution.pendingPriceRevision();
        this.submitter = execution.submitter();
        this.optExerciseOrLapseType = execution.optExerciseOrLapseType();
    }
}
