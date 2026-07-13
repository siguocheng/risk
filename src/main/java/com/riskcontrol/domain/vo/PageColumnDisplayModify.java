package com.riskcontrol.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 页面列表列展示修改请求
 *
 * @author zpc
 * @date 2026-07-13
 */
@Data
public class PageColumnDisplayModify {

    @Schema(description = "页面名称")
    private String pageName;

    @Schema(description = "类型")
    private String type;

    @Schema(description = "列名称")
    private String columnName;

    @Schema(description = "是否展示")
    private Boolean isDisplay;
}
