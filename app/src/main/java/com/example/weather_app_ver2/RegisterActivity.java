package com.example.weather_app_ver2;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONObject;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_signup_email,et_signup_password,et_signup_confirm_password,et_cityname;
    private TextView tv_go_to_login;
    private String signup_email,signup_password,signup_confirm_password;
    private Button bt_create_account;
    private ProgressBar sinup_progressbar;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private InputMethodManager inputmethodmaneger;
    private RelativeLayout signup_layout;
    private String device_token;
    private String cityname;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        user = mAuth.getCurrentUser();
        if(user != null){
            Intent_to_MainActivity();
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth=FirebaseAuth.getInstance();
        signup_layout=findViewById(R.id.signup_layout);
        et_signup_email=findViewById(R.id.et_signup_email);
        et_signup_password=findViewById(R.id.et_signup_password);
        et_signup_confirm_password=findViewById(R.id.et_signup_confirmpassword);
        bt_create_account=findViewById(R.id.bt_create_account);
        sinup_progressbar=findViewById(R.id.signup_progressbar);
        tv_go_to_login=findViewById(R.id.tv_go_to_login);
        et_cityname=findViewById(R.id.et_cityname);

        tv_go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent_to_LoginActivity();
            }
        });

        bt_create_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Close_Keyboard(view);
                signup_email=et_signup_email.getText().toString();
                signup_password=et_signup_password.getText().toString();
                signup_confirm_password=et_signup_confirm_password.getText().toString();
                cityname=et_cityname.getText().toString();
                if(signup_email.isEmpty()||signup_password.isEmpty()||signup_confirm_password.isEmpty()||cityname.isEmpty()){
                    Toast.makeText(RegisterActivity.this,"Check your inputs",Toast.LENGTH_SHORT).show();
                    return;
                }else if(!(signup_password.equals(signup_confirm_password))){
                    Toast.makeText(RegisterActivity.this,"The password confirmation does not much",Toast.LENGTH_SHORT).show();
                    return;
                }
                Check_cityname(cityname);

                //sinup_progressbar.setVisibility(View.VISIBLE);
            }
        });

    }
    private void Check_cityname(String cityname){

        Loading_Dialog loading_dialog=new Loading_Dialog(RegisterActivity.this);
        loading_dialog.Show_Loading_Dialog();
        WeatherAPI weatherAPI = new WeatherAPI();
        // API リクエストを行い、レスポンスを受け取る
        weatherAPI.httprequest(cityname,RegisterActivity.this, new WeatherCallbackListener() {
            @Override
            public void OnWeatherResponse(JSONObject response) {
                // レスポンスを処理する
                //Log.d(TAG, "onWeatherResponse: " + response.toString());
                mAuth.createUserWithEmailAndPassword(signup_email, signup_password)
                        .addOnCompleteListener(RegisterActivity.this,new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                //sinup_progressbar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "createUserWithEmail:success");
                                    Toast.makeText(RegisterActivity.this,"Signup success",Toast.LENGTH_SHORT).show();
                                    getDeviceToken();
                                    //InitUserData();
                                    Intent_to_MainActivity();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Show_Error_Dialog("Signup Failed !","Please try again");
                                    Toast.makeText(RegisterActivity.this, "Signup failed.", Toast.LENGTH_SHORT).show();
                                    //updateUI(null);
                                }
                                loading_dialog.Dismiss_Loading_Dialog();
                            }
                        });
            }

            @Override
            public void OnError(String message) {
                // エラーが発生した場合の処理
                Log.e(TAG, "onError: " + message);
                Show_Error_Dialog("Error",cityname+" not found");
            }
        });
    }

    private void Show_Error_Dialog(String title,String massage){
        Error_Dialog error_dialog=new Error_Dialog();
        error_dialog.Show_Dialog(RegisterActivity.this,title,massage);
    }
    private void Intent_to_MainActivity(){
        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void Intent_to_LoginActivity(){
        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        inputmethodmaneger=(InputMethodManager)getSystemService(RegisterActivity.this.INPUT_METHOD_SERVICE);
        inputmethodmaneger.hideSoftInputFromWindow(signup_layout.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //signup_layout.requestFocus();
        return false;
    }
    private void Close_Keyboard(View view){
        InputMethodManager inputmethodmanager;
        inputmethodmanager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void InitUserData(){
        FirebaseDatabase database=FirebaseDatabase.getInstance();
        DatabaseReference reference=database.getReference("users");
        user=mAuth.getCurrentUser();
        String userid=user.getUid();
        cityname=et_cityname.getText().toString();
        UserData userdata=new UserData(cityname,device_token);
        reference.child(userid).setValue(userdata).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){

                }else{

                }
            }
        });
    }

    private void getDeviceToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            device_token=task.getResult();
                            InitUserData();
                        }

                    }
                });
    }
}