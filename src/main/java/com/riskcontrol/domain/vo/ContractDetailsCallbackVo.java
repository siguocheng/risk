package com.riskcontrol.domain.vo;

import com.ib.client.ContractDetails;
import lombok.Data;

@Data
public class ContractDetailsCallbackVo {

    private Integer conid;

    private String industry;

    private String category;

    private String subcategory;

    private String symbol;

    private String secType;

    public ContractDetailsCallbackVo(){

    }

    public ContractDetailsCallbackVo(ContractDetails contractDetails) {
        this.conid = contractDetails.conid();
        this.symbol = contractDetails.contract().symbol();
        this.secType = contractDetails.contract().secType().getApiString();
        this.industry = contractDetails.industry();
        this.category = contractDetails.category();
        this.subcategory = contractDetails.subcategory();
    }
}
