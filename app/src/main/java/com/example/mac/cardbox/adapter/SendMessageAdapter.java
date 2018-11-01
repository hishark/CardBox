package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.OthersProfileActivity;
import com.example.mac.cardbox.bean.User;
import com.example.mac.cardbox.bean.UserMessage;
import com.example.mac.cardbox.util.Constant;
import com.example.mac.cardbox.util.CurrentUserUtil;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SendMessageAdapter extends RecyclerView.Adapter<SendMessageAdapter.ViewHolder> {

    private static final String TAG = "SendMessageAdapter";
    private List<UserMessage> messageList;
    private List<User> receiverList;
    private Context mcontext;




    static class ViewHolder extends RecyclerView.ViewHolder {
        View itemView;
        CircleImageView user_avatar;
        TextView user_nickname;
        TextView msg_content;
        TextView msg_send_time;
        TextView sendTo;

        public ViewHolder(View view) {
            super(view);
            itemView = view;
            user_avatar = view.findViewById(R.id.img_sendMsg_userAvatar);
            user_nickname = view.findViewById(R.id.tv_sendMsg_userNickname);
            msg_content = view.findViewById(R.id.tv_sendMsg_content);
            msg_send_time = view.findViewById(R.id.tv_sendMsg_sendTime);
            sendTo = view.findViewById(R.id.tv_sendMsg_sendTo);
        }
    }

    public SendMessageAdapter(List<UserMessage> list, Context context,List<User> rlist) {
        messageList = list;
        mcontext = context;
        receiverList = rlist;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.send_message_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                UserMessage sendMessage = messageList.get(position);

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
            String time  =  message.getMessage_send_time().toString();
            viewHolder.msg_send_time.setText(time.substring(0,19));
            viewHolder.sendTo.setText("寄给"+receiverList.get(i).getUser_nickname());
        }

    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }



}
