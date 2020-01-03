package com.example.weahen.wstest.Activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.Utils.ScreenUtils;
import com.example.weahen.wstest.Utils.SharedPreferencesUtil;

import butterknife.ButterKnife;

public class AboutActivity extends BaseActivity {
    ScreenUtils utils;
    SharedPreferencesUtil spUtils;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ButterKnife.bind(this);
        initTitle("软件介绍" );
    }

    public static void newInstance(Context context) {
        Intent intent = new Intent(context,AboutActivity.class);
        context.startActivity(intent);
        ((Activity)context).overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }
}
