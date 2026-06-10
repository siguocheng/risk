package com.riskcontrol.domain.vo;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * 账户币种视图对象
 *
 * @author zpc
 * @date 2026-06-10
 */
@Data
@ColumnWidth(18)
public class AccountCurrencyVo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "账户id")
    @TableField(value = "account_code")
    @ExcelProperty(value = "账户id", index = 0)
    @ColumnWidth(25)
    private String accountCode;

    @Schema(description = "BASE币种")
    @TableField(value = "currency")
    @ExcelProperty(value = "BASE币种", index = 1)
    @ColumnWidth(25)
    private String currency;
}
