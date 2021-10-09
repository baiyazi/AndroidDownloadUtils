package com.weizu.myapplication.custom.test;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.weizu.myapplication.R;
import com.weizu.myapplication.custom.ImageLoader;

import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private List<String> mUrls;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int mResId;

    public ImageAdapter(Context context, List<String> urls, int resId){
        this.mUrls = urls;
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mResId = resId;
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public String getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mLayoutInflater.inflate(mResId, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (MImageView) convertView.findViewById(R.id.imageview);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        ImageView imageVIew = viewHolder.imageView;
        String url = getItem(position);
        ImageLoader.with(mContext).load(url).into(imageVIew);
        return convertView;
    }

    static class ViewHolder{
        MImageView imageView;
    }
}
