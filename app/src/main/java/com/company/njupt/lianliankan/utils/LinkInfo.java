package com.company.njupt.lianliankan.utils;


import android.graphics.Point;

import java.util.ArrayList;
import java.util.List;

// 连接信息类
public class LinkInfo {
    //用于保存连接点
    private List<Point> points = new ArrayList<Point>();

    // 提供第一个构造器, 表示两个Point可以直接相连, 没有转折点
    public LinkInfo(Point p1, Point p2) {
        points.add(p1);
        points.add(p2);
    }

    // 提供第二个构造器, 表示三个Point可以相连, p2是p1与p3之间的转折点
    public LinkInfo(Point p1, Point p2, Point p3) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
    }

    // 提供第三个构造器, 表示四个Point可以相连, p2, p3是p1与p4的转折点
    public LinkInfo(Point p1, Point p2, Point p3, Point p4) {
        points.add(p1);
        points.add(p2);
        points.add(p3);
        points.add(p4);
    }

    public List<Point> getLinkPoints() {
        return points;
    }
}
