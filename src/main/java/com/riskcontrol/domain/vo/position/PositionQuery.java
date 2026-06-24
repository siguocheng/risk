package com.riskcontrol.domain.vo.position;

import com.riskcontrol.domain.bo.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 持仓查询请求VO
 *
 * @author zpc
 * @date 2026-06-22
 */
@Data
public class PositionQuery extends BasePageQuery {

    @Schema(description = "账号id")
    private List<String> accountCodes;

    @Schema(description = "合约ID")
    private List<Integer> conids;
}
