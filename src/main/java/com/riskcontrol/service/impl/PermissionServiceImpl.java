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
                list.add(new Permission(1010000L, 0L, null, "任务管理", 1, 1));
                {
                    list.add(new Permission(1010100L, 1010000L, null, "计划清单", 1, 1));
                    {
                        list.add(new Permission(1010101L, 1010100L, "btn-pc-plan-query-page", "查看", 1, 1));
                        list.add(new Permission(1010102L, 1010100L, "btn-pc-plan-create", "新增", 1, 1));
                        list.add(new Permission(1010103L, 1010100L, "btn-pc-plan-update", "编辑", 1, 1));
                        list.add(new Permission(1010104L, 1010100L, "btn-pc-plan-delete", "删除", 1, 1));
                        list.add(new Permission(1010105L, 1010100L, "btn-pc-plan-enable", "启用", 1, 1));
                        list.add(new Permission(1010106L, 1010100L, "btn-pc-plan-disable", "禁用", 1, 1));
                        list.add(new Permission(1010107L, 1010100L, "btn-pc-task-cancel", "任务取消", 1, 1));
                    }
                    
                    list.add(new Permission(1010200L, 1010000L, null, "称重任务", 1, 1));
                    {
                        list.add(new Permission(1010201L, 1010200L, "btn-pc-weighing-task-query", "查看", 1, 1));
                        list.add(new Permission(1010202L, 1010200L, "btn-pc-weighing-task-add", "新增", 1, 1));
                        list.add(new Permission(1010203L, 1010200L, "btn-pc-weighing-task-delete", "删除", 1, 1));
                    }
                    list.add(new Permission(1010300L, 1010000L, null, "动物喂食", 1, 1));
                    {
                        list.add(new Permission(1010301L, 1010300L, "btn-pc-feeding-task-query", "查看", 1, 1));
                        list.add(new Permission(1010302L, 1010300L, "btn-pc-feeding-task-sort-adjust", "调整顺序", 1, 1));
                    }
                    
                    list.add(new Permission(1010400L, 1010000L, null, "吹洗笼具", 1, 1));
                    {
                        list.add(new Permission(1010401L, 1010400L, "btn-pc-cage-clean-query", "查看", 1, 1));
                        list.add(new Permission(1010402L, 1010400L, "btn-pc-cage-cleaning-task-add", "新增", 1, 1));
                        list.add(new Permission(1010403L, 1010400L, "btn-pc-cage-cleaning-sort-adjust", "调整顺序", 1, 1));
                    }
                    
                    list.add(new Permission(1010500L, 1010000L, null, "笼具搬运", 1, 1));
                    {
                        list.add(new Permission(1010501L, 1010500L, "btn-pc-carry-page", "查看", 1, 1));
                        list.add(new Permission(1010502L, 1010500L, "btn-pc-carry-task-add", "新增", 1, 1));
                    }
                    list.add(new Permission(1010600L, 1010000L, null, "异常清单", 1, 1));
                    {
                        list.add(new Permission(1010601L, 1010600L, "btn-pc-abnormal-query-page", "查看", 1, 1));
                    }
                }
                list.add(new Permission(1020000L, 0L, null, "基础数据", 1, 1));
                {
                    list.add(new Permission(1020100L, 1020000L, null, "组织建模", 1, 1));
                    {
                        list.add(new Permission(1020101L, 1020100L, "btn-pc-factory-query-list", "查看", 1, 1));
                        list.add(new Permission(1020102L, 1020100L, "btn-pc-factory-create", "新增", 1, 1));
                        list.add(new Permission(1020103L, 1020100L, "btn-pc-factory-update", "编辑", 1, 1));
                        list.add(new Permission(1020104L, 1020100L, "btn-pc-factory-delete", "删除", 1, 1));
                        list.add(new Permission(1020105L, 1020100L, "btn-pc-factory-type-all", "组织类型", 1, 1));

                    }
                    list.add(new Permission(1020200L, 1020000L, null, "动物管理", 1, 1));
                    {
                        list.add(new Permission(1020201L, 1020200L, "btn-pc-animal-category-query-page", "查看", 1, 1));
                        list.add(new Permission(1020202L, 1020200L, "btn-pc-animal-category-create", "新增", 1, 1));
                        list.add(new Permission(1020203L, 1020200L, "btn-pc-animal-category-update", "编辑", 1, 1));
                        list.add(new Permission(1020204L, 1020200L, "btn-pc-animal-category-delete", "删除", 1, 1));
                    }

                    list.add(new Permission(1020300L, 1020000L, null, "饲料管理", 1, 1));
                    {
                        list.add(new Permission(1020301L, 1020300L, "btn-pc-feed-query-page", "查看", 1, 1));
                        list.add(new Permission(1020302L, 1020300L, "btn-pc-feed-create", "新增", 1, 1));
                        list.add(new Permission(1020303L, 1020300L, "btn-pc-feed-update", "编辑", 1, 1));
                        list.add(new Permission(1020304L, 1020300L, "btn-pc-feed-delete", "删除", 1, 1));
                    }
                    list.add(new Permission(1020400L, 1020000L, null, "设备管理", 1, 1));
                    {
                        list.add(new Permission(1020401L, 1020400L, "btn-pc-device-query-page", "查看", 1, 1));
                        list.add(new Permission(1020402L, 1020400L, "btn-pc-device-create", "新增", 1, 1));
                        list.add(new Permission(1020403L, 1020400L, "btn-pc-device-update", "编辑", 1, 1));
                        list.add(new Permission(1020404L, 1020400L, "btn-pc-device-delete", "删除", 1, 1));
                    }
                    list.add(new Permission(1020500L, 1020000L, null, "动物类型管理", 1, 1));
                    {
                        list.add(new Permission(1020501L, 1020500L, "btn-pc-animal-type-query-list", "查看", 1, 1));
                        list.add(new Permission(1020502L, 1020500L, "btn-pc-animal-type-create", "新增", 1, 1));
                        list.add(new Permission(1020503L, 1020500L, "btn-pc-animal-type-update", "编辑", 1, 1));
                        list.add(new Permission(1020504L, 1020500L, "btn-pc-animal-type-delete", "删除", 1, 1));
                    }
                    list.add(new Permission(1020600L, 1020000L, null, "笼位管理", 1, 1));
                    {
                        list.add(new Permission(1020601L, 1020600L, "btn-pc-cage-position-query", "查看", 1, 1));
                        list.add(new Permission(1020602L, 1020600L, "btn-pc-cage-position-add", "新增", 1, 1));
                        list.add(new Permission(1020603L, 1020600L, "btn-pc-cage-position-update", "编辑", 1, 1));
                        list.add(new Permission(1020604L, 1020600L, "btn-pc-cage-position-delete", "删除", 1, 1));
                    }
                    list.add(new Permission(1020700L, 1020000L, null, "系统配置", 1, 1));
                    {
                        list.add(new Permission(1020701L, 1020700L, "btn-pc-robot-config-query", "查看", 1, 1));
                        list.add(new Permission(1020702L, 1020700L, "btn-pc-robot-config-update", "修改", 1, 1));
                    }
                    list.add(new Permission(1020800L, 1020000L, null, "搬运路线管理", 1, 1));
                    {
                        list.add(new Permission(1020801L, 1020800L, "btn-pc-route-query-page", "查看", 1, 1));
                        list.add(new Permission(1020802L, 1020800L, "btn-pc-route-create", "新增", 1, 1));
                        list.add(new Permission(1020803L, 1020800L, "btn-pc-route-update", "编辑", 1, 1));
                        list.add(new Permission(1020804L, 1020800L, "btn-pc-route-delete", "删除", 1, 1));
                    }
                }

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
        {
            // 基础权限
            {
               list.add(new Permission(2010000L, 0L, null, "任务清单", 2, 1));
               {
                  list.add(new Permission(2010100L, 2010000L, "", "查看", 2, 1));
               }
               list.add(new Permission(2020000L, 0L, null, "饲料称重", 2, 1));
                {
                    list.add(new Permission(2020100L, 2020000L, "", "查看", 2, 1));
                    list.add(new Permission(2020101L, 2020000L, "", "新增", 2, 1));
                    list.add(new Permission(2020102L, 2020000L, "", "调整排序", 2, 1));
                }

            }
        }

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
