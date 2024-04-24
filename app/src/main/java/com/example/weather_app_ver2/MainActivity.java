package com.example.weather_app_ver2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    private androidx.constraintlayout.widget.ConstraintLayout main_layout;
    private Button bt_logout,bt_save,bt_search,bt_delete_account;
    private EditText et_save_cityname,et_search_cityname;
    private TextView tv_temperature,tv_weather_condition,tv_date;
    private ImageView iv_icon_weather;
    private InputMethodManager inputmethodmaneger;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;
    private FirebaseUser user;
    private String userid;
    private DatabaseReference reference;
    private String readdata_cityname;
    private Spinner spinner;
    private int hour=0;
    final int now=0;
    Warning_Dialog warning_dialog=new Warning_Dialog();
    Error_Dialog error_dialog=new Error_Dialog();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        bt_logout=findViewById(R.id.bt_logout);
        bt_save=findViewById(R.id.bt_save);
        bt_search=findViewById(R.id.bt_search);
        bt_delete_account=findViewById(R.id.bt_delete_account);
        et_save_cityname=findViewById(R.id.et_save_cityname);
        et_search_cityname=findViewById(R.id.et_search_cityname);
        tv_temperature=findViewById(R.id.tv_temperature);
        tv_weather_condition=findViewById(R.id.tv_weather_condition);
        tv_date=findViewById(R.id.tv_date);
        main_layout=findViewById(R.id.main_layout);
        iv_icon_weather=findViewById(R.id.iv_icon_weather);
        spinner=findViewById(R.id.spinner);

        if(user==null){
            Intent_to_LoginActivity();
            return;
        }
        Init_Ui();

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(MainActivity.this,R.array.spinner_hour,android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            //　アイテムが選択された時
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hour=position;
            }
            //　アイテムが選択されなかった
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bt_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Close_Keyboard(view);
                String cityname=et_save_cityname.getText().toString();
                if(cityname.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter City name!",Toast.LENGTH_SHORT).show();
                    return;
                }
                Check_Vailed_Cityname(cityname);
            }
        });

        bt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Close_Keyboard(view);
                String cityname=et_search_cityname.getText().toString();
                if(cityname.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please enter City name!",Toast.LENGTH_SHORT).show();
                    return;
                }
                //weather API
                Serch_Weather(cityname,hour);
            }
        });

        bt_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                warning_dialog.Show_Dialog(MainActivity.this,"Logout ?","Are you sure want to Logout ?",new CustomDialogClickListener() {
                    @Override
                    public void OnPositiveButtonClick() {
                        //okが押された処理
                        FirebaseAuth.getInstance().signOut();
                        Intent_to_LoginActivity();
                    }

                    @Override
                    public void OnNegativeButtonClick() {

                    }
                });
            }
        });

        bt_delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warning_dialog.Show_Dialog(MainActivity.this,"Warning","Are you sure want to Delete Account ?",new CustomDialogClickListener() {
                    @Override
                    public void OnPositiveButtonClick() {
                        //okが押された処理
                        //Toast.makeText(MainActivity.this, "positive", Toast.LENGTH_SHORT).show();
                        Delete_Account();
                    }

                    @Override
                    public void OnNegativeButtonClick() {
                        //cancelが押された処理
                        //Toast.makeText(MainActivity.this, "negative", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        inputmethodmaneger=(InputMethodManager)getSystemService(MainActivity.this.INPUT_METHOD_SERVICE);
        inputmethodmaneger.hideSoftInputFromWindow(main_layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //main_layout.requestFocus();
        return false;
    }

    private void Delete_Account(){
        userid=user.getUid();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("users").child(userid);
        reference.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent_to_LoginActivity();
                        }else{
                            Show_Error_Dialog("Error!","Delete Account Failed");
                        }
                    }
                });
            }
        });
    }

    private void Read_Data(){
        userid=user.getUid();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("users");
        reference.child(userid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful()&&task.getResult().exists()){
                    DataSnapshot snapshot = task.getResult();
                    readdata_cityname=String.valueOf(snapshot.child("city_name").getValue());
                    Set_Data();
                }
            }
        });
    }

    private void Set_Data(){
        et_save_cityname.setText(readdata_cityname);
        et_search_cityname.setText(readdata_cityname);
    }

    private void Parse_Response(JSONObject response,int hour){
        try {
            JSONObject current = response.getJSONObject("current");
            String current_date = current.getString("last_updated");
            String date;
            String temperature;
            String conditionText;
            String conditioniconUrl;
            if(hour==now){
                JSONObject condition = current.getJSONObject("condition");
                temperature = current.getString("temp_c");
                conditionText = condition.getString("text");
                conditioniconUrl = condition.getString("icon");
                Picasso.get().load("http:"+conditioniconUrl).resize(400, 400).centerCrop().into(iv_icon_weather);
                tv_date.setText(current_date);
                tv_temperature.setText("Average : "+temperature+"℃");
                tv_weather_condition.setText(conditionText);
            }else{
                String target_date=gethourlater(hour,current_date);
                System.out.println(target_date);

                JSONObject forecast = response.getJSONObject("forecast");
                JSONArray forecastdayArray = forecast.getJSONArray("forecastday");

                for (int i = 0; i < forecastdayArray.length(); i++) {
                    JSONObject forecastday = forecastdayArray.getJSONObject(i);
                    JSONArray hourArray = forecastday.getJSONArray("hour");

                    for (int j = 0; j < hourArray.length(); j++) {
                        JSONObject Hour = hourArray.getJSONObject(j);
                        String time = Hour.getString("time");

                        if (time.equals(target_date)) {

                            temperature=Hour.getString("temp_c");
                            JSONObject condition = Hour.getJSONObject("condition");
                            conditionText=condition.getString("text");
                            conditioniconUrl=condition.getString("icon");
                            date=time;
                            Picasso.get().load("http:"+conditioniconUrl).resize(400, 400).centerCrop().into(iv_icon_weather);
                            tv_date.setText(date);
                            tv_temperature.setText("Average : "+temperature+"℃");
                            tv_weather_condition.setText(conditionText);
                            System.out.println(time);
                            return;
                        }
                    }
                }
           }
                /*JSONObject forecast = response.getJSONObject("forecast");
                JSONArray forecastday = forecast.getJSONArray("forecastday");
                JSONObject firstDay = forecastday.getJSONObject(0);
                String date=firstDay.getString("date");
                JSONObject day = firstDay.getJSONObject("day");
                JSONObject condition = day.getJSONObject("condition");
                String Avg_temperature = day.getString("avgtemp_c");
                String conditionText = condition.getString("text");
                String conditioniconUrl = condition.getString("icon");
                tv_date.setText(date);
                tv_temperature.setText("Average : "+Avg_temperature+"℃");
                tv_weather_condition.setText(conditionText);
                Picasso.get().load("http:"+conditioniconUrl).resize(400, 400).centerCrop().into(iv_icon_weather);*/
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void Update_Data(String cityname){
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("users");
        reference.child(userid).child("city_name").setValue(cityname).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"Success Save city name",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Fail Save city name",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void Check_Vailed_Cityname(String cityname){
        WeatherAPI weatherapi = new WeatherAPI();
        weatherapi.httprequest(cityname,MainActivity.this, new WeatherCallbackListener() {
            @Override
            public void OnWeatherResponse(JSONObject response) {
                // レスポンスを処理する
                warning_dialog.Show_Dialog(MainActivity.this,"Save ?","Receive notifications for "+cityname+" weather",new CustomDialogClickListener() {
                    @Override
                    public void OnPositiveButtonClick() {//ok button
                        Update_Data(cityname);
                    }
                    @Override
                    public void OnNegativeButtonClick() {//cancel button

                    }
                });
                Log.d(TAG, "onWeatherResponse: " + response.toString());
            }

            @Override
            public void OnError(String message) {
                // エラーが発生した場合の処理
                Log.e(TAG, "onError: " + message);
                Show_Error_Dialog("Error",cityname+" not found");
            }
        });
    }
    private void Serch_Weather(String cityname,int hour){
        WeatherAPI weatherapi = new WeatherAPI();
        weatherapi.httprequest(cityname,MainActivity.this, new WeatherCallbackListener() {
            @Override
            public void OnWeatherResponse(JSONObject response) {
                // レスポンスを処理する
                Parse_Response(response,hour);
                Log.d(TAG, "onWeatherResponse: " + response.toString());
            }
            @Override
            public void OnError(String message) {
                // エラーが発生した場合の処理
                Log.e(TAG, "onError: " + message);
                Show_Error_Dialog("Error",cityname+" not found");
            }
        });
    }

    private void Close_Keyboard(View v){
        InputMethodManager inputmethodmanager;
        inputmethodmanager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputmethodmanager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
    private void Intent_to_LoginActivity(){
        Intent intent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }
    private void Show_Error_Dialog(String title,String massage){
        Error_Dialog error_dialog=new Error_Dialog();
        error_dialog.Show_Dialog(MainActivity.this,title,massage);
    }

    private void Init_Ui(){
        Read_Data();
        //hour=0;
        //Serch_Weather(readdata_cityname,hour);
    }

    private String gethourlater(int hour,String current_date){
        Calendar calendar;
        calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Date date = null;
        try {
            date = sdf.parse(current_date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Date を Calendar に設定し、9時間後の時刻を計算
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE,00);
        // Calendar を Date に戻して結果を出力
        Date resultdate = calendar.getTime();
        String result = sdf.format(resultdate);
        Log.d("hourlatar", "hourlater: "+result);
        return result;
    }
}