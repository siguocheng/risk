package com.riskcontrol.domain.vo.trader;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 交易员查询条件VO
 *
 * @author zpc
 * @date 2026-06-20
 */
@Data
public class TraderSelectQuery {

    @Schema(description = "账号代码列表")
    private List<String> accountCodes;

    @Schema(description = "交易员名称（支持模糊查询）")
    private String traderName;
}
