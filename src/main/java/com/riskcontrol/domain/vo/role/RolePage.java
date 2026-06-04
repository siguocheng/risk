package com.riskcontrol.domain.vo.role;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RolePage {

    @Schema(description = "角色id")
    private Long id;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "是否默认角色，0否，1是，是的话则不允许删除")
    private Boolean isDefaultRole;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Schema(description = "修改时间")
    private LocalDateTime updateTime;

    @Schema(description = "创建者")
    private String createUserName;

    @Schema(description = "权限id")
    private List<Long> permissionIdList;
}
