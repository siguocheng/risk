package com.riskcontrol.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.user.UserModify;
import com.riskcontrol.domain.vo.user.UserPage;
import com.riskcontrol.domain.vo.user.UserQuery;
import com.riskcontrol.exception.BusinessException;
import com.riskcontrol.service.IUserService;
import com.riskcontrol.util.EncryptionUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Tag(description = "用户", name = "用户")
@RestController
@RequestMapping("/user")
public class UserController extends BaseController {

    @Resource
    IUserService userService;

    @Operation(summary = "用户列表")
    @PostMapping("/pc/query-page")
    @ResourceMethod(btnCode = "btn-pc-user-query-page", level = 3)
    public ResultBean<IPage<UserPage>> queryList(@RequestBody UserQuery query){
        return new ResultBean<>(userService.queryPage(query));
    }

    @Operation(summary = "新增用户")
    @PostMapping("/pc/create")
    @ResourceMethod(btnCode = "btn-pc-user-create", level = 3)
    public ResultBean<Long> create(@RequestBody UserModify update) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new ResultBean<>(userService.create(update));
    }

    @Operation(summary = "更新用户")
    @PostMapping("/pc/update")
    @ResourceMethod(btnCode = "btn-pc-user-update", level = 3)
    public ResultBean<Long> update(@RequestBody UserModify update){
        return new ResultBean<>(userService.update(update));
    }

    @Operation(summary = "删除用户")
    @PostMapping("/pc/delete")
    @ResourceMethod(btnCode = "btn-pc-user-delete", level = 3)
    public ResultBean<Long> delete(@RequestBody UserModify update){
        return new ResultBean<>(userService.delete(update.getId()));
    }
    
    @Operation(summary = "重置密码")
    @PostMapping("/pc/reset")
    @ResourceMethod(btnCode = "btn-pc-user-rst-password", level = 3)
    public ResultBean<Integer> resetPassword(@RequestBody UserModify userFormVO) throws NoSuchAlgorithmException, UnsupportedEncodingException{
    	User user = userService.queryUserInfo(userFormVO.getId());
        if (user == null) {
            throw new BusinessException("当前用户不存在或已被删除");
        }
        String password = EncryptionUtil.hashPassword(userFormVO.getPassword());
        user.setPassword(password);
        Integer updateAmount = userService.updatePassword(user);
        return new ResultBean<>(updateAmount);
    }
}
