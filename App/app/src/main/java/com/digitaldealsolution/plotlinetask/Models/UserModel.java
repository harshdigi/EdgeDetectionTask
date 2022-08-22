package com.digitaldealsolution.plotlinetask.Models;

public class UserModel {
    String name,email,id;
    ImageUpload imageUpload;

    public UserModel(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public UserModel(String name, String email, String id, ImageUpload imageUpload) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.imageUpload = imageUpload;
    }

    public UserModel(String name, String email, String id) {
        this.name = name;
        this.email = email;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ImageUpload getImageUpload() {
        return imageUpload;
    }

    public void setImageUpload(ImageUpload imageUpload) {
        this.imageUpload = imageUpload;
    }
}
