package com.example.weather_app_ver2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

public class Error_Dialog {
    public static void Show_Dialog(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.error_dialog, null);

        TextView titleTextView = view.findViewById(R.id.layout_error).findViewById(R.id.error_title);
        TextView messageTextView = view.findViewById(R.id.tv_error_message);
        Button bt_ok = view.findViewById(R.id.layout_error).findViewById(R.id.bt_error_ok);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        bt_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Positive button click action
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
