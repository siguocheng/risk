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
@TableName("permission_resource")
@Schema(description = "PermissionResource对象")
public class PermissionResource extends BaseEntity {

    private static final long serialVersionUID = 1L;

    private String permissionCode;

    private String resourceUrl;

    public PermissionResource() {
    }

    public PermissionResource(String permissionCode, String resourceUrl) {
        this.permissionCode = permissionCode;
        this.resourceUrl = resourceUrl;
    }
}
