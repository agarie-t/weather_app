package com.example.weather_app_ver2;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AlertDialog;

public class Warning_Dialog {
    public static void Show_Dialog(Context context, String title, String message, CustomDialogClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.warning_dialog,null);

        TextView titleTextView = view.findViewById(R.id.warning_layout).findViewById(R.id.warning_tittle);
        TextView messageTextView = view.findViewById(R.id.warning_layout).findViewById(R.id.warning_message);
        Button cancel = view.findViewById(R.id.warning_layout).findViewById(R.id.bt_cancel);
        Button ok = view.findViewById(R.id.warning_layout).findViewById(R.id.bt_ok);

        titleTextView.setText(title);
        messageTextView.setText(message);

        builder.setView(view);
        AlertDialog dialog = builder.create();

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Positive button click action
                if (listener != null) {
                    listener.OnPositiveButtonClick();
                }
                dialog.dismiss();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.OnNegativeButtonClick();
                }
                dialog.dismiss();
            }
        });
        if (dialog.getWindow() != null){
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        dialog.show();
    }
}
