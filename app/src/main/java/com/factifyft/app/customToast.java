package com.factifyft.app;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class customToast {

    public static void showToast(Context context, String message,int duration) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_msg_deisgn,
                ((Activity) context).findViewById(android.R.id.content),
                false
        );

//        View layout = inflater.inflate(R.layout.toast_msg_deisgn, null,false);
        TextView textView=layout.findViewById(R.id.toastText);
        textView.setText(message);

        Toast toast = new Toast(context);
        toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL,0,100);
        toast.setDuration(duration);
        toast.setView(layout);
        toast.show();

    }

}
