package com.lee.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 浮点数精确计算 工具类
 */
public class ArithUtil {
    // 除法运算默认精度
    private static final int DEF_DIV_SCALE = 15;

    /**
     * 精确加法
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 精确减法
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 精确乘法
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        return b1.multiply(b2).doubleValue();
    }

    /**
     * 精确除法 使用默认精度
     */
    public static double div(double value1, double value2){
        double res = 0.0;
        try {
            res = div(value1, value2, DEF_DIV_SCALE);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return res;
    }

    /**
     * 精确除法
     * @param scale 精度
     */
    private static double div(double value1, double value2, int scale) throws IllegalAccessException {
        if(scale < 0) {
            throw new IllegalAccessException("精确度不能小于0");
        }
        BigDecimal b1 = BigDecimal.valueOf(value1);
        BigDecimal b2 = BigDecimal.valueOf(value2);
        // return b1.divide(b2, scale).doubleValue();
        return b1.divide(b2, scale, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param scale 小数点后保留几位
     */
    public static double round(double v, int scale) {
        double res = 0.0;
        try {
            res = div(v, 1, scale);
        }catch (IllegalAccessException e){
            e.printStackTrace();
        }
        return res;
    }

}
