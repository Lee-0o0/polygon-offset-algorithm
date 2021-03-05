package com.lee.entity;

import java.io.Serializable;
import java.util.Objects;

/** 轮廓上的线段 */
public class LineSegment implements Serializable {
    private Point startPoint;         // 第一个点
    private Point endPoint;           // 第二个点
    private LineSegment next;         // 其连接的下一条线段，即next边的一个点与当前线段的一个点相同

    public LineSegment(Point startPoint, Point endPoint){
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.next = null;
    }

    public LineSegment getNext() {
        return next;
    }

    public void setNext(LineSegment next) {
        this.next = next;
    }

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public Point getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Point endPoint) {
        this.endPoint = endPoint;
    }

    /**
     * point 是否为该线段的端点
     * @param point
     * @return
     */
    public boolean isEndPoint(Point point){
        return this.startPoint.equals(point) || this.endPoint.equals(point);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineSegment that = (LineSegment) o;
        return (Objects.equals(startPoint, that.startPoint) && Objects.equals(endPoint, that.endPoint))||
                (Objects.equals(startPoint, that.endPoint) && Objects.equals(endPoint, that.startPoint));
    }

    @Override
    public int hashCode() {
        return Objects.hash(startPoint, endPoint);
    }

    @Override
    public String toString() {
        return "LineSegment{" +
                "startPoint=" + startPoint +
                ", endPoint=" + endPoint +
                '}';
    }
}
