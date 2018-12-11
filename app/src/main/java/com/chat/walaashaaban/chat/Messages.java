package com.chat.walaashaaban.chat;

/**
 * Created by walaa on 11/29/17.
 */

public class Messages {


    private String message;
    private String type;



    private String image;
    private long  time;
    private boolean seen;

    private String from;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPush_id() {
        return push_id;
    }

    public void setPush_id(String push_id) {
        this.push_id = push_id;
    }

    private String push_id;

    public Messages(String from) {
        this.from = from;
    }

    public String getFrom() {
        return from;
    }


    public void setFrom(String from) {
        this.from = from;
    }

    public Messages(String message, String type, long time, boolean seen) {
        this.message = message;
        this.type = type;
        this.time = time;
        this.seen = seen;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public Messages(){

    }


}
