package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.PageColumnDisplay;

import java.util.List;

/**
 * 页面列表列展示Service接口
 *
 * @author zpc
 * @date 2026-07-13
 */
public interface IPageColumnDisplayService extends IService<PageColumnDisplay> {

    List<PageColumnDisplay> getByPageName(String pageName);

    void updateDisplay(String pageName, String type, String columnName, Boolean isDisplay);
}
