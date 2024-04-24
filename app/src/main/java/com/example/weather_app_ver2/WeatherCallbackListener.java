package com.example.weather_app_ver2;

import org.json.JSONObject;

public interface WeatherCallbackListener {
    void OnWeatherResponse(JSONObject response);
    void OnError(String message);
}
