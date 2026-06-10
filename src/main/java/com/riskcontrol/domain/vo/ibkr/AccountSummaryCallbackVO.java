package com.riskcontrol.domain.vo.ibkr;

import lombok.Data;

@Data
public class AccountSummaryCallbackVO {

    private String account;
    private String tag;
    private String value;
    private String currency;
}
