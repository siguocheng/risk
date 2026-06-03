package com.riskcontrol.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.domain.bo.codetemplate.CodeTemplateBo;
import com.riskcontrol.domain.vo.codetemplate.CodeTemplateVo;

import java.util.List;

/**
 * 代码模板Service接口
 *
 * @author zpc
 * @date 2026-06-01
 */
public interface ICodeTemplateService {

    /**
     * 查询代码模板
     *
     * @param id 主键
     * @return 代码模板
     */
    CodeTemplateVo queryById(Long id);

    /**
     * 分页查询代码模板列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 代码模板分页列表
     */
    IPage<CodeTemplateVo> queryPageList(CodeTemplateBo bo);

    /**
     * 查询符合条件的代码模板列表
     *
     * @param bo 查询条件
     * @return 代码模板列表
     */
    List<CodeTemplateVo> queryList(CodeTemplateBo bo);

    /**
     * 新增代码模板
     *
     * @param bo 代码模板
     * @return 是否新增成功
     */
    Boolean insertByBo(CodeTemplateBo bo);

    /**
     * 修改代码模板
     *
     * @param bo 代码模板
     * @return 是否修改成功
     */
    Boolean updateByBo(CodeTemplateBo bo);

    /**
     * 校验并批量删除代码模板信息
     *
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(CodeTemplateBo bo, Boolean isValid);
}
