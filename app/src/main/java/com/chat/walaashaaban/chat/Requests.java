package com.chat.walaashaaban.chat;

/**
 * Created by walaa on 12/2/17.
 */

public class Requests {

    public String name;
    public String image;
    public String request_type;


    public Requests(){

    }



    public Requests(String name, String image, String request_type) {
        this.name = name;
        this.image = image;
        this.request_type = request_type;

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

    public String getRequest_type() { return request_type;}

    public void setRequest_type(String request_type) {this.request_type = request_type;}
}

