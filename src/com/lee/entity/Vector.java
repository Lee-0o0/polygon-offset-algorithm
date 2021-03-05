package com.lee.entity;

import com.lee.util.ArithUtil;

/**
 * 向量
 */
public class Vector {
    private double x;
    private double y;

    public Vector() {
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
        if (Double.compare(Math.abs(this.x),1e-10)<=0){
            this.x = 0;
        }
        if (Double.compare(Math.abs(this.y),1e-10)<=0){
            this.y = 0;
        }
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
        if (Double.compare(Math.abs(this.x),1e-10)<=0){
            this.x = 0;
        }
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
        if (Double.compare(Math.abs(this.y),1e-10)<=0){
            this.y = 0;
        }
    }

    /**
     * 获取向量的长度
     * @return
     */
    public double getLength(){
        return Math.sqrt(ArithUtil.add(Math.pow(x,2), Math.pow(y,2)));
    }

    /**
     * 向量归一化
     */
    public void normalize(){
        double length = this.getLength();
        this.x = ArithUtil.div(this.x,length);
        this.y = ArithUtil.div(this.y,length);
    }

    /**
     * 向量乘以一个常数t
     * @param t
     */
    public void mul(double t){
        this.x = ArithUtil.mul(this.x,t);
        this.y = ArithUtil.mul(this.y,t);
    }

    @Override
    public String toString() {
        return "Vector{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }
}
