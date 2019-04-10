package com.yitianyitiandan.googlemap.model;

public class Hospital {
    private String zone;
    private String tel;
    private String address;
    private String name;

    public Hospital (String zone, String name, String address, String tel) {
        this.zone = zone;
        this.name = name;
        this.address = address;
        this.tel = tel;
    }

    public String getZone() {
        return this.address.substring(0,3);
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
