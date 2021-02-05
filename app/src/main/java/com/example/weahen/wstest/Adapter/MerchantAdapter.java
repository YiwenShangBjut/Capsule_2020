package com.example.weahen.wstest.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.example.weahen.wstest.Activity.MerchantActivity;
import com.example.weahen.wstest.Model.Merchant;
import com.example.weahen.wstest.R;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 为RecyclerView准备一个适配器，加载商家信息
 */
public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.ViewHolder> {

    private Context mContext;

    private List<Merchant> merchantList;
    private List<Bitmap> bitmapList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView merchantImg;
        TextView merchantName;

        public ViewHolder(@NonNull View view) {
            super(view);
            cardView = (CardView) view;
            merchantImg = view.findViewById(R.id.merchant_image);
            merchantName = view.findViewById(R.id.merchant_name);

        }
    }

    public MerchantAdapter(List<Merchant> merchantList, List<Bitmap> bitmapList) {
        this.merchantList = merchantList;
        this.bitmapList = bitmapList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        if (mContext == null) {
            mContext = viewGroup.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.merchant_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Merchant merchant = merchantList.get(position);
                Intent intent = new Intent(mContext, MerchantActivity.class);
                intent.putExtra(MerchantActivity.MERCHANT_NAME, merchant.getTitle());

                //将bitmap转为字节数组
                Bitmap bitmap = bitmapList.get(position);
                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte [] bitmapByte =baos.toByteArray();
                intent.putExtra(MerchantActivity.MERCHANT_IMAGE, bitmapByte);

                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Merchant merchant = merchantList.get(position);
        viewHolder.merchantName.setText(merchant.getTitle());
        RequestOptions options = new RequestOptions().placeholder(R.drawable.ic_pre_img).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE);
        Glide.with(mContext).asBitmap().load(bitmapList.get(position)).apply(options).into(viewHolder.merchantImg);

    }

    @Override
    public int getItemCount() {
        return merchantList.size();
    }


}
