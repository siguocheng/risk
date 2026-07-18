package com.riskcontrol.domain.bo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.riskcontrol.exception.ServiceException;
import com.riskcontrol.util.SqlUtil;
import com.riskcontrol.util.WYStringUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class BasePageQuery {

    @Schema(description = "页码", example = "1")
    protected Integer pageNum = 1;

    @Schema(description = "每页展示数", example = "10")
    protected Integer pageSize = 10;

    @Schema(description = "排序列", example = "id")
    private String orderColumn;

    @Schema(description = "排序的方向desc或者asc", example = "desc")
    private String orderType;

    /**
     * 当前记录起始索引 默认值
     */
    public static final int DEFAULT_PAGE_NUM = 1;

    /**
     * 每页显示记录数 默认值 默认查全部
     */
    public static final int DEFAULT_PAGE_SIZE = Integer.MAX_VALUE;

    public <T> Page<T> build() {
        Integer pageNum = ObjectUtil.defaultIfNull(getPageNum(), DEFAULT_PAGE_NUM);
        Integer pageSize = ObjectUtil.defaultIfNull(getPageSize(), DEFAULT_PAGE_SIZE);
        if (pageNum <= 0) {
            pageNum = DEFAULT_PAGE_NUM;
        }
        Page<T> page = new Page<>(pageNum, pageSize);
//        List<OrderItem> orderItems = buildOrderItem();
//        if (CollUtil.isNotEmpty(orderItems)) {
//            page.addOrder(orderItems);
//        }
        return page;
    }

    /**
     * 构建排序
     *
     * 支持的用法如下:
     * {orderType:"asc",orderColumn:"id"} order by id asc
     * {orderType:"asc",orderColumn:"id,createTime"} order by id asc,create_time asc
     * {orderType:"desc",orderColumn:"id,createTime"} order by id desc,create_time desc
     * {orderType:"asc,desc",orderColumn:"id,createTime"} order by id asc,create_time desc
     */
    private List<OrderItem> buildOrderItem() {
        if (WYStringUtils.isBlank(orderColumn) || WYStringUtils.isBlank(orderType)) {
            return null;
        }
        String orderBy = SqlUtil.escapeOrderBySql(orderColumn);
        orderBy = WYStringUtils.toUnderScoreCase(orderBy);

        // 兼容前端排序类型
        orderType = WYStringUtils.replaceEach(orderType, new String[]{"ascending", "descending"}, new String[]{"asc", "desc"});

        String[] orderByArr = orderBy.split(WYStringUtils.SEPARATOR);
        String[] orderTypeArr = orderType.split(WYStringUtils.SEPARATOR);
        if (orderTypeArr.length != 1 && orderTypeArr.length != orderByArr.length) {
            throw new ServiceException("排序参数有误");
        }

        List<OrderItem> list = new ArrayList<>();
        // 每个字段各自排序
        for (int i = 0; i < orderByArr.length; i++) {
            String orderByStr = orderByArr[i];
            String orderTypeStr = orderTypeArr.length == 1 ? orderTypeArr[0] : orderTypeArr[i];
            if ("asc".equals(orderTypeStr)) {
                list.add(OrderItem.asc(orderByStr));
            } else if ("desc".equals(orderTypeStr)) {
                list.add(OrderItem.desc(orderByStr));
            } else {
                throw new ServiceException("排序参数有误");
            }
        }
        return list;
    }

    @Schema(description = "请求参数", hidden = true)
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @TableField(exist = false)
    protected Map<String, Object> params = new HashMap<>();

    @Schema(description = "id集合")
    @TableField(value = "id集合")
    protected List<Long> idList;
}
