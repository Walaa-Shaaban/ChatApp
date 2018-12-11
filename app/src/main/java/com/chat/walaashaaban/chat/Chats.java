package com.chat.walaashaaban.chat;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by walaa on 12/3/17.
 */

public class Chats {

    public String name;



    public String image;


    public Chats(){

    }

    public Chats(String name, String image) {
        this.name = name;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
