package com.riskcontrol.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.riskcontrol.constant.UserLevelEnum;
import com.riskcontrol.dao.PermissionMapper;
import com.riskcontrol.dao.PermissionRoleMapper;
import com.riskcontrol.dao.UserMapper;
import com.riskcontrol.domain.Permission;
import com.riskcontrol.domain.User;
import com.riskcontrol.domain.vo.permisssion.PermissionTableVO;
import com.riskcontrol.service.IPermissionService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author 
 * @since 2025-10-20
 */
@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements IPermissionService {

    @Resource
    PermissionRoleMapper permissionRoleMapper;

    @Resource
    UserMapper userMapper;

    @Override
    public void init() {

        this.baseMapper.deleteAll();

        List<Permission> list = new ArrayList<>();
        //pc
        {
            // 基础权限
            {
                list.add(new Permission(1030000L, 0L, null, "用户管理", 1, 1));
                {
                    list.add(new Permission(1030100L, 1030000L, null, "用户管理", 1, 1));
                    {
                        list.add(new Permission(1030101L, 1030100L, "btn-pc-user-query-page", "查看", 1, 1));
                        list.add(new Permission(1030102L, 1030100L, "btn-pc-user-create", "新增", 1, 1));
                        list.add(new Permission(1030103L, 1030100L, "btn-pc-user-update", "编辑", 1, 1));
                        list.add(new Permission(1030104L, 1030100L, "btn-pc-user-delete", "删除", 1, 1));
                        list.add(new Permission(1030105L, 1030100L, "btn-pc-user-rst-password", "重置密码", 1, 1));
                    }
                    list.add(new Permission(1030200L, 1030000L, null, "角色管理", 1, 1));
                    {
                        list.add(new Permission(1030201L, 1030200L, "btn-pc-role-query-page", "查看", 1, 1));
                        list.add(new Permission(1030202L, 1030200L, "btn-pc-role-create", "新增", 1, 1));
                        list.add(new Permission(1030203L, 1030200L, "btn-pc-role-update", "编辑", 1, 1));
                        list.add(new Permission(1030204L, 1030200L, "btn-pc-role-delete", "删除", 1, 1));
                    }
                }
            }
        }
        //app
//        {
//            // 基础权限
//            {
//               list.add(new Permission(2010000L, 0L, null, "任务清单", 2, 1));
//               {
//                  list.add(new Permission(2010100L, 2010000L, "", "查看", 2, 1));
//               }
//               list.add(new Permission(2020000L, 0L, null, "饲料称重", 2, 1));
//                {
//                    list.add(new Permission(2020100L, 2020000L, "", "查看", 2, 1));
//                    list.add(new Permission(2020101L, 2020000L, "", "新增", 2, 1));
//                    list.add(new Permission(2020102L, 2020000L, "", "调整排序", 2, 1));
//                }
//
//            }
//        }

        this.saveBatch(list);

        permissionRoleMapper.deleteNoExist();
    }

    @Override
    public List<PermissionTableVO> listPermission(Integer platformType) {
        List<PermissionTableVO> permissionList = this.baseMapper.getPermissions(platformType);
        Map<Long, List<PermissionTableVO>> permissionMap = permissionList.stream()
                .collect(Collectors.groupingBy(PermissionTableVO::getParentId));
        List<PermissionTableVO> parentList = permissionMap.get(0L);
        if (CollectionUtils.isEmpty(parentList)) {
            return new ArrayList<>();
        }
        buildChildren(parentList, permissionMap);
        return parentList;
    }

    private void buildChildren(List<PermissionTableVO> permissionList, Map<Long, List<PermissionTableVO>> permissionMap) {
        permissionList.forEach(permission -> {
            if (CollectionUtils.isEmpty(permissionMap.get(permission.getId()))) {
                return;
            }
            permission.setSonList(permissionMap.get(permission.getId()));
            buildChildren(permissionMap.get(permission.getId()), permissionMap);
        });
    }

    @Override
    public Map<String, Boolean> getPermissionCodeMapByUserId(Long userId) {
        List<String> allPermissionCode = this.baseMapper.getAllPermissionCode();
        User user = userMapper.selectById(userId);
        // 用户类型是管理员，就默认给全权限
        if (UserLevelEnum.ADMIN.value.equals(user.getLevel())) {

            Map<String, Boolean> map = new HashMap<>();
            for (String s : allPermissionCode) {
                map.put(s, true);
            }
            return map;
        } else {
            List<String> list = this.baseMapper.getPermissionCodeByUserId(userId);

            return allPermissionCode.stream().collect(Collectors.toMap(Function.identity(), list::contains));
        }
    }
}
