package com.riskcontrol.domain.vo;

import com.ib.client.*;
import lombok.Data;

@Data
public class ExecutionCallbackVo {

    private int conid;
    private int orderId;
    private int clientId;
    private String execId;
    private String time;
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
    private OptionExerciseType optExerciseOrLapseType;

    public ExecutionCallbackVo(){

    }

    public ExecutionCallbackVo(Contract contract, Execution execution){
        this.conid = contract.conid();
        this.orderId = execution.orderId();
        this.orderId = execution.orderId();
        this.clientId = execution.clientId();
        this.execId = execution.execId();
        this.time = execution.time();
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
