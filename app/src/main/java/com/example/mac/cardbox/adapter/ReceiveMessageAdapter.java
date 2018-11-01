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
import com.example.mac.cardbox.bean.UserMessage;
import com.example.mac.cardbox.util.CurrentUserUtil;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ReceiveMessageAdapter extends RecyclerView.Adapter<ReceiveMessageAdapter.ViewHolder> {

    private static final String TAG = "SendMessageAdapter";
    private List<UserMessage> messageList;
    private Context mcontext;


    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView user_avatar;
        TextView user_nickname;
        TextView msg_content;
        TextView msg_send_time;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            user_avatar = view.findViewById(R.id.img_receiveMsg_userAvatar);
            user_nickname = view.findViewById(R.id.tv_receiveMsg_userNickname);
            msg_content = view.findViewById(R.id.tv_receiveMsg_content);
            msg_send_time = view.findViewById(R.id.tv_receiveMsg_sendTime);
        }
    }

    public ReceiveMessageAdapter(List<UserMessage> list, Context context) {
        messageList = list;
        mcontext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.receive_message_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                UserMessage userMessage = messageList.get(position);
                Intent intent = new Intent(mcontext, OthersProfileActivity.class);
                intent.putExtra("selectedUser", userMessage.getUser_sender());
                mcontext.startActivity(intent);


            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (messageList.size() != 0) {
            UserMessage message = messageList.get(i);
            Glide.with(mcontext).load(message.getUser_sender().getUser_avatar()).into(viewHolder.user_avatar);
            viewHolder.user_nickname.setText(message.getUser_sender().getUser_nickname());
            viewHolder.msg_content.setText(message.getMessage_content());
            String time = message.getMessage_send_time().toString();
            viewHolder.msg_send_time.setText(time.substring(0, 19));
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }


}
