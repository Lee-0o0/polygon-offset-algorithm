package com.lee.display;

import com.lee.entity.Point;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class DrawingBoard extends JPanel {

    private List<List<Point>> contours ;

    public DrawingBoard(){
        this.contours = null;
    }

    public DrawingBoard(List<List<Point>> contours){
        this.contours = contours;
    }

    public List<List<Point>> getContours() {
        return contours;
    }

    public void setContours(List<List<Point>> contours) {
        this.contours = contours;
    }

    /**
     * 画图主要函数
     * @param g
     */
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        for (List<Point> contour:this.contours){
            drawContour(contour,g);
        }
    }

    public void drawContour(List<Point> contour,Graphics g){
        for (int i = 0; i < contour.size(); i++){
            int next = (i+1)%contour.size();
            g.drawLine((int)contour.get(i).getX(),(int)contour.get(i).getY(),
                    (int)contour.get(next).getX(),(int)contour.get(next).getY());
        }
    }
}
