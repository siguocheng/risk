package com.riskcontrol.controller;


import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.permisssion.PermissionPlatformTypeVO;
import com.riskcontrol.service.IPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
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
@Tag(description = "权限", name = "权限")
@RestController
@RequestMapping("/permission")
public class PermissionController extends BaseController {

    @Resource
    IPermissionService permissionService;

    @GetMapping(value = "/pc/query-list")
    @Operation(summary = "权限列表")
    @ResourceMethod(btnCode = "btn-pc-role-update", level = 3)
    public ResultBean<PermissionPlatformTypeVO> listPermission() {

        PermissionPlatformTypeVO vo = new PermissionPlatformTypeVO();
        vo.setPc(permissionService.listPermission(1));
        vo.setApp(permissionService.listPermission(2));
        return new ResultBean<>(vo);
    }
}
