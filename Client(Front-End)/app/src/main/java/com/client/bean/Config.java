package com.ece651group8.uwaterloo.ca.ece_651_group8.bean;

// set and get ip and port


public class Config {


    private String ip = "192.168.43.38";
    private String port = "8000";


    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
}