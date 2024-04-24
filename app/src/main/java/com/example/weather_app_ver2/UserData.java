package com.example.weather_app_ver2;

public class UserData {
    String device_token;
    String city_name;
    public UserData() {
    }
    public UserData(String city_name,String device_token) {
        this.device_token = device_token;
        this.city_name = city_name;
    }
    public String getDevice_token() {
        return device_token;
    }

    public void setDevice_token(String device_token) {
        this.device_token = device_token;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }
}
