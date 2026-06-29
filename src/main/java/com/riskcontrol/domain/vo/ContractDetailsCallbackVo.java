package com.riskcontrol.domain.vo;

import com.ib.client.ContractDetails;
import lombok.Data;

@Data
public class ContractDetailsCallbackVo {

    private Integer conid;

    private String industry;

    private String category;

    private String subcategory;

    public ContractDetailsCallbackVo(){

    }

    public ContractDetailsCallbackVo(ContractDetails contractDetails) {
        this.conid = contractDetails.conid();
        this.industry = contractDetails.industry();
        this.category = contractDetails.category();
        this.subcategory = contractDetails.subcategory();
    }
}
