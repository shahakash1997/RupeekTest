package com.rupeek.rupeektest.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class Utils {
    public static void showSnackBar(View view, String message, String action, final View.OnClickListener listener){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        if (!action.isEmpty() && listener != null){
            snackbar.setAction(action, v -> {
                snackbar.dismiss();
                listener.onClick(view);
            });
        }
        snackbar.show();
    }
}
