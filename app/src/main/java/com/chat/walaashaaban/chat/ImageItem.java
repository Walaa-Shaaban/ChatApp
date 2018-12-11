package com.chat.walaashaaban.chat;

import android.graphics.Bitmap;

public class ImageItem {
    private int image;

    public ImageItem(int image) {
        super();
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

}


class ImageItemChat {
    private String image;

    public ImageItemChat(String image) {
        super();
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

}
