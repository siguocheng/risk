package com.riskcontrol.domain.vo.role;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class RoleQuery extends BasePageQuery {

    @Schema(description = "角色名称")
    private String name;
}
