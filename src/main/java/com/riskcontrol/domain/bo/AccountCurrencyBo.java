package com.riskcontrol.domain.bo;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 账户币种业务对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AccountCurrencyBo extends BasePageQuery {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "账户id")
    private String accountCode;

    @Schema(description = "BASE币种")
    private String currency;
}
