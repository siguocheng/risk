package com.riskcontrol.controller;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.riskcontrol.annotation.ResourceMethod;
import com.riskcontrol.common.ResultBean;
import com.riskcontrol.domain.vo.user.UserLogin;
import com.riskcontrol.domain.vo.user.UserLoginVO;
import com.riskcontrol.service.IUserService;
import com.riskcontrol.util.RedisUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping(value = "/auth")
@Slf4j
@Tag(description = "用户授权认证", name = "授权认证")
public class AuthController extends BaseController {

    @Resource
    IUserService userService;

    @Operation(summary = "PC端登录", description = "PC端登录")
    @PostMapping(value = {"/pc/login"})
    @ResourceMethod(level = 1)
    public ResultBean<UserLoginVO> webLogin(@RequestBody UserLogin userLogin) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        return new ResultBean<>(userService.login(userLogin));
    }

    @PostMapping(value = {"/pc/logout"})
    @ResourceMethod(level = 1)
    public ResultBean logout(@RequestBody String json, HttpServletRequest request) throws Exception {

        Long userId = this.getUserId();

        ResultBean resultBean;
        JSONObject jsonObject = JSON.parseObject(json);
        String token = jsonObject.getString("token");
        // 下面是清理redis缓存
        token = RedisUtil.thePrefixOfToken + token;
        Long deleteAmount = RedisUtil.delete(token);
        JSONObject result = null;
        if (deleteAmount != null && deleteAmount > 0) {
            resultBean = new ResultBean(0, "退出登录成功");
        } else {
            resultBean = new ResultBean(0, "删除缓存失败");
        }
        HttpSession session = request.getSession();
        session.invalidate();

        return resultBean;
    }

}
