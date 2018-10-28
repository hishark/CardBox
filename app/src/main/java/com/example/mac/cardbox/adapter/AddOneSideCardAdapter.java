package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Card;

import java.util.HashMap;
import java.util.List;

public class AddOneSideCardAdapter extends RecyclerView.Adapter<AddOneSideCardAdapter.ViewHolder> {

    private List<Card> mCardList;
    private Context mContext;
    private Handler handler;

    private static final String TAG = "AddOneSideCardAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        View mycardView;
        EditText et_front;
        ImageButton checkbutton,clearbutton;

        public ViewHolder(View view) {
            super(view);
            mycardView = view;
            et_front = (EditText) view.findViewById(R.id.et_addOnesideCard_front);
            checkbutton = (ImageButton)view.findViewById(R.id.bt_finishOneInput);
            clearbutton= (ImageButton)view.findViewById(R.id.bt_clearInput);
        }
    }

    public AddOneSideCardAdapter(List<Card> cards, Context context, Handler h) {
        mCardList = cards;
        mContext = context;
        handler = h;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.addonesidecard_item, viewGroup, false);
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
                msg.obj = result;
                handler.sendMessage(msg);
                Snackbar.make(v,"该卡片已保存，点击右上角完成添加所有",BaseTransientBottomBar.LENGTH_SHORT).show();
            }
        });

        holder.clearbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.et_front.setText("");
            }
        });

        return holder;
    }

    //对子项进行赋值
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Card card = mCardList.get(i);
        viewHolder.et_front.setText(card.getCard_front());
    }

    @Override
    public int getItemCount() {
        return mCardList.size();
    }


}
