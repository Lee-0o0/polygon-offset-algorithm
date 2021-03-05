package com.lee.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * 点
 */
public class Point implements Serializable {
    double x;
    double y;

    public Point(double x,double y){
        BigDecimal bigDecimalx = new BigDecimal(x);
        this.x = bigDecimalx.setScale(6, RoundingMode.HALF_UP).doubleValue();
        BigDecimal bigDecimaly = new BigDecimal(y);
        this.y = bigDecimaly.setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        BigDecimal bigDecimalx = new BigDecimal(x);
        this.x = bigDecimalx.setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        BigDecimal bigDecimaly = new BigDecimal(y);
        this.y = bigDecimaly.setScale(6, RoundingMode.HALF_UP).doubleValue();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
//        return Double.compare(point.x, x) == 0 &&
//                Double.compare(point.y, y) == 0;
        return Double.compare(Math.abs(point.x-x) , 1e-5)<0 && Double.compare(Math.abs(point.y-y),1e-5)<0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "Point{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
