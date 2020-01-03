package com.example.weahen.wstest.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.weahen.wstest.BaseActivity;
import com.example.weahen.wstest.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeHeadImageActivity extends BaseActivity {
    GridView grid;
    ImageView imageView;

    //保存选择的图片ID，形式：R.Drawable.图片名
    public int headImage;
    public static String headImageString;
    int[] imageIds = new int[]{
            R.drawable.s01, R.drawable.s02, R.drawable.s03, R.drawable.s04, R.drawable.s05, R.drawable.s06, R.drawable.s07, R.drawable.s08, R.drawable.s09, R.drawable.s10, R.drawable.s11, R.drawable.s12};
    ///, R.drawable.s13, R.drawable.s14, R.drawable.s15, R.drawable.s16, R.drawable.s17, R.drawable.s18, R.drawable.s19, R.drawable.s20, R.drawable.s21, R.drawable.s22, R.drawable.s23, R.drawable.s24, R.drawable.s25};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_head_image);
        initTitle("修改头像");
        Button btnHeadImageSure = findViewById(R.id.btn_headImageSure);
        List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
        for (int i = 0; i < imageIds.length; i++) {
            Map<String, Object> listItem = new HashMap<String, Object>();
            listItem.put("image", imageIds[i]);
            listItems.add(listItem);
        }
        imageView = findViewById(R.id.imageView);
        SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItems, R.layout.cell, new String[]{"image"}, new int[]{R.id.image1});
        grid = findViewById(R.id.grid01);
        grid.setAdapter(simpleAdapter);
        grid.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //显示被选中的图片
                headImage = imageIds[position];
                imageView.setImageResource(headImage);
                Log.e("Tag1", "headImages  " + headImage);


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //显示被单击的图片
                headImage = imageIds[position];
                Log.e("Tag1", "imageIds  " + imageIds[position]);
                imageView.setImageResource(headImage);
                Log.e("Tag1", "headImages  " + headImage);
            }
        });
        //确定按钮的点击事件
        btnHeadImageSure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                headImageString = headImage + "";
                Toast.makeText(ChangeHeadImageActivity.this, "修改成功", Toast.LENGTH_SHORT).show();
                Log.e("Tag1", "headImageString in click" + headImageString);
            }
        });
    }
}
