package com.example.weather_app_ver2;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;

public class Loading_Dialog {
    private Activity activity;
    private AlertDialog dialog;
    public Loading_Dialog() {
    }

    public Loading_Dialog(Activity activity){
        this.activity=activity;
    }

    public void Show_Loading_Dialog(){

        LayoutInflater inflater=activity.getLayoutInflater();
        AlertDialog.Builder builder=new AlertDialog.Builder(activity);
        builder.setView(inflater.inflate(R.layout.loading_dialog,null));
        builder.setCancelable(true);
        dialog=builder.create();
        dialog.show();
    }
    public void Dismiss_Loading_Dialog(){
        dialog.dismiss();
    }
}
