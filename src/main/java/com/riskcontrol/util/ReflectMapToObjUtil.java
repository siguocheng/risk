package com.riskcontrol.util;

import java.lang.reflect.Field;
import java.util.Map;

public class ReflectMapToObjUtil {

    /**
     * 将 map 中 key 与对象字段名相同的 value 赋值到对象字段
     * @param map 数据源map
     * @param target 目标对象
     * @param <T> 目标对象泛型
     */
    public static <T> void mapToObject(Map<String, Object> map, T target) {
        if (map == null || map.isEmpty() || target == null) {
            return;
        }
        // 获取对象所有字段（包括私有）
        Field[] fields = target.getClass().getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            // map 包含当前字段名才赋值
            if (map.containsKey(fieldName)) {
                Object value = map.get(fieldName);
                try {
                    // 突破私有访问限制
                    field.setAccessible(true);
                    // 赋值
                    field.set(target, value);
                } catch (Exception e) {
                    // 类型不匹配/权限异常可自行扩展日志
                    e.printStackTrace();
                }
            }
        }
    }
}
