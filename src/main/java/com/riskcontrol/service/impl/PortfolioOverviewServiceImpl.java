package com.riskcontrol.service.impl;

import com.riskcontrol.dao.PositionRelationMapper;
import com.riskcontrol.domain.bo.PortfolioOverviewBo;
import com.riskcontrol.domain.vo.PortfolioOverviewVo;
import com.riskcontrol.service.IPortfolioOverviewService;
import com.riskcontrol.service.IPositionRelationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PortfolioOverviewServiceImpl implements IPortfolioOverviewService {

    private final PositionRelationMapper positionRelationMapper;

    @Override
    public List<PortfolioOverviewVo> queryPortfolioOverview(PortfolioOverviewBo portfolioOverviewBo) {

        List<PortfolioOverviewVo> result = new ArrayList<>();



        return result;
    }
}
