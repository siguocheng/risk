package com.riskcontrol.domain.vo.position;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(18)
public class PositionImportVo {

    @ExcelProperty(value = "来源", index = 0)
    private String accountCode;

    @ExcelProperty(value = "日期", index = 0)
    @ColumnWidth(25)
    private String positionDate;

    @ExcelProperty(value = "合约", index = 1)
    @ColumnWidth(25)
    private String symbol;

    @ExcelProperty(value = "代码", index = 2)
    @ColumnWidth(25)
    private String shorName;

    @ExcelProperty(value = "收盘价格", index = 2)
    @ColumnWidth(25)
    private String calMarketPrice;

}