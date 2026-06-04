package com.riskcontrol.domain.vo.role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

@Data
public class RoleModify {

    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "权限列表id")
    private List<Long> permissionIdList;
}
