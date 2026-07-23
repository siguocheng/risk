package com.riskcontrol.domain.vo.positionexecution;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ColumnWidth(18)
public class PositionExecutionErrorVo {

    @ExcelProperty(value = "来源", index = 0)
    private String accountCode;

    @ExcelProperty(value = "合约", index = 1)
    @ColumnWidth(25)
    private String symbol;

    @ExcelProperty(value = "代码", index = 2)
    @ColumnWidth(25)
    private String shorName;

    @ExcelProperty(value = "交易时间", index = 3)
    @ColumnWidth(25)
    private String time;

    @ExcelProperty(value = "成交数量", index = 4)
    @ColumnWidth(25)
    private String shares;

    @ExcelProperty(value = "成交价格", index = 5)
    @ColumnWidth(25)
    private String price;

    @ExcelProperty(value = "佣金及各项费用", index = 6)
    private String commissionAndFees;

    @ExcelProperty(value = "错误内容", index = 7)
    @ColumnWidth(50)
    private String errorMsg;

}