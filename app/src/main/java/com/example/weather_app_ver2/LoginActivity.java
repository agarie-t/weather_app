package com.example.weather_app_ver2;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private EditText et_login_email,et_login_password;
    private TextView tv_go_to_signup;
    private Button bt_login;
    private String login_email,login_password;
    private FirebaseAuth mAuth;
    private RelativeLayout login_layput;
    private InputMethodManager inputmethodmaneger;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent_to_MainActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        et_login_email=findViewById(R.id.et_login_email);
        et_login_password=findViewById(R.id.et_login_password);
        bt_login=findViewById(R.id.bt_login);
        tv_go_to_signup=findViewById(R.id.tv_go_to_signup);
        login_layput=findViewById(R.id.login_layout);

        tv_go_to_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent_to_RegisterActivity();
            }
        });

        bt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Close_Keyboard(view);
                login_email=et_login_email.getText().toString();
                login_password=et_login_password.getText().toString();

                if(login_email.isEmpty() || login_password.isEmpty()){
                    Toast.makeText(LoginActivity.this, "Enter email and password", Toast.LENGTH_SHORT).show();
                    return;
                }
                Loading_Dialog loading_dialog=new Loading_Dialog(LoginActivity.this);
                loading_dialog.Show_Loading_Dialog();

                mAuth.signInWithEmailAndPassword(login_email, login_password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "signInWithEmail:success");
                                    Toast.makeText(LoginActivity.this,"Login success",Toast.LENGTH_SHORT).show();
                                    Intent_to_MainActivity();
                                    //FirebaseUser user = mAuth.getCurrentUser();
                                    //updateUI(user);
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "signInWithEmail:failure", task.getException());
                                    Show_Error_Dialog("Login Failed !","Please try again");
                                    //updateUI(null);
                                }
                                loading_dialog.Dismiss_Loading_Dialog();
                            }
                        });
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        inputmethodmaneger=(InputMethodManager)getSystemService(LoginActivity.this.INPUT_METHOD_SERVICE);
        inputmethodmaneger.hideSoftInputFromWindow(login_layput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        //login_layput.requestFocus();
        return false;
    }


    private void Intent_to_RegisterActivity(){
        Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
        startActivity(intent);
        finish();
    }

    private void Intent_to_MainActivity(){
        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void Close_Keyboard(View view){
        InputMethodManager inputmethodmanager;
        inputmethodmanager = (InputMethodManager) getSystemService(this.INPUT_METHOD_SERVICE);
        inputmethodmanager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private void Show_Error_Dialog(String title,String massage){
        Error_Dialog error_dialog=new Error_Dialog();
        error_dialog.Show_Dialog(LoginActivity.this,title,massage);
    }
}
