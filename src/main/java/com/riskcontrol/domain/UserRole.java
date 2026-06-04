package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

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
@TableName("user_role")
@Schema(description = "UserRole对象")
@AllArgsConstructor
@NoArgsConstructor
public class UserRole extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "用户主键")
    private Long userId;

    @Schema(description = "角色主键")
    private Long roleId;


}
