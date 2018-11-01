package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.BoxDetailActivity;
import com.example.mac.cardbox.bean.Box;
import com.example.mac.cardbox.bean.Card;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddCardAdapter extends RecyclerView.Adapter<AddCardAdapter.ViewHolder> {

    private List<Card> mCardList;
    private Context mContext;
    private Handler handler;

    private static final String TAG = "AddCardAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mycardView;
        EditText et_front;
        EditText et_back;
        Button checkbutton,clearbutton;

        public ViewHolder(View view) {
            super(view);
            mycardView = view;
            et_front = (EditText) view.findViewById(R.id.et_addCard_front);
            et_back = (EditText) view.findViewById(R.id.et_addCard_back);
            checkbutton =view.findViewById(R.id.bt_finishOneInput);
            clearbutton= view.findViewById(R.id.bt_clearInput);
        }
    }

    public AddCardAdapter(List<Card> cards, Context context, Handler h) {
        mCardList = cards;
        mContext = context;
        handler = h;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addcard_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);

        holder.checkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Message msg = new Message();
                msg.what = 1;
                HashMap<String,Object> result = new HashMap<>();
                result.put("position",position);
                result.put("front",holder.et_front.getText().toString());
                result.put("back",holder.et_back.getText().toString());
                msg.obj = result;
                handler.sendMessage(msg);
                Snackbar.make(v,"该卡片已保存，点击加号添加下一张，点击右上角完成添加",BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

        holder.clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.et_front.setText("");
                holder.et_back.setText("");
            }
        });

        return holder;
    }

    //对子项进行赋值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Card card = mCardList.get(i);
        viewHolder.et_front.setText(card.getCard_front());
        viewHolder.et_back.setText(card.getCard_back());
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }


}
