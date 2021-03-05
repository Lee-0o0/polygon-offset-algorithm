package com.lee.test;

import com.lee.algorithm.OffsetAlgorithm;
import com.lee.display.MainWindow;
import com.lee.entity.Point;
import com.lee.util.FileUtil;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class OffsetTest {

    @Test
    public void testTriangle() throws IOException, InterruptedException {
        offsetTest("file/triangle.txt",3.0,10);
        TimeUnit.HOURS.sleep(1);
    }

    @Test
    public void testRectangle() throws IOException, InterruptedException {
        offsetTest("file/rectangle.txt",3.0,20);
        TimeUnit.HOURS.sleep(1);
    }

    @Test
    public void testStar() throws IOException, InterruptedException {
        offsetTest("file/star.txt",3.0,10);
        TimeUnit.HOURS.sleep(1);
    }

    @Test
    public void testTunnel() throws IOException, InterruptedException {
        offsetTest("file/tunnel.txt",3.0,25);
        TimeUnit.HOURS.sleep(1);
    }

    @Test
    public void test5Edge() throws IOException, InterruptedException {
        offsetTest("file/5edge.txt",8.0,10);
        TimeUnit.HOURS.sleep(1);
    }

    public void offsetTest(String file, double distance,int layers) throws IOException {
        // 读取文件，得到多边形轮廓点列表
        List<Point> points = FileUtil.readPoints(file);
        List<List<Point>> contours = new ArrayList<>();
        contours.add(points);
        // 逐层获取内缩轮廓
        for(int i = 1; i <= layers ; i++) {
            List<List<Point>> lists = OffsetAlgorithm.offsetAlgorithm(points, i * distance);
            if (lists!=null){
                contours.addAll(lists);
            }
        }
        // 显示结果
        MainWindow mainWindow = new MainWindow(contours);
        mainWindow.show();
    }
}
