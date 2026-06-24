package com.riskcontrol.domain.bo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountCodeBo {

    @Schema(description = "账号id")
    private String accountCode;
}
