package com.riskcontrol.domain.vo.positionexecution;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

import java.math.BigDecimal;

@Data
@ColumnWidth(18)
public class PositionExecutionImportVo {

    @ExcelProperty(value = "合约", index = 0)
    @ColumnWidth(25)
    private Integer conid;

    @ExcelProperty(value = "合约类型", index = 1)
    @ColumnWidth(25)
    private String secType;

    @ExcelProperty(value = "代码", index = 2)
    @ColumnWidth(25)
    private String symbol;

    @ExcelProperty(value = "交易时间", index = 3)
    @ColumnWidth(25)
    private String time;

    @ExcelProperty(value = "成交数量", index = 4)
    @ColumnWidth(25)
    private BigDecimal shares;

    @ExcelProperty(value = "成交价格", index = 5)
    @ColumnWidth(25)
    private BigDecimal price;

}