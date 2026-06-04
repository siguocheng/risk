package com.riskcontrol.domain;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
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
@TableName("user")
@Schema(description = "User对象")
@AllArgsConstructor
@NoArgsConstructor
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    @Schema(description = "账号")
    private String accountName;

    @Schema(description = "昵称")
    private String name;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "密码")
    @TableField(updateStrategy = FieldStrategy.NOT_EMPTY)
    private String password;

    @Schema(description = "手机")
    private String mobile;

    @Schema(description = "用户等级 0管理员 1普通用户")
    private Integer level;


    public User(String username, String name, Integer value) {
        this.accountName = username;
        this.name = name;
        this.level = value;
    }
}
