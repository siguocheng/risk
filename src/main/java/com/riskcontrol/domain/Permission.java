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
 * @since 2026-06-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("permission")
public class Permission extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description =  "父树节点")
    private Long parentId;

    private String permissionCode;

    @Schema(description =  "按钮名称")
    private String name;

    @Schema(description =  "平台类型 1:pc 2:app")
    private Integer platformType;

    @Schema(description =  "类别 1：基础权限")
    private Integer category;

    private String remark;

    public Permission() {
    }

    public Permission(Long id, Long parentId, String permissionCode, String name, Integer platformType, Integer category) {
        this.id = id;
        this.parentId = parentId;
        this.permissionCode = permissionCode;
        this.name = name;
        this.platformType = platformType;
        this.category = category;
    }

}
