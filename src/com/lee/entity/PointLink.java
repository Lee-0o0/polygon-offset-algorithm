package com.lee.entity;

public class PointLink {
    private Point point;
    private Boolean intersection;
    private PointLink next;

    public PointLink() {
    }

    public PointLink(Point point, PointLink next, Boolean intersection) {
        this.point = point;
        this.next = next;
        this.intersection = intersection;
    }

    public Point getPoint() {
        return point;
    }

    public void setPoint(Point point) {
        this.point = point;
    }

    public PointLink getNext() {
        return next;
    }

    public void setNext(PointLink next) {
        this.next = next;
    }

    public Boolean getIntersection() {
        return intersection;
    }

    public void setIntersection(Boolean intersection) {
        this.intersection = intersection;
    }
}
