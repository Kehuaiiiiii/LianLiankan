package com.company.njupt.lianliankan.view;


import android.graphics.Point;

// 连连看游戏中的方块对象
public class Piece {
    //保存方块对象的所对应的图片
    private PieceImage pieceImage;
    //该方块的左上角的x坐标
    private int beginX;
    //该方块的左上角的y座标
    private int beginY;
    //该对象在Piece[][]数组中第一维的索引值
    private int indexX;
    //该对象在Piece[][]数组中第二维的索引值
    private int indexY;


    // 设置该Piece对象在数组中的索引值
    public Piece(int indexX, int indexY) {
        this.indexX = indexX;
        this.indexY = indexY;
    }

    // 获取该Piece的中心位置
    public Point getCenter() {
        return new Point(getBeginX() + getPieceImage().getImage().getWidth()
                / 2, getBeginY() + getPieceImage().getImage().getHeight() / 2);
    }

    // 判断两个Piece上的图片是否相同
    public boolean isSameImage(Piece otherPieceImage) {
        if (pieceImage == null) {
            if (otherPieceImage.pieceImage != null) {
                return false;
            }
        }
        // 当两个Piece封装图片资源ID相同时，即可认为这两个Piece上的图片相同。
        return pieceImage.getImageId() == otherPieceImage.pieceImage.getImageId();
    }

    public int getBeginX() {
        return beginX;
    }

    public void setBeginX(int beginX) {
        this.beginX = beginX;
    }

    public int getBeginY() {
        return beginY;
    }

    public void setBeginY(int beginY) {
        this.beginY = beginY;
    }

    public int getIndexX() {
        return indexX;
    }

    public void setIndexX(int indexX) {
        this.indexX = indexX;
    }

    public int getIndexY() {
        return indexY;
    }

    public void setIndexY(int indexY) {
        this.indexY = indexY;
    }

    public PieceImage getPieceImage() {
        return pieceImage;
    }

    public void setPieceImage(PieceImage pieceImage) {
        this.pieceImage = pieceImage;
    }
}