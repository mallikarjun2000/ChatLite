package com.example.chatlite;

public class User {
    String name;
    String status;
    String image;
    String thumb_image;

    User(String name, String status , String image , String thumb_image){
        this.image=image;
        this.name=name;
        this.status=status;
        this.thumb_image=thumb_image;
    }
    User(){

    }

    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getThumb_image() {
        return thumb_image;
    }

}
