package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 交易员和账号关系实体类
 *
 * @author zpc
 * @date 2026-06-18
 */
@Data
public class TraderAccount extends BaseEntity {

    @Schema(description = "交易员名称")
    @TableField(value = "trader_name")
    private String traderName;

    @Schema(description = "交易员ID")
    @TableField(value = "trader_id")
    private Long traderId;

    @Schema(description = "账号代码")
    @TableField(value = "account_code")
    private String accountCode;
}