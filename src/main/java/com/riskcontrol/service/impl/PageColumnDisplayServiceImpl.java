package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.PageColumnDisplayMapper;
import com.riskcontrol.domain.PageColumnDisplay;
import com.riskcontrol.service.IPageColumnDisplayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 页面列表列展示Service业务层处理
 *
 * @author zpc
 * @date 2026-07-13
 */
@Slf4j
@Service
public class PageColumnDisplayServiceImpl extends ServiceImpl<PageColumnDisplayMapper, PageColumnDisplay> implements IPageColumnDisplayService {

    @Override
    public List<PageColumnDisplay> getByPageName(String pageName) {
        return this.list(new LambdaQueryWrapper<PageColumnDisplay>()
                .eq(PageColumnDisplay::getPageName, pageName));
    }

    @Override
    @Transactional
    public void updateDisplay(String pageName, String type, String columnName, Boolean isDisplay) {
        PageColumnDisplay display = this.getOne(new LambdaQueryWrapper<PageColumnDisplay>()
                .eq(PageColumnDisplay::getPageName, pageName)
                .eq(PageColumnDisplay::getType, type)
                .eq(PageColumnDisplay::getColumnName, columnName));
        if (display != null) {
            display.setIsDisplay(isDisplay);
            this.updateById(display);
        }
    }
}
