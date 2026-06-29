package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.dao.ContractSectorMapper;
import com.riskcontrol.domain.ContractSector;
import com.riskcontrol.service.IContractSectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContractSectorServiceImpl extends ServiceImpl<ContractSectorMapper, ContractSector> implements IContractSectorService {

    @Override
    public ContractSector getByConid(Integer conid) {
        LambdaQueryWrapper<ContractSector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractSector::getConid, conid);
        return this.getOne(queryWrapper);
    }

    @Override
    public boolean saveOrUpdateByConid(ContractSector contractSector) {
        LambdaQueryWrapper<ContractSector> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ContractSector::getConid, contractSector.getConid());
        queryWrapper.eq(ContractSector::getType, contractSector.getType());

        long count = this.count(queryWrapper);
        if (count > 0) {
            return this.update(contractSector, queryWrapper);
        } else {
            return this.save(contractSector);
        }
    }
}
