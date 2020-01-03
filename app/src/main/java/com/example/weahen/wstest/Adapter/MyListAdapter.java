package com.example.weahen.wstest.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.weahen.wstest.R;

import java.util.List;
import java.util.Map;


public  class MyListAdapter extends BaseAdapter {
    private List<Map<String,String>> list;
    private Context context;
    public MyListAdapter(Context context, List<Map<String, String>> list) {
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh=null;
        if (convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.layout_list_item,null);
            vh=new ViewHolder(convertView);
            vh.name= (TextView) convertView.findViewById(R.id.name);

            convertView.setTag(vh);
        }else{
            vh= (ViewHolder) convertView.getTag();
        }
        Map<String,String> aa=list.get(position);
        vh.name.setText(aa.get("name"));

        return convertView;
    }
    class ViewHolder{
        TextView name;

        public ViewHolder(View view){
            name= (TextView) view.findViewById(R.id.name);

        }
    }

}
