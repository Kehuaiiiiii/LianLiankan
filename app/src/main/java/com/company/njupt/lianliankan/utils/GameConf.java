package com.company.njupt.lianliankan.utils;

import android.content.Context;

// 游戏配置类
public class GameConf {
    //X轴有几个方块
    public final static int PIECE_X_SUM = 8;
    //Y轴有几个方块
    public final  static int PIECE_Y_SUM = 8;
    //从哪里开始画第一张图片出现的x座标
    public final  static int BEGIN_IMAGE_X = 15;
    //从哪里开始画第一张图片出现的x座标
    public final  static int BEGIN_IMAGE_Y = 75;
    //连连看的每个方块的图片的宽   启动的时候赋值
    public static int PIECE_WIDTH ;
    //连连看的每个方块的图片的高   启动的时候赋值
    public static int PIECE_HEIGHT ;
    //记录游戏的总时间
    public static int DEFAULT_TIME = 100;
    //Piece[][]数组第一维的长度
    private int xSize;
    //Piece[][]数组第二维的长度
    private int ySize;
    //Board中第一张图片出现的x座标
    private int beginImageX;
    //Board中第一张图片出现的y座标
    private int beginImageY;
    //记录游戏的剩余时间
    private int gameTime;
    //上下文
    private Context context;
    //难度
    private int level;

    // 构造函数
    public GameConf(int xSize, int ySize, int beginImageX, int beginImageY,
                    int gameTime, Context context, int level) {
        this.xSize = xSize;
        this.ySize = ySize;
        this.beginImageX = beginImageX;
        this.beginImageY = beginImageY;
        this.gameTime = gameTime;
        this.context = context;
        this.level = level;
    }

    public int getGameTime() {
        return gameTime;
    }

    public int getXSize() {
        return xSize;
    }

    public int getYSize() {
        return ySize;
    }

    public int getBeginImageX() {
        return beginImageX;
    }

    public int getBeginImageY() {
        return beginImageY;
    }

    public Context getContext() {
        return context;
    }

    public int getLevel() {
        return level;
    }
}
