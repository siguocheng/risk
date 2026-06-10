package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户币种实体类
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@TableName("account_currency")
public class AccountCurrency extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账户id")
    @TableField(value = "account_code")
    private String accountCode;

    @Schema(description = "BASE币种")
    @TableField(value = "currency")
    private String currency;
}
