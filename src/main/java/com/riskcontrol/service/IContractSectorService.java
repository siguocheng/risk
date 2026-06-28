package com.riskcontrol.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.riskcontrol.domain.ContractSector;

import java.util.List;

public interface IContractSectorService extends IService<ContractSector> {

    ContractSector getByConid(Integer conid);

    boolean saveOrUpdateByConid(ContractSector contractSector);
}
