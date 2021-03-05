package com.lee.display;

import com.lee.entity.Point;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MainWindow {
    private JFrame frame;
    private DrawingBoard drawingBoard;

    public MainWindow(List<List<Point>> contours){
        frame = new JFrame("polygon offset algorithm display");
        frame.setSize(600, 600);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        drawingBoard = new DrawingBoard(contours);
        drawingBoard.setBackground(Color.WHITE);
        frame.add(drawingBoard);
    }

    public DrawingBoard getDrawingBoard() {
        return drawingBoard;
    }

    public void setDrawingBoard(DrawingBoard drawingBoard) {
        this.drawingBoard = drawingBoard;
    }

    public void show(){
        frame.setVisible(true);
    }
}
