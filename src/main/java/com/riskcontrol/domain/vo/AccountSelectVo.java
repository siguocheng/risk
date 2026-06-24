package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountSelectVo {

    @Schema(description = "账号id")
    private String accountCode;
}
