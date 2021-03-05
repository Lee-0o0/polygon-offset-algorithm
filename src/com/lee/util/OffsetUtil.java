package com.lee.util;

import com.lee.entity.LineSegment;
import com.lee.entity.Point;

/**
 * 获取两条线段的交点
 */
public class OffsetUtil {
     double[] ans = new double[0];

    public Point intersection(LineSegment lineSegment1, LineSegment lineSegment2){
        Point first = lineSegment1.getStartPoint();
        Point second = lineSegment1.getEndPoint();
        Point first1 = lineSegment2.getStartPoint();
        Point second1 = lineSegment2.getEndPoint();

        double[] start1 = new double[]{first.getX(),first.getY()};
        double[] end1 = new double[]{second.getX(),second.getY()};
        double[] start2 = new double[]{first1.getX(),first1.getY()};
        double[] end2 = new double[]{second1.getX(),second1.getY()};

        double[] intersection = intersection(start1, end1, start2, end2);

        if (intersection.length == 0){
            return null;
        }
        return new Point(intersection[0],intersection[1]);
    }

    public  double[] intersection(double[] start1, double[] end1, double[] start2, double[] end2) {
        double x1 = start1[0], y1 = start1[1];
        double x2 = end1[0], y2 = end1[1];
        double x3 = start2[0], y3 = start2[1];
        double x4 = end2[0], y4 = end2[1];

        // 判断 (x1, y1)~(x2, y2) 和 (x3, y3)~(x4, y4) 是否平行
        if ((y4 - y3) * (x2 - x1) == (y2 - y1) * (x4 - x3)) {
            // 若平行，则判断 (x3, y3) 是否在「直线」(x1, y1)~(x2, y2) 上
            if ((y2 - y1) * (x3 - x1) == (y3 - y1) * (x2 - x1)) {
                // 判断 (x3, y3) 是否在「线段」(x1, y1)~(x2, y2) 上
                if (inside(x1, y1, x2, y2, x3, y3)) {
                    update(x3, y3);
                }
                // 判断 (x4, y4) 是否在「线段」(x1, y1)~(x2, y2) 上
                if (inside(x1, y1, x2, y2, x4, y4)) {
                    update(x4, y4);
                }
                // 判断 (x1, y1) 是否在「线段」(x3, y3)~(x4, y4) 上
                if (inside(x3, y3, x4, y4, x1, y1)) {
                    update(x1, y1);
                }
                // 判断 (x2, y2) 是否在「线段」(x3, y3)~(x4, y4) 上
                if (inside(x3, y3, x4, y4, x2, y2)) {
                    update(x2, y2);
                }
            }
            // 在平行时，其余的所有情况都不会有交点
        } else {
            // 联立方程得到 t1 和 t2 的值
            double t1 =
                    (double) (x3 * (y4 - y3) + y1 * (x4 - x3) - y3 * (x4 - x3) - x1 * (y4 - y3))
                            / ((x2 - x1) * (y4 - y3) - (x4 - x3) * (y2 - y1));
            double t2 =
                    (double) (x1 * (y2 - y1) + y3 * (x2 - x1) - y1 * (x2 - x1) - x3 * (y2 - y1))
                            / ((x4 - x3) * (y2 - y1) - (x2 - x1) * (y4 - y3));
            // 判断 t1 和 t2 是否均在 [0, 1] 之间
            if (t1 >= 0.0 && t1 <= 1.0 && t2 >= 0.0 && t2 <= 1.0) {
                ans = new double[] {x1 + t1 * (x2 - x1), y1 + t1 * (y2 - y1)};
            }
        }
        return ans;
    }

    // 判断 (xk, yk) 是否在「线段」(x1, y1)~(x2, y2) 上
    // 这里的前提是 (xk, yk) 一定在「直线」(x1, y1)~(x2, y2) 上
    public  boolean inside(double x1, double y1, double x2, double y2, double xk, double yk) {
        // 若与 x 轴平行，只需要判断 x 的部分
        // 若与 y 轴平行，只需要判断 y 的部分
        // 若为普通线段，则都要判断
        return (x1 == x2 || (Math.min(x1, x2) <= xk && xk <= Math.max(x1, x2)))
                && (y1 == y2 || (Math.min(y1, y2) <= yk && yk <= Math.max(y1, y2)));
    }

    public  void update(double xk, double yk) {
        // 将一个交点与当前 ans 中的结果进行比较
        // 若更优则替换
        if (ans.length == 0 || xk < ans[0] || (xk == ans[0] && yk < ans[1])) {
            ans = new double[] {xk, yk};
        }
    }
}
