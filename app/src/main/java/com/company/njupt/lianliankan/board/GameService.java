package com.company.njupt.lianliankan.board;

import com.company.njupt.lianliankan.utils.LinkInfo;
import com.company.njupt.lianliankan.view.Piece;

//游戏逻辑接口
public interface GameService {

    // 控制游戏开始的方法
    void start();

    // 获取Piece[][]数组
    Piece[][] getPieces();

    // 判断参数Piece[][]数组中是否还存在非空的Piece对象
    boolean hasPieces();

    // 根据鼠标的x座标和y座标, 查找出一个Piece对象
    Piece findPiece(float touchX, float touchY);

    // 判断两个Piece是否可以相连, 可以连接, 返回LinkInfo对象
    LinkInfo link(Piece p1, Piece p2);
}