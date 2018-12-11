package com.chat.walaashaaban.chat;

import java.security.PublicKey;

/**
 * Created by walaa on 11/13/17.
 */

public class Users {

    public String name;
    public String thumb_image;
    public String status;

    public Users(){

    }

    public Users(String name, String image, String status) {
        this.name = name;
        this.thumb_image = image;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return thumb_image;
    }

    public void setImage(String image) {
        this.thumb_image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
