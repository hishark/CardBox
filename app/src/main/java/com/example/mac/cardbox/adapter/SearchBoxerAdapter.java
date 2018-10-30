package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.OthersProfileActivity;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.CurrentUserUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchBoxerAdapter extends RecyclerView.Adapter<SearchBoxerAdapter.ViewHolder> {

    private static final String TAG = "SearchBoxerAdapter";
    private List<User> mUserList;
    private Context mcontext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        CircleImageView boxer_avatar;
        TextView boxer_nickname;
        public ViewHolder(View view) {
            super(view);
            boxer_avatar = (CircleImageView)view.findViewById(R.id.img_searchBoxer_boxeritem);
            boxer_nickname = (TextView)view.findViewById(R.id.tv_searchBoxer_boxeritem);
        }
    }

    public SearchBoxerAdapter(List<User> userList, Context context) {
        mUserList = userList;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.searchboxer_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.boxer_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                User user = mUserList.get(position);
                if(!user.getUser_account().equals(CurrentUserUtil.getCurrentUser().getUser_account())) {
                    Intent intent = new Intent(mcontext,OthersProfileActivity.class);
                    intent.putExtra("selectedUser",user);
                    mcontext.startActivity(intent);
                } else {
                    Snackbar.make(v,"你搜自己干什么咯",Snackbar.LENGTH_SHORT).show();
                }

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if(mUserList.size()!=0) {
            User user = mUserList.get(i);
            //Log.d(TAG, "onBindViewHolder: "+user.getUser_avatar());
            Glide.with(mcontext).load(user.getUser_avatar()).into(viewHolder.boxer_avatar);
            viewHolder.boxer_nickname.setText(user.getUser_nickname());
        }

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


}
