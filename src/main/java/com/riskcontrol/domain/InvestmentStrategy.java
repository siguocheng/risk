package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 投资策略实体类
 *
 * @author zpc
 * @date 2026-06-19
 */
@Data
@TableName("investment_strategy")
@Schema(description = "投资策略")
public class InvestmentStrategy extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "策略名称")
    @TableField(value = "strategy_name")
    private String strategyName;
}
