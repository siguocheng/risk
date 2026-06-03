package com.riskcontrol.common;

import java.util.Arrays;

/**
 * 操作人类别
 * 常量顺序不能修改，后面增加常量在最后添加!!!
 * 操作类别（0其它 1后台用户 2手机端用户）
 *
 * @author fallrain
 */
public enum OperatorType {
    /**
     * 其它-0
     */
    OTHER(0, "其它"),

    /**
     * 后台用户-1
     */
    MANAGE(1, "后台用户"),

    /**
     * 手机端用户-2
     */
    MOBILE(2, "手机端用户");

    private final Integer code;
    private final String msg;

    OperatorType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }

    /**
     * 根据code获取枚举
     *
     * @param code 操作人类别编码
     * @return 操作人类别枚举
     */
    public static OperatorType getByCode(Integer code) {
        for (OperatorType value : OperatorType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据code获取msg
     *
     * @param code 操作人类别编码
     * @return 操作人类别描述
     */
    public static String getMsgByCode(Integer code) {
        for (OperatorType value : OperatorType.values()) {
            if (value.code.equals(code)) {
                return value.msg;
            }
        }
        return null;
    }

    /**
     * 根据msg获取code
     *
     * @param msg 操作人类别描述
     * @return 操作人类别编码
     */
    public static Integer getCodeByMsg(String msg) {
        for (OperatorType value : OperatorType.values()) {
            if (value.msg.equals(msg)) {
                return value.code;
            }
        }
        return null;
    }

    /**
     * 根据msg获取枚举名称
     *
     * @param msg 操作人类别描述
     * @return 枚举名称
     */
    public static String getKeyByValue(String msg) {
        return Arrays.stream(OperatorType.values())
                .filter(e -> e.getMsg().equals(msg))
                .findFirst()
                .map(Enum::name)
                .orElse(null);
    }

    /**
     * 测试用例 - 演示如何获取和使用code
     *
     * @param args 参数
     */
    public static void main(String[] args) {
        System.out.println("========== OperatorType 获取Code使用示例 ==========\n");

        // 1. 直接通过枚举实例获取code
        System.out.println("1. 直接通过枚举实例获取code:");
        System.out.println("   OperatorType.OTHER.getCode() = " + OperatorType.OTHER.getCode());
        System.out.println("   OperatorType.MANAGE.getCode() = " + OperatorType.MANAGE.getCode());
        System.out.println("   OperatorType.MOBILE.getCode() = " + OperatorType.MOBILE.getCode());
        System.out.println();

        // 2. 根据msg获取code
        System.out.println("2. 根据msg获取code:");
        System.out.println("   getCodeByMsg('其它') = " + OperatorType.getCodeByMsg("其它"));
        System.out.println("   getCodeByMsg('后台用户') = " + OperatorType.getCodeByMsg("后台用户"));
        System.out.println("   getCodeByMsg('手机端用户') = " + OperatorType.getCodeByMsg("手机端用户"));
        System.out.println();

        // 3. 根据code获取枚举，再获取其他信息
        System.out.println("3. 根据code获取枚举，再获取其他信息:");
        OperatorType type = OperatorType.getByCode(1);
        if (type != null) {
            System.out.println("   code=1 -> 枚举: " + type.name() + ", msg: " + type.getMsg());
        }
        System.out.println();

        // 4. 实际业务场景：在@Log注解中使用
        System.out.println("4. 实际业务场景示例:");
        System.out.println("   @Log(title = \"操作日志\", operatorType = OperatorType.MANAGE)");
        System.out.println("   对应的code值为: " + OperatorType.MANAGE.getCode());
        System.out.println();

        System.out.println("5. 遍历所有操作人类别及其code:");
        for (OperatorType typeItem : OperatorType.values()) {
            System.out.println("   " + String.format("%-15s", typeItem.name()) + " -> code: " + typeItem.getCode() + ", msg: " + typeItem.getMsg());
        }
        System.out.println();

        System.out.println("========== 示例完成 ==========");
    }
}
