package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.riskcontrol.domain.BaseEntity;
import com.riskcontrol.domain.bo.codetemplate.CodeTemplateBo;
import com.riskcontrol.dao.CodeTemplateMapper;
import com.riskcontrol.domain.CodeTemplate;
import com.riskcontrol.domain.CurrencyEntity;
import com.riskcontrol.service.ICodeTemplateService;
import com.riskcontrol.util.WYObjectUtils;
import com.riskcontrol.util.WYStringUtils;
import com.riskcontrol.domain.vo.codetemplate.CodeTemplateVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 代码模板Service业务层处理
 *
 * @author fallrain
 * @date 2026-04-07
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CodeTemplateServiceImpl implements ICodeTemplateService {

    private final CodeTemplateMapper baseMapper;

    /**
     * 查询代码模板
     *
     * @param id 主键
     * @return 代码模板
     */
    @Override
    public CodeTemplateVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询代码模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 代码模板分页列表
     */
    @Override
    public IPage<CodeTemplateVo> queryPageList(CodeTemplateBo bo) {
        LambdaQueryWrapper<CodeTemplate> lqw = buildQueryWrapper(bo);
        Page<CodeTemplateVo> result = baseMapper.selectVoPage(bo.build(), lqw);
        return result;
    }

    /**
     * 查询符合条件的代码模板列表
     *
     * @param bo 查询条件
     * @return 代码模板列表
     */
    @Override
    public List<CodeTemplateVo> queryList(CodeTemplateBo bo) {
        LambdaQueryWrapper<CodeTemplate> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<CodeTemplate> buildQueryWrapper(CodeTemplateBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<CodeTemplate> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(CodeTemplate::getId);
        lqw.eq(CodeTemplate::getDeleted, Boolean.FALSE);
        lqw.eq(WYObjectUtils.isNotNull(bo.getCodeId()), CodeTemplate::getCodeId, bo.getCodeId());
        lqw.eq(WYObjectUtils.isNotNull(bo.getCodeState()), CodeTemplate::getCodeState, bo.getCodeState());
        lqw.like(WYStringUtils.isNotBlank(bo.getCodeName()), CodeTemplate::getCodeName, bo.getCodeName());
        lqw.eq(WYStringUtils.isNotBlank(bo.getCodeCode()), CodeTemplate::getCodeCode, bo.getCodeCode());
        lqw.eq(WYObjectUtils.isNotNull(bo.getCodeStatus()), CodeTemplate::getCodeStatus, bo.getCodeStatus());
        lqw.eq(WYObjectUtils.isNotNull(bo.getCodeType()), CodeTemplate::getCodeType, bo.getCodeType());
        lqw.eq(WYStringUtils.isNotBlank(bo.getCodeUrl()), CodeTemplate::getCodeUrl, bo.getCodeUrl());
        lqw.eq(WYStringUtils.isNotBlank(bo.getCodeText()), CodeTemplate::getCodeText, bo.getCodeText());
        lqw.eq(WYObjectUtils.isNotNull(bo.getNumberProductionRod()), CodeTemplate::getNumberProductionRod, bo.getNumberProductionRod());
        lqw.eq(WYStringUtils.isNotBlank(bo.getRemark()), CodeTemplate::getRemark, bo.getRemark());
        lqw.between(WYObjectUtils.isNotNull(params.get("beginTime"))  && WYObjectUtils.isNotNull(params.get("endTime")),CodeTemplate::getCreateTime, params.get("beginTime"), params.get("endTime"));
        return lqw;
    }

    /**
     * 新增代码模板
     *
     * @param bo 代码模板
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(CodeTemplateBo bo) {
        CodeTemplate add = WYObjectUtils.convert(bo, CodeTemplate.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 修改代码模板
     *
     * @param bo 代码模板
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(CodeTemplateBo bo) {
        CodeTemplate update = WYObjectUtils.convert(bo, CodeTemplate.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(CodeTemplate entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除代码模板信息
     *
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(CodeTemplateBo bo, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return ChainWrappers.lambdaUpdateChain(baseMapper)
                .in(CodeTemplate::getId, bo.getIdList())
                .set(BaseEntity::getDeleted, Boolean.TRUE)
                .update();
    }
}
