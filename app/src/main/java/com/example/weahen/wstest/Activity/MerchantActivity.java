package com.example.weahen.wstest.Activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;


/**
 * 从FunctionActivity跳转来
 * 功能：用于展示商家详情
 */

public class MerchantActivity extends BaseActivity {

    public static final String MERCHANT_NAME = "merchant_name";

    public static final String MERCHANT_IMAGE = "merchant_image";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merchant);

        Intent intent = getIntent();
        String merchantName = intent.getStringExtra(MERCHANT_NAME);
        //将图片的字节数组数据转为bitmap对象
        byte[] bis = intent.getByteArrayExtra(MERCHANT_IMAGE);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bis, 0, bis.length);

        Toolbar toolbar = findViewById(R.id.toolbar_mer);
        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsing_toolbar);
        ImageView merchantImageView = findViewById(R.id.merchant_image);
        TextView merchantContentText = findViewById(R.id.merchant_content);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(merchantName);
        Glide.with(this).load(bitmap).into(merchantImageView);

        //String merchantContent = generateMerchantContent(merchantName);
        //文字缩进
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\u3000\u3000").append(merchantName);
        merchantContentText.setText(stringBuilder);

        //悬浮按钮
        FloatingActionButton fab = findViewById(R.id.float_comment);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MerchantActivity.this, "该功能暂未开启", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }
}
