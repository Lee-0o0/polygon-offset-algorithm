package com.lee.util;

import com.lee.entity.Point;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {
    /**
     * 从路径file中读取多边形轮廓点
     * @param file
     * @return
     */
    public static List<Point> readPoints(String file) throws IOException {
        List<Point> polygon = new ArrayList<>();

        BufferedReader fileReader = new BufferedReader(new FileReader(file));
        String content;
        while ((content = fileReader.readLine()) != null){
            String[] strings = content.split(",");
            polygon.add(new Point(Double.valueOf(strings[0]),Double.valueOf(strings[1])));
        }

        return polygon;
    }
}
