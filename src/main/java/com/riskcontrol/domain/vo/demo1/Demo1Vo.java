package com.riskcontrol.domain.vo.demo1;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Demo1视图对象
 *
 * @author zpc
 * @date 2026-06-04
 */
@Data
@ColumnWidth(18)
public class Demo1Vo implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "主键id")
    @TableField(value = "id")
    private Long id;

    @Schema(description = "名字")
    @TableField(value = "name")
    @ExcelProperty(value = "名字", index = 0)
    @ColumnWidth(25)
    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间")
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间")
    @TableField(value = "modified_time")
    private LocalDateTime modifiedTime;

}
