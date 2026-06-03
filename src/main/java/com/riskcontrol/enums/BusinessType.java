package com.riskcontrol.enums;

import java.util.Arrays;

/**
 * 业务操作类型
 * 常量顺序不能修改，后面增加常量在最后添加!!!
 * businessType().ordinal() 获取枚举常量在代码中定义的顺序: 第一个常量 → 0，第二个 → 1，第三个 → 2，以此类推
 * 操作类型（0其它 1新增 2批量新增 3修改 4批量修改 5删除 6批量删除 7授权 8下载 9批量下载 10导出 11导入 12强退 13生成代码 14清空数据）
 *
 * @author fallrain
 */
public enum BusinessType {
    /**
     * 其它-0
     */
    OTHER(0, "其它"),

    /**
     * 新增-1
     */
    INSERT(1, "新增"),

    /**
     * 批量新增-2
     */
    BATCH_INSERT(2, "批量新增"),

    /**
     * 修改-3
     */
    UPDATE(3, "修改"),

    /**
     * 批量修改-4
     */
    BATCH_UPDATE(4, "批量修改"),

    /**
     * 删除-5
     */
    DELETE(5, "删除"),

    /**
     * 批量删除-6
     */
    BATCH_DELETE(6, "批量删除"),

    /**
     * 授权-7
     */
    GRANT(7, "授权"),

    /**
     * 下载-8
     */
    DOWNLOAD(8, "下载"),

    /**
     * 批量下载-9
     */
    BATCH_DOWNLOAD(9, "批量下载"),

    /**
     * 导出-10
     */
    EXPORT(10, "导出"),

    /**
     * 导入-11
     */
    IMPORT(11, "导入"),

    /**
     * 强退-12
     */
    FORCE(12, "强退"),

    /**
     * 生成代码-13
     */
    GENCODE(13, "生成代码"),

    /**
     * 清空数据-14
     */
    CLEAN(14, "清空数据");

    private final Integer code;
    private final String msg;

    BusinessType(Integer code, String msg) {
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
     * @param code 业务类型编码
     * @return 业务类型枚举
     */
    public static BusinessType getByCode(Integer code) {
        for (BusinessType value : BusinessType.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }

    /**
     * 根据code获取msg
     *
     * @param code 业务类型编码
     * @return 业务类型描述
     */
    public static String getMsgByCode(Integer code) {
        for (BusinessType value : BusinessType.values()) {
            if (value.code.equals(code)) {
                return value.msg;
            }
        }
        return null;
    }

    /**
     * 根据msg获取code
     *
     * @param msg 业务类型描述
     * @return 业务类型编码
     */
    public static Integer getCodeByMsg(String msg) {
        for (BusinessType value : BusinessType.values()) {
            if (value.msg.equals(msg)) {
                return value.code;
            }
        }
        return null;
    }

    /**
     * 根据msg获取枚举名称
     *
     * @param msg 业务类型描述
     * @return 枚举名称
     */
    public static String getKeyByValue(String msg) {
        return Arrays.stream(BusinessType.values())
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
        System.out.println("========== BusinessType 获取Code使用示例 ==========\n");

        // 1. 直接通过枚举实例获取code
        System.out.println("1. 直接通过枚举实例获取code:");
        System.out.println("   BusinessType.INSERT.getCode() = " + BusinessType.INSERT.getCode());
        System.out.println("   BusinessType.UPDATE.getCode() = " + BusinessType.UPDATE.getCode());
        System.out.println("   BusinessType.DELETE.getCode() = " + BusinessType.DELETE.getCode());
        System.out.println("   BusinessType.EXPORT.getCode() = " + BusinessType.EXPORT.getCode());
        System.out.println();

        // 2. 根据msg获取code
        System.out.println("2. 根据msg获取code:");
        System.out.println("   getCodeByMsg('新增') = " + BusinessType.getCodeByMsg("新增"));
        System.out.println("   getCodeByMsg('修改') = " + BusinessType.getCodeByMsg("修改"));
        System.out.println("   getCodeByMsg('删除') = " + BusinessType.getCodeByMsg("删除"));
        System.out.println();

        // 3. 根据code获取枚举，再获取其他信息
        System.out.println("3. 根据code获取枚举，再获取其他信息:");
        BusinessType type = BusinessType.getByCode(10);
        if (type != null) {
            System.out.println("   code=10 -> 枚举: " + type.name() + ", msg: " + type.getMsg());
        }
        System.out.println();

        // 4. 实际业务场景：在@Log注解中使用
        System.out.println("4. 实际业务场景示例:");
        System.out.println("   @Log(title = \"操作日志\", businessType = BusinessType.INSERT)");
        System.out.println("   对应的code值为: " + BusinessType.INSERT.getCode());
        System.out.println();

        System.out.println("5. 遍历所有业务类型及其code:");
        for (BusinessType typeItem : BusinessType.values()) {
            System.out.println("   " + String.format("%-15s", typeItem.name()) + " -> code: " + typeItem.getCode() + ", msg: " + typeItem.getMsg());
        }
        System.out.println();

        System.out.println("========== 示例完成 ==========");
    }
}
