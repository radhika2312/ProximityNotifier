package com.example.proximitynotifier;
public class User {
    private String name,mobile,pwd;

    public User(String name, String mobile, String pwd) {
        this.name = name;
        this.mobile = mobile;
        this.pwd = pwd;
    }
    public User()
    {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}