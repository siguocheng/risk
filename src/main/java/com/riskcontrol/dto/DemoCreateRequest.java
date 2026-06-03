package com.riskcontrol.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Demo新增请求")
public class DemoCreateRequest {

    @Schema(description = "名称", example = "示例名称")
    @NotBlank(message = "名称不能为空")
    private String name;
}
