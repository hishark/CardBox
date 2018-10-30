package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.BoxDetailActivity;
import com.example.mac.cardbox.activity.OthersBoxDetailActivity;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.util.CurrentUserUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class OthersProfileBoxAdapter extends RecyclerView.Adapter<OthersProfileBoxAdapter.ViewHolder> {

    private List<Box> mBoxList;
    private Context mContext;

    private static final String TAG = "OthersProfileBoxAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View wholeview;
        TextView box_textView;

        public ViewHolder(View view) {
            super(view);
            wholeview = view;
            box_textView = (TextView) view.findViewById(R.id.tv_searchBox_boxName);
        }
    }

    public OthersProfileBoxAdapter(List<Box> boxList, Context context) {
        mBoxList = boxList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.othersprofile_box_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.wholeview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Box box = mBoxList.get(position);
                String updatetime = box.getBox_update_time().toString();
                Log.d(TAG, "onClick: 日语单词咋没有更新时间" + updatetime);


                //如果选中了别人的盒子
                Intent intent = new Intent(mContext, OthersBoxDetailActivity.class);
                intent.putExtra("OthersBox", mBoxList.get(position));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (mBoxList.size() != 0) {
            Box box = mBoxList.get(i);
            viewHolder.box_textView.setText(box.getBox_name());
            Log.d(TAG, "onBindViewHolder: " + box.getUser().getUser_nickname());
        }
    }

    @Override
    public int getItemCount() {
        return mBoxList.size();
    }


}
