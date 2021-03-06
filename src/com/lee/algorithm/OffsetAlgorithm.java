package com.lee.algorithm;

import com.lee.entity.LineSegment;
import com.lee.entity.Point;
import com.lee.entity.PointLink;
import com.lee.entity.Vector;
import com.lee.util.ArithUtil;
import com.lee.util.MathUtil;
import com.lee.util.OffsetUtil;

import java.util.*;

public class OffsetAlgorithm {

    /**
     * 生成缩进轮廓算法
     * @param points 闭合轮廓
     * @param distance 缩进距离,如果distance>0，向轮廓里缩进；如果distance<0，外扩
     * @return
     */
    public static List<List<Point>> offsetAlgorithm(List<Point> points, double distance){
        if (points == null || points.size() < 3 || Double.compare(distance,0.0) == 0){
            return null;
        }

        // 将points中的重复点删除，保持顺序
        LinkedHashSet<Point> linkedHashSet1 = new LinkedHashSet<>(points);
        points = new ArrayList<>(linkedHashSet1);

        // 计算缩进方向
        Map<String, List<com.lee.entity.Vector>> direction = getDirection(points);
        List<com.lee.entity.Vector> in = direction.get("in");

        // 确定内缩点
        List<Point> inShell = getInfillPoints(points, in, distance);

        // 确定每条缩进线段的有效性
        List<Boolean> validOffsetLine = isValidOffsetLine(points, inShell);

        // 是否访问
        boolean[] isVisited = new boolean[validOffsetLine.size()];
        Arrays.fill(isVisited, false);

        // 无效内缩线段的处理
        List<Point> finalInshell = new ArrayList<>();
        for (int i = 0; i < inShell.size(); ) {
            if (validOffsetLine.get(i)) {
                // 以i点开头的线段是有效边
                finalInshell.add(inShell.get(i));
                isVisited[i] = true;
                i++;
            } else {
                // 以i点开头的线段是无效边
                if (isVisited[i]) {
                    // 访问过了，不再处理
                    continue;
                }
                // 确定case1 或 case2
                int backIndex = (i - 1 + validOffsetLine.size()) % validOffsetLine.size();
                while (!validOffsetLine.get(backIndex)) {
                    backIndex =
                            (backIndex - 1 + validOffsetLine.size()) % validOffsetLine.size();
                    if (backIndex == i) {
//                        System.out.println("backward:all invalid offset lines");
                        break;
                    }
                }
                int current = i;
                int next = (i + 1) % validOffsetLine.size();
                while (!validOffsetLine.get(next)) {
                    next = (next + 1) % validOffsetLine.size();
                    if (next == i) {
//                        System.out.println("forward:all invalid offset lines");
                        break;
                    }
                }
//                System.out.println("back = " + backIndex + ", i = " + i + ", next = " + next);
                // 根据case 1 和case 2寻找交点
                if (next == backIndex) {
                    // all edges are invalid edges
                    return null;
//                    break;
                } else if (next - backIndex == 2
                        || (next == 1 && backIndex == validOffsetLine.size() - 1)
                        || (next == 0 && backIndex == validOffsetLine.size() - 2)) {
                    // case 1 只有一条无效边
                    // 1. 计算backward edge 和forward edge的交点
                    double[] backwardEdge =
                            MathUtil.getLineFromTwoPoints(
                                    inShell.get(backIndex), inShell.get(current));
                    double[] forwardEdge =
                            MathUtil.getLineFromTwoPoints(
                                    inShell.get(next),
                                    inShell.get((next + 1) % validOffsetLine.size()));

                    Point intersection =
                            MathUtil.getIntersectionOfTwoLines(backwardEdge, forwardEdge);
//                    System.out.println("intersection=" + intersection);
                    // 2. 更新数据
                    if (next == 0) {
                        finalInshell.set(next, intersection);
                    } else {
                        inShell.set(next, intersection);
                    }
                    isVisited[i] = true;
                    i++;
                } else {
                    // case 2: 有连续的无效边
                    LineSegment backwardEdge =
                            new LineSegment(inShell.get(backIndex), inShell.get(current));
                    LineSegment forwardEdge =
                            new LineSegment(
                                    inShell.get(next),
                                    inShell.get((next + 1) % inShell.size()));
                    // 计算backward和forward与轮廓是否有交点

                    for (int j = current; j != next; j = (j + 1) % validOffsetLine.size()) {
                        isVisited[j] = true;
                        LineSegment boundaryEdge =
                                new LineSegment(
                                        points.get(j), points.get((j + 1) % points.size()));
//                        System.out.println("j=" + j + ",j_next=" + (j + 1) % points.size());
//                        System.out.println("boundary edge=" + boundaryEdge);

                        // 计算boundary的pair-wise offset
                        // 1. 偏移方向
                        com.lee.entity.Vector normalVector = getNormalVector(boundaryEdge);
                        if (Double.compare(MathUtil.getMulOfVector(normalVector, in.get(j)), 0)
                                < 0) {
                            normalVector.mul(-1);
                        }
                        normalVector.normalize();
                        normalVector.mul(3 * distance);
//                        System.out.println("偏移方向=" + normalVector);
                        // 2. 计算偏移线段
                        Point first =
                                new Point(
                                        boundaryEdge.getStartPoint().getX() + normalVector.getX(),
                                        boundaryEdge.getStartPoint().getY() + normalVector.getY());
                        Point second =
                                new Point(
                                        boundaryEdge.getEndPoint().getX() + normalVector.getX(),
                                        boundaryEdge.getEndPoint().getY() + normalVector.getY());
                        LineSegment offsetEdgeOfPairwise = new LineSegment(first, second);
                        //                            drawLineSegment(offsetEdgeOfPairwise,g);
                        // 计算偏移线段与backward、forward的交点，并更新
                        // 此处应该计算偏移线段offsetEdgeOfPairwise 与所有有效偏移边的交点
                        // 不只是backward 和 forward
                        //
                        // System.out.println("offsetEdge="+offsetEdgeOfPairwise+",
                        // backwardEdge="+backwardEdge);

                        Point point2 = new OffsetUtil().intersection(offsetEdgeOfPairwise, backwardEdge);
//                        Point point2 =
//                                MathUtil.intersectionOfTwoLineSegment(
//                                        offsetEdgeOfPairwise, backwardEdge);
                        //                            System.out.println(point2);
                        if (point2 != null) {
                            backwardEdge.setEndPoint(point2);
                        }

                        // System.out.println("offsetEdge="+offsetEdgeOfPairwise+",
                        // forwardEdge="+forwardEdge);
                        Point point1 =new OffsetUtil().intersection(offsetEdgeOfPairwise, forwardEdge);
                        //                            System.out.println(point1);
                        if (point1 != null) {
                            forwardEdge.setStartPoint(point1);
                        }
                    }
                    // 所有的无效边处理完成后
                    // 计算backward 和forward的交点
//                    System.out.println(
//                            "backwardEdge=" + backwardEdge + ",forwardEdge=" + forwardEdge);
                    Point point =new OffsetUtil().intersection(backwardEdge, forwardEdge);
                    Point preForwardStartPoint = forwardEdge.getStartPoint();
                    if (point != null) {
                        backwardEdge.setEndPoint(point);
                        forwardEdge.setStartPoint(point);
                    }

                    finalInshell.add(backwardEdge.getEndPoint());

                    if (next < current) {
                        i = inShell.size();
                        if (point != null) {
                            for (int k = 0; k < finalInshell.size(); k++){
                                if (finalInshell.get(k).equals(preForwardStartPoint)){
                                    finalInshell.set(k, point);
                                    break;
                                }
                            }
                        }
                    } else {
                        inShell.set(next, forwardEdge.getStartPoint());
                        i = next;
                    }
                }
            }
        }

        // 全局无效环的处理
        if (finalInshell.size() >= 3 ) {
            // 将finalInshell中处于轮廓外的点消除
            List<Point> finalFinalInshell = new ArrayList<>();
            List<LineSegment> contour = new ArrayList<>();
            for (int m = 0; m < points.size(); m++){
                LineSegment lineSegment = new LineSegment(points.get(m), points.get((m + 1) % points.size()));
                contour.add(lineSegment);
            }

            for (Point point: finalInshell){
                if (point == null){
                    continue;
                }
                if (Double.compare(distance, 0.0) > 0) {
                    // 向内缩进
                    if (MathUtil.isInPolygon(contour, point)) {
                        finalFinalInshell.add(point);
                    }
                }else if (Double.compare(distance,0.0) < 0){
                    // 向外扩张
                    if (!MathUtil.isInPolygon(contour, point)) {
                        finalFinalInshell.add(point);
                    }
                }
            }
            finalInshell = finalFinalInshell;
            // finalInshell 可能有相同的点，去除重复点
            LinkedHashSet<Point> linkedHashSet = new LinkedHashSet<>(finalInshell);
            finalInshell = new ArrayList<>(linkedHashSet);

            // 消除全局无效环
            // 1.获取所有的自交点
            Map<Point, List<Point>> intersection = getIntersection(finalInshell);

            if (intersection.size() == 0) {
                // 如果没有自交点，说明不存在全局无效环，不用处理
                List<List<Point>> res = new ArrayList<>();
                res.add(finalInshell);
                return res;
            } else {
                // 有自交点，需要消除全局无效环
                List<Point> allPoints = new ArrayList<>();
                List<Boolean> isIntersectionPoint = new ArrayList<>();
                for (int i = 0; i < finalInshell.size(); i++) {
                    allPoints.add(finalInshell.get(i));
                    isIntersectionPoint.add(false);
                    if (intersection.get(finalInshell.get(i)) != null) {
                        for (int j = 0; j < intersection.get(finalInshell.get(i)).size(); j++) {
                            allPoints.add(intersection.get(finalInshell.get(i)).get(j));
                            isIntersectionPoint.add(true);
                        }
                    }
                }

                List<List<Point>> lists =
                        processGlobalInvalidLoop(points, allPoints, isIntersectionPoint);
                return lists;
            }
        }
        return null;
    }

    /**
     * 消除全局无效环
     *
     * @param contour 原轮廓
     * @param allPoints 包含自交点的缩进轮廓
     * @param isIntersectionPoint 判断allPoints是否为自交点
     * @return
     */
    private static List<List<Point>> processGlobalInvalidLoop(
            List<Point> contour, List<Point> allPoints, List<Boolean> isIntersectionPoint) {
        // 判断原轮廓的方向
        boolean isClockwiseOfContour = isClockwise(contour);
        // 判断该点是否被访问过
        boolean[] isVisited = new boolean[allPoints.size()];
        Arrays.fill(isVisited, false);
        // 任选一个起始点
        Point start = null;
        int indexOfStartPoint = -1;
        for (int i = 0; i < allPoints.size(); i++) {
            if (!isIntersectionPoint.get(i)) {
                start = allPoints.get(i);
                indexOfStartPoint = i;
                break;
            }
        }

        Stack<Point> stack = new Stack<>();
        Stack<Integer> indexOfStartPointStack = new Stack<>();
        stack.push(start);
        indexOfStartPointStack.push(indexOfStartPoint);

        List<List<Point>> res = new ArrayList<>();

        while (!stack.isEmpty()) {
            Point startPoint = stack.pop();
            int index = indexOfStartPointStack.pop();
            isVisited[index] = true;

            List<Point> loop = new ArrayList<>();
            loop.add(startPoint);

            Point nextPoint = allPoints.get((index + 1) % allPoints.size());
            int indexOfNextPoint = (index + 1) % allPoints.size();
            while (!nextPoint.equals(startPoint)) {
                if (isVisited[indexOfNextPoint]) {
                    // 重复访问某点，则表示生成的轮廓有问题，直接返回null
                    return null;
                } else {
                    if (isIntersectionPoint.get(indexOfNextPoint) ) {
                        // nextPoint是自相交的点
                        stack.push(nextPoint);
                        indexOfStartPointStack.push(indexOfNextPoint);
                        // 切换到另一个方向
                        for (int k = 0; k < allPoints.size(); k++) {
                            if (nextPoint.equals(allPoints.get(k))) {
                                if (k != indexOfNextPoint) {
                                    isVisited[k] = true;
                                    loop.add(allPoints.get(k));
                                    nextPoint = allPoints.get((k + 1) % allPoints.size());
                                    indexOfNextPoint = (k + 1) % allPoints.size();
                                    break;
                                }
                            }
                        }
                    } else {
                        // nextPoint 不是自相交的点
                        loop.add(nextPoint);
                        isVisited[indexOfNextPoint] = true;
                        nextPoint = allPoints.get((indexOfNextPoint + 1) % allPoints.size());
                        indexOfNextPoint = (indexOfNextPoint + 1) % allPoints.size();
                    }
                }
            }

            if (isClockwise(loop) == isClockwiseOfContour && loop.size() >= 3) {
                res.add(loop);
            }
        }
        return res;
    }

    /**
     * 获取所有的自交点
     *
     * @param points
     * @return
     */
    private static Map<Point, List<Point>> getIntersection(List<Point> points) {
        Map<Point, List<Point>> intersections = new HashMap<>();

        for (int i = 0; i < points.size(); i++) {
            LineSegment lineSegment =
                    new LineSegment(points.get(i), points.get((i + 1) % points.size()));

            for (int j = 0; j < points.size(); j++) {
                if (Math.abs(i-j) > 1) {
                    LineSegment a =
                            new LineSegment(points.get(j), points.get((j + 1) % points.size()));
                    OffsetUtil offsetUtil = new OffsetUtil();
                    Point point =  null;
                    if (i < j ){
                        point = offsetUtil.intersection(lineSegment, a);
                    }else {
                        point = offsetUtil.intersection(a,lineSegment);
                    }

                    if (point != null) {
                        if (!point.equals(lineSegment.getStartPoint())
                                && !point.equals(lineSegment.getEndPoint())
                                && !point.equals(a.getStartPoint())
                                && !point.equals(a.getEndPoint())) {

                            if (intersections.get(points.get(i)) != null){
                                intersections.get(points.get(i)).add(point);
                            }else {
                                List<Point> intersection = new ArrayList<>();
                                intersection.add(point);
                                intersections.put(points.get(i), intersection);
                            }

                            if (intersections.get(points.get(j)) != null){
                                intersections.get(points.get(j)).add(point);
                            }else {
                                List<Point> intersection = new ArrayList<>();
                                intersection.add(point);
                                intersections.put(points.get(j), intersection);
                            }
                        }
                    }
                }
            }
        }

        // 去除重复的点
        for (Point point:intersections.keySet()){
            LinkedHashSet<Point> linkedHashSet = new LinkedHashSet<>(intersections.get(point));
            intersections.put(point, new ArrayList<>(linkedHashSet));
        }
        // 自交点排序
        for (Point point : intersections.keySet()) {
            List<Point> pointList = intersections.get(point);
            Collections.sort(
                    pointList,
                    new Comparator<Point>() {
                        @Override
                        public int compare(Point o1, Point o2) {
                            double distance1 = MathUtil.getDistanceBetweenTwoPoints(point, o1);
                            double distance2 = MathUtil.getDistanceBetweenTwoPoints(point, o2);
                            return Double.compare(distance1, distance2);
                        }
                    });
        }

        return intersections;
    }

    private static PointLink toPointLink(List<Point> points) {
        List<PointLink> pointLinks = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            PointLink pointLink = new PointLink();
            pointLink.setPoint(points.get(i));
            pointLink.setIntersection(false);
            pointLinks.add(pointLink);
        }

        for (int i = 0; i < pointLinks.size(); i++) {
            pointLinks.get(i).setNext(pointLinks.get((i + 1) % pointLinks.size()));
        }

        return pointLinks.get(0);
    }

    /**
     * 判断多边形是否为顺时针方向
     * https://stackoverflow.com/questions/1165647/how-to-determine-if-a-list-of-polygon-points-are-in-clockwise-order
     *
     * @param contour
     * @return true if contour is in clockwise
     */
    private static boolean isClockwise(List<Point> contour) {
        double sum = 0;
        for (int i = 0; i < contour.size(); i++) {
            int next = (i + 1) % contour.size();

            double x1 = contour.get(i).getX();
            double y1 = contour.get(i).getY();
            double x2 = contour.get(next).getX();
            double y2 = contour.get(next).getY();

            sum += (x2 - x1) * (y1 + y2);
        }

        if (Double.compare(sum, 0) > 0) {
            return true;
        }
        return false;
    }

    /**
     * 获取一条线段的法线方向
     *
     * @param lineSegment
     * @return
     */
    private static com.lee.entity.Vector getNormalVector(LineSegment lineSegment) {
        double[] line = MathUtil.getLineFromLineSegment(lineSegment);
        com.lee.entity.Vector vector = new com.lee.entity.Vector();
        vector.setX(line[0]);
        vector.setY(line[1]);
        return vector;
    }

    /**
     * 确定缩进轮廓线是否有效
     *
     * @param points 原轮廓线中的点
     * @param offset 缩进点
     * @return res.get(i)表示 （i，i+1)线是否有效，true表示有效
     */
    private static List<Boolean> isValidOffsetLine(List<Point> points, List<Point> offset) {
        List<Boolean> res = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            int next = (i + 1) % points.size();
            com.lee.entity.Vector originalVector =
                    MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(next));
            com.lee.entity.Vector offsetVector =
                    MathUtil.getNormalVectorFromTwoPoints(offset.get(i), offset.get(next));
            double mul = MathUtil.getMulOfVector(originalVector, offsetVector);
            if (Double.compare(mul, 0) == -1) {
                res.add(false);
            } else {
                res.add(true);
            }
        }

        return res;
    }

    /**
     * 获取轮廓的内缩点
     *
     * @param points
     * @param in
     * @param distance
     * @return
     */
    private static List<Point> getInfillPoints(
            List<Point> points, List<com.lee.entity.Vector> in, double distance) {
        List<Point> inPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            inPoints.add(
                    new Point(
                            points.get(i).getX() + in.get(i).getX() * distance,
                            points.get(i).getY() + in.get(i).getY() * distance));
        }
        return inPoints;
    }

    /**
     * 获取轮廓的外扩点
     *
     * @param points 原轮廓点
     * @param out 外扩方向
     * @param distance 外扩距离
     * @return 外扩点
     */
    private static List<Point> getExtendPoints(
            List<Point> points, List<com.lee.entity.Vector> out, double distance) {
        List<Point> outPoints = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            outPoints.add(
                    new Point(
                            points.get(i).getX() + out.get(i).getX() * distance,
                            points.get(i).getY() + out.get(i).getY() * distance));
        }
        return outPoints;
    }

    /**
     * 确定一个轮廓向内和向外的缩进方向
     *
     * @param points
     * @return map.get("in") 向内缩进的方向，map.get("out") 外扩的方向
     */
    private static Map<String, List<com.lee.entity.Vector>> getDirection(List<Point> points) {
        Map<String, List<com.lee.entity.Vector>> map = new HashMap<>();

        List<com.lee.entity.Vector> in = new ArrayList<>();
        List<com.lee.entity.Vector> out = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {

            com.lee.entity.Vector vector1;
            com.lee.entity.Vector vector2;
            if (i == 0) {
                vector1 =
                        MathUtil.getNormalVectorFromTwoPoints(
                                points.get(i), points.get(points.size() - 1));
                vector2 = MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(i + 1));
            } else if (i == points.size() - 1) {
                vector1 = MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(i - 1));
                vector2 = MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(0));
            } else {
                vector1 = MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(i - 1));
                vector2 = MathUtil.getNormalVectorFromTwoPoints(points.get(i), points.get(i + 1));
            }
            // 计算缩进方向
            com.lee.entity.Vector vector = new com.lee.entity.Vector();
            vector.setX(ArithUtil.add(vector1.getX(), vector2.getX()));
            vector.setY(ArithUtil.add(vector1.getY(), vector2.getY()));

            if (Double.compare(0, vector.getLength()) == 0) {
                if (Double.compare(vector1.getX(), 0) == 0
                        && Double.compare(vector2.getX(), 0) == 0) {
                    vector.setX(1.0);
                    vector.setY(0.0);
                } else if (Double.compare(vector1.getY(), 0) == 0
                        && Double.compare(vector2.getY(), 0) == 0) {
                    vector.setX(0.0);
                    vector.setY(1.0);
                } else {
                    vector.setX(points.get(i).getX());
                    double y =
                            ArithUtil.mul(
                                    vector.getX(),
                                    ArithUtil.div(
                                            -1, ArithUtil.div(vector1.getY(), vector1.getX())));
                    vector.setY(y);
                }
            }
            // 计算偏移距离
            double cos =
                    ArithUtil.div(
                            MathUtil.getMulOfVector(vector1, vector2),
                            ArithUtil.mul(vector1.getLength(), vector2.getLength()));
            // 半角公式
            double sin2 = Math.sqrt((1 - cos) / 2);
//            System.out.println(sin2);
            double L = 1.0;
            if (Double.compare(0.0, sin2) != 0) {
                L = 1.0 / sin2;
            }
            vector.normalize();
            vector.mul(L);

            // 结果点
            Point point =
                    new Point(
                            ArithUtil.add(points.get(i).getX(), vector.getX()),
                            ArithUtil.add(points.get(i).getY(), vector.getY()));
            // 判断结果点是在多边形外面还是里面，射线法
            List<LineSegment> contour = new ArrayList<>();
            for (int m = 0; m < points.size(); m++){
                LineSegment lineSegment = new LineSegment(points.get(m), points.get((m + 1) % points.size()));
                contour.add(lineSegment);
            }

            boolean inPolygon = MathUtil.isInPolygon(contour, point);
            com.lee.entity.Vector pointToIn = null;
            com.lee.entity.Vector pointToOut = null;
            if (inPolygon) {
                // 在轮廓里，说明vector是指向轮廓里的方向
                pointToIn = vector;
                pointToOut = new com.lee.entity.Vector();
                pointToOut.setX(ArithUtil.mul(vector.getX(), -1));
                pointToOut.setY(ArithUtil.mul(vector.getY(), -1));
            } else {
                pointToOut = vector;
                pointToIn = new Vector();
                pointToIn.setX(ArithUtil.mul(vector.getX(), -1));
                pointToIn.setY(ArithUtil.mul(vector.getY(), -1));
            }

            in.add(pointToIn);
            out.add(pointToOut);
        }

        map.put("in", in);
        map.put("out", out);

        return map;
    }
}
