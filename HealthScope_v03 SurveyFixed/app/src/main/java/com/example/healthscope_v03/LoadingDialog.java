package com.example.healthscope_v03;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class LoadingDialog {

    private Activity activity;
    private View layout;
    private AlertDialog dialog;

    public LoadingDialog(Activity myActivity, View myLayout) {
        activity = myActivity;
        layout = myLayout;
    }

    public void startLoadingAlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setView(layout);
        builder.setCancelable(false);

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog(){
        dialog.dismiss();
    }


}