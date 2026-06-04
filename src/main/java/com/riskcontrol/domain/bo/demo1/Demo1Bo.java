package com.riskcontrol.domain.bo.demo1;

import com.baomidou.mybatisplus.annotation.TableField;
import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Demo1业务对象
 *
 * @author zpc
 * @date 2026-06-04
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class Demo1Bo extends BasePageQuery {

    @Schema(description = "主键id")
    private Long id;

    @Schema(description = "名字")
    private String name;

}
