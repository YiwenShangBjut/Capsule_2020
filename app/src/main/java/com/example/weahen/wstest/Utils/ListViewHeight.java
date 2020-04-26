package com.example.weahen.wstest.Utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.weahen.wstest.Adapter.ReplyAdapter;

public class ListViewHeight {
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        ReplyAdapter listAdapter = (ReplyAdapter)listView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
}
