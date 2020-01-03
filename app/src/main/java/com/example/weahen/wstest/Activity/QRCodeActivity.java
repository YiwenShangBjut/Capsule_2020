package com.example.weahen.wstest.Activity;

import android.os.Bundle;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;

public class QRCodeActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        initTitle("软件二维码" );
    }

}
