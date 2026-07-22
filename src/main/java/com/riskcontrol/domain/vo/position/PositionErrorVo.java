package com.riskcontrol.domain.vo.position;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import lombok.Data;

@Data
@ColumnWidth(18)
public class PositionErrorVo {

    @ExcelProperty(value = "合约", index = 0)
    @ColumnWidth(25)
    private Integer conid;

    @ExcelProperty(value = "类型", index = 1)
    @ColumnWidth(25)
    private String secType;

    @ExcelProperty(value = "代码", index = 2)
    @ColumnWidth(25)
    private String symbol;

    @ExcelProperty(value = "错误内容", index = 3)
    @ColumnWidth(50)
    private String errorMsg;

}