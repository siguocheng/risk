package com.riskcontrol.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.role.RoleModify;
import com.riskcontrol.domain.vo.role.RolePage;
import com.riskcontrol.domain.vo.role.RoleQuery;
import com.riskcontrol.service.IRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Tag(description = "角色", name = "角色")
@RestController
@RequestMapping("/role")
public class RoleController extends BaseController {

    @Resource
    IRoleService roleService;

    @Operation(summary = "角色列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-role-query-page", level = 3)
    public ResultBean<IPage<RolePage>> queryList(@RequestBody RoleQuery query){
        return new ResultBean<>(roleService.queryPage(query));
    }

    @Operation(summary = "新增角色")
    @PostMapping("/pc/create")
    @ResourceMethod(btnCode = "btn-pc-role-create", level = 3)
    public ResultBean<Long> create(@RequestBody RoleModify update){
        return new ResultBean<>(roleService.create(update));
    }

    @Operation(summary = "更新角色")
    @PostMapping("/pc/update")
    @ResourceMethod(btnCode = "btn-pc-role-update", level = 3)
    public ResultBean<Long> update(@RequestBody RoleModify update){
        return new ResultBean<>(roleService.update(update));
    }

    @Operation(summary = "删除角色")
    @PostMapping("/pc/delete")
    @ResourceMethod(btnCode = "btn-pc-role-delete", level = 3)
    public ResultBean<Long> delete(@RequestBody RoleModify update){
        return new ResultBean<>(roleService.delete(update.getId()));
    }
}
