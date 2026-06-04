package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("role")
@Schema(description = "Role对象")
public class Role extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "角色名称")
    private String name;

    @Schema(description = "是否默认角色，0否，1是，是的话则不允许删除")
    private Boolean isDefaultRole;


}
