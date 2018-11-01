package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.OthersProfileActivity;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.util.CurrentUserUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserRelationAdapter extends RecyclerView.Adapter<UserRelationAdapter.ViewHolder> {

    private static final String TAG = "UserRelationAdapter";
    private List<User> mUserList;
    private Context mcontext;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView user_avatar;
        TextView user_nickname;
        TextView user_intro;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            user_avatar = (CircleImageView) view.findViewById(R.id.tv_userrelation_avatar);
            user_nickname = (TextView) view.findViewById(R.id.tv_userrelation_username);
            user_intro  = view.findViewById(R.id.tv_userrelation_userIntro);
        }
    }

    public UserRelationAdapter(List<User> userList, Context context) {
        mUserList = userList;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.userrelation_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                User user = mUserList.get(position);
                Intent intent = new Intent(mcontext, OthersProfileActivity.class);
                intent.putExtra("selectedUser", user);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mcontext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (mUserList.size() != 0) {
            User user = mUserList.get(i);
            //Log.d(TAG, "onBindViewHolder: "+user.getUser_avatar());
            Glide.with(mcontext).load(user.getUser_avatar()).into(viewHolder.user_avatar);
            viewHolder.user_nickname.setText(user.getUser_nickname());
            viewHolder.user_intro.setText(user.getUser_intro());
        }

    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }


}
