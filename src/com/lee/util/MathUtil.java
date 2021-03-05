package com.lee.util;

import com.lee.entity.LineSegment;
import com.lee.entity.Point;
import com.lee.entity.Vector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/** 扩展的数学工具类 */
public class MathUtil {

    /**
     * 从线段中获取该线段所在的直线
     * @param lineSegment
     * @return
     */
    public static double[] getLineFromLineSegment(LineSegment lineSegment){
        double[] line = new double[3];
        Point first = lineSegment.getStartPoint();
        Point second = lineSegment.getEndPoint();
        if (Double.compare(first.getX(),second.getX()) == 0){
            // 该线段垂直于x轴
            line[0] = 1.0;
            line[1] = 0.0;
            line[2] = ArithUtil.mul(-1,first.getX());
        }else if (Double.compare(first.getY(),second.getY()) == 0){
            // 该线段垂直于y轴
            line[0] = 0.0;
            line[1] = 1.0;
            line[2] = ArithUtil.mul(-1,first.getY());
        }else {
            double k = ArithUtil.div(ArithUtil.sub(second.getY(),first.getY()), ArithUtil.sub(second.getX(),first.getX()));
            line[0] = k;
            line[1] = -1;
            line[2] = ArithUtil.sub(first.getY() , ArithUtil.mul(first.getX(),k));
        }

        return line;
    }

    /**
     * 获取两条直线的交点
     * @param line1
     * @param line2
     * @return
     */
    public static Point getIntersectionOfTwoLines(double[] line1, double[] line2){
        // 两直线平行，没有交点
        if (Double.compare(line1[0],0.0) ==0 && Double.compare(line2[0],0.0) ==0){
            return  null;
        }
        if (Double.compare(line1[0], 0.0) != 0 && Double.compare(line2[0], 0.0) != 0) {
            if (Double.compare(ArithUtil.div(line1[1],line1[0]), ArithUtil.div(line2[1],line2[0]))==0){
                return null;
            }
        }
        // 两直线不平行，有交点
        double x = (line1[1]*line2[2] - line2[1]*line1[2])/(line1[0]*line2[1] - line2[0]*line1[1]);
        double y = (line2[0]*line1[2] - line1[0]*line2[2])/(line1[0]*line2[1] - line2[0]*line1[1]);
        Point point = new Point(x,y);
        return point;
    }

    /**
     * 获取两向量的点乘结果
     * @param vector1
     * @param vector2
     * @return
     */
    public static double getMulOfVector(Vector vector1,Vector vector2) {
        double mul1 = ArithUtil.mul(vector1.getX(), vector2.getX());
        double mul2 = ArithUtil.mul(vector1.getY(), vector2.getY());
        return ArithUtil.add(mul1,mul2);
    }

    /**
     * 判断点point是否在轮廓内，射线法
     * @param contour
     * @param point
     * @return true if point is in contour
     */
    public static boolean isInPolygon(List<LineSegment> contour, Point point){
        boolean flag = false;
        // intersectionPoints为y=point.getY()直线与contour的交点
        Set<Point> intersectionPoints = new HashSet<>();
        for (LineSegment lineSegment:contour){
            Point point1 = getPoint(lineSegment, point.getY());
            if (point1!=null){
                intersectionPoints.add(point1);
            }
        }
        int count = 0;
        for (Point p:intersectionPoints){
            if (Double.compare(p.getX(),point.getX()) < 0){
                count++;
            }
        }

        // 是奇数，在轮廓里
        if (count%2!=0){
            flag = true;
        }
        return flag;
    }

    /**
     * 知道一个点的y坐标，获取该点坐标
     * @param lineSegment
     * @param y
     * @return
     */
    public static Point getPoint(LineSegment lineSegment, double y){
        Point first = lineSegment.getStartPoint();
        Point second = lineSegment.getEndPoint();
        double x1 = first.getX();
        double y1 = first.getY();
        double x2 = second.getX();
        double y2 = second.getY();

        if (Double.compare(y,y1) < 0 && Double.compare(y,y2) < 0){
            return null;
        }
        if (Double.compare(y,y1) > 0 && Double.compare(y,y2) > 0){
            return null;
        }
        if (Double.compare(y1,y2)==0 ){
            return null;
        }

        double a = ArithUtil.div(ArithUtil.mul(ArithUtil.sub(y, y1), ArithUtil.sub(x2, x1)), ArithUtil.sub(y2, y1));
        double x = ArithUtil.add(a,x1);

        Point point = new Point(x,y);
        return point;
    }

    /**
     * 返回A-->B的单位向量
     * @param A
     * @param B
     * @return
     */
    public static Vector getNormalVectorFromTwoPoints(Point A, Point B){
//        System.out.println("A="+A+"  B="+B);
        Vector vector = new Vector();
        vector.setX(ArithUtil.sub(B.getX(),A.getX()));
        vector.setY(ArithUtil.sub(B.getY(),A.getY()));
        vector.normalize();
        return vector;
    }

    /**
     * 获取两个点之间的距离
     * @param point1
     * @param point2
     * @return
     */
    public static double getDistanceBetweenTwoPoints(Point point1, Point point2){
        double x1 = point1.getX();
        double y1 = point1.getY();
        double x2 = point2.getX();
        double y2 = point2.getY();

        return Math.sqrt(Math.pow(ArithUtil.sub(x1,x2),2)+ Math.pow(ArithUtil.sub(y1,y2),2));
    }

    /**
     * 从两个点获取直线方程
     *
     * @param one
     * @param two
     * @return
     */
    public static double[] getLineFromTwoPoints(Point one, Point two) {
        double x1 = one.getX();
        double y1 = one.getY();
        double x2 = two.getX();
        double y2 = two.getY();

        double[] line = new double[3];
        line[0] = y2 - y1;
        line[1] = x1 - x2;
        line[2] = y1 * (x2 - x1) - x1 * (y2 - y1);

        return line;
    }

}
