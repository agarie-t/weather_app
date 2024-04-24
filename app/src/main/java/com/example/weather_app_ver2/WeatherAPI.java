package com.example.weather_app_ver2;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
public class WeatherAPI {
    private static final String TAG = "WeatherAPI";
    private final String apikey = "api_key";
    private final String firsturl = "http://api.weatherapi.com/v1/forecast.json?key=";
    private final String secondurl = "&days=2&aqi=no&alerts=no";
    //private String city_name;
    public void httprequest(String cityname,Context context, WeatherCallbackListener listener) {
        String Url =firsturl+apikey+"&q="+cityname+secondurl;
        RequestQueue requestqueue = Volley.newRequestQueue(context);
        JsonObjectRequest jsonrequest = new JsonObjectRequest(Request.Method.GET, Url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.OnWeatherResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.OnError("ERROR: " + error.getMessage());
            }
        });
        requestqueue.add(jsonrequest);
    }
}
