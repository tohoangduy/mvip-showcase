package com.mq.myvtg.model;

public class App {
    private int img;
    private boolean isInstalled;
    private String name, des;

    public App(int img, boolean isInstalled, String name, String des) {
        this.img = img;
        this.isInstalled = isInstalled;
        this.name = name;
        this.des = des;
    }

    public int getImg() {
        return img;
    }

    public boolean getIsInstalled() {
        return isInstalled;
    }

    public String getName() {
        return name;
    }

    public String getDes() {
        return des;
    }
}
