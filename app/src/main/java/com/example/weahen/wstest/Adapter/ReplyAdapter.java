package com.example.weahen.wstest.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.weahen.wstest.Activity.ShowImageActivity;
import com.example.weahen.wstest.Model.Content;
import com.example.weahen.wstest.Obj.ImageInfoObj;
import com.example.weahen.wstest.Obj.ImageWidgetInfoObj;
import com.example.weahen.wstest.R;
import com.example.weahen.wstest.widget.CircleImageView;

import java.util.List;

public class ReplyAdapter extends BaseAdapter implements View.OnClickListener {
    private Context context;
    private Activity activity;
    private List<Content> contentItems;
    public Thread thread;
    // private static final int

    Content m;
    ImageView imageView;

    Uri uri;
    private ImageInfoObj imageInfoObj;
    private ImageWidgetInfoObj imageWidgetInfoObj;


    public ReplyAdapter(Context context, List<Content> navDrawerItems, Activity activity) {
        this.context = context;
        this.contentItems = navDrawerItems;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        Log.e("Reply count","count size is "+contentItems.size());
        return contentItems.size();
    }

    @Override
    public Object getItem(int position) {
        return contentItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        m = contentItems.get(position);
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if(position==0){
            convertView = mInflater.inflate(R.layout.reply_list_item_header_right, null);
            TextView txtlblMsgFrom = convertView.findViewById(R.id.lblMsgFrom);
            txtlblMsgFrom.setText(m.getUserName());
            CircleImageView imageSend = convertView.findViewById(R.id.chat_item_header);
            Log.e("Tag1", "MainActivity.sendHeadImage" + m.getHeadImage());

            int i = Integer.parseInt(m.getHeadImage());
            Log.e("Tag1", "i" + i);
            imageSend.setImageResource(i);
//显示时间
            TextView txtTime = convertView.findViewById(R.id.getCurrentTime);
            txtTime.setText(m.getTime());
            TextView withdraw = convertView.findViewById(R.id.chat_item_withdraw);
        }else{
            convertView = mInflater.inflate(R.layout.reply_list_item, null);
        }
//        if (contentItems.get(position).getWithdraw()) {
//            convertView = mInflater.inflate(R.layout.list_item_message_withdraw, null);
//            TextView txtMsg = convertView.findViewById(R.id.txtMsg);
//            txtMsg.setText(m.getContent());
//        } else {
//            convertView = mInflater.inflate(R.layout.list_item_message_right, null);
//            TextView txtMsg = convertView.findViewById(R.id.txtMsg);
//            txtMsg.setText(m.getContent());
//        }

        TextView txtMsg = convertView.findViewById(R.id.txtMsg);
        txtMsg.setText(m.getContent());

        return convertView;
    }


    private void init() {


        imageInfoObj = new ImageInfoObj();
        imageInfoObj.imageUrl = uri.toString();
        imageInfoObj.imageWidth = 1280;
        imageInfoObj.imageHeight = 720;

        imageWidgetInfoObj = new ImageWidgetInfoObj();
        imageWidgetInfoObj.x = imageView.getLeft();
        imageWidgetInfoObj.y = imageView.getTop();
        imageWidgetInfoObj.width = imageView.getLayoutParams().width;
        imageWidgetInfoObj.height = imageView.getLayoutParams().height;

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(context, ShowImageActivity.class);
        intent.putExtra("imageInfoObj", imageInfoObj);
        intent.putExtra("imageWidgetInfoObj", imageWidgetInfoObj);
        context.startActivity(intent);

    }

}
