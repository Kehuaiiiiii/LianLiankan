package com.company.njupt.lianliankan.view;


import android.graphics.Bitmap;

// 封装图片ID与图片本身的工具类
public class PieceImage {
    //图片
    private Bitmap image;
    //图片资源ID
    private int imageId;

    public PieceImage(Bitmap image, int imageId) {
        super();
        this.image = image;
        this.imageId = imageId;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
}
