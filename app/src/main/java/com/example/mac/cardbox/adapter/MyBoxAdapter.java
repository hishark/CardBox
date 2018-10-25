package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.AddOneSideBoxActivity;
import com.example.mac.cardbox.activity.BoxDetailActivity;
import com.example.mac.cardbox.bean.Box;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MyBoxAdapter extends RecyclerView.Adapter<MyBoxAdapter.ViewHolder> {

    private List<Box> mBoxList;
    private Context mContext;

    private static final String TAG = "MyBoxAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View myboxView;
        TextView box_textView;
        CircleImageView box_Authority;
        public ViewHolder(View view) {
            super(view);
            myboxView = view;
            box_textView = (TextView)view.findViewById(R.id.tv_myBox_boxName);
            box_Authority = (CircleImageView)view.findViewById(R.id.img_myBox_If_Public);
        }
    }

    public MyBoxAdapter(List<Box> boxList, Context context) {
        mBoxList = boxList;
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.mybox_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        //点击盒子就进入盒子详情页面
        holder.myboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Box box = mBoxList.get(position);
                Toast.makeText(mContext, box.getBox_name(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(mContext, BoxDetailActivity.class);
                intent.putExtra("Box",mBoxList.get(position));
                mContext.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(mBoxList.size()!=0) {
            Box box = mBoxList.get(i);
            viewHolder.box_textView.setText(box.getBox_name());
            if(box.getBox_authority().equals("公开")) {
                viewHolder.box_Authority.setVisibility(View.INVISIBLE);
            }else {
                viewHolder.box_Authority.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return mBoxList.size();
    }


}
