package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.BoxDetailActivity;
import com.example.mac.cardbox.activity.OthersBoxDetailActivity;
import com.example.mac.cardbox.activity.SelectedOthersCardActivity;
import com.example.mac.cardbox.bean.Box;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyFavouriteBoxAdapter extends RecyclerView.Adapter<MyFavouriteBoxAdapter.ViewHolder> {

    private List<Box> mBoxList;
    private Context mContext;

    private static final String TAG = "MyFavouriteBoxAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View myboxView;
        TextView box_name;
        TextView box_username;
        TextView box_updatetime;

        public ViewHolder(View view) {
            super(view);
            myboxView = view;
            box_name = view.findViewById(R.id.tv_lovebox_boxname);
            box_username = view.findViewById(R.id.tv_lovebox_username);
            box_updatetime = view.findViewById(R.id.tv_lovebox_updatetime);
        }
    }

    public MyFavouriteBoxAdapter(List<Box> boxList, Context context) {
        mBoxList = boxList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myfavouritebox_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.myboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //得到点击位置
                int position = holder.getAdapterPosition();
                Intent intent = new Intent(mContext, OthersBoxDetailActivity.class);
                intent.putExtra("OthersBox", mBoxList.get(position));
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (mBoxList.size() != 0) {
            Box box = mBoxList.get(i);
            viewHolder.box_name.setText(box.getBox_name());
            String time  = box.getBox_update_time().toString();
            viewHolder.box_updatetime.setText(time.substring(0,16));
            viewHolder.box_username.setText(box.getUser().getUser_nickname());
        }
    }

    @Override
    public int getItemCount() {
        return mBoxList.size();
    }


}
