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
@TableName("permission_role")
@Schema(description = "PermissionRole对象")
public class PermissionRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private Long permissionId;

    private Long roleId;


}
