package com.weizu.myapplication.adapters;

import android.content.Context;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.weizu.myapplication.Bean.User;
import com.weizu.myapplication.R;

import java.util.List;

public class RVUserAdapter extends ArrayAdapter<User> {

    private int mResId;

    public RVUserAdapter(@NonNull Context context, int resource, @NonNull List<User> datas) {
        super(context, resource, datas);
        this.mResId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MyViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new MyViewHolder();
            convertView = LayoutInflater.from(getContext()).inflate(mResId, parent, false);
            viewHolder.left = convertView.findViewById(R.id.item_left);
            viewHolder.right = convertView.findViewById(R.id.item_right);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (MyViewHolder) convertView.getTag();
        }
        User user = getItem(position);
        if(user != null && user.getName() != null)
            viewHolder.left.setText(user.getName());

        if(user != null && user.getUserID() != null)
            viewHolder.right.setText(user.getUserID());

        return convertView;
    }

    static class MyViewHolder{
        private TextView left;
        private TextView right;
    }
}
