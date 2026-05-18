package com.factifyft.app.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.factifyft.app.R;

public class customToast {

    public static void showToast(Context context, String message, int duration) {
        if (context == null) return;
        
        LayoutInflater inflater = LayoutInflater.from(context);
        // Do not cast context to Activity, and do not use Activity root for Toast inflation
        View layout = inflater.inflate(R.layout.toast_msg_deisgn, null);

        TextView textView = layout.findViewById(R.id.toastText);
        if (textView != null) {
            textView.setText(message);
        }

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 100);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();
    }
}
