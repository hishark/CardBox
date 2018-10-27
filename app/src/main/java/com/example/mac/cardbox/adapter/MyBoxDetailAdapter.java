package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Card;

import java.util.List;

public class MyBoxDetailAdapter extends RecyclerView.Adapter<MyBoxDetailAdapter.ViewHolder>  {

    private List<Card> cardList;
    private Context context;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView card_front;
        TextView card_back;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            card_front = (TextView)view.findViewById(R.id.twosideCard_frontContent);
            card_back = (TextView)view.findViewById(R.id.twosideCard_backContent);
        }
    }

    public MyBoxDetailAdapter(List<Card> list, Context con) {
        cardList = list;
        context = con;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.twoside_card_item, viewGroup, false);
        final MyBoxDetailAdapter.ViewHolder holder = new MyBoxDetailAdapter.ViewHolder(view);
        //点击事件啥的都在这设置
        return holder;
    }

    //会在每个子项被滚动到屏幕时执行
    @Override
    public void onBindViewHolder(@NonNull MyBoxDetailAdapter.ViewHolder viewHolder, int i) {
        Card card = cardList.get(i);
        viewHolder.card_front.setText(card.getCard_front());
        viewHolder.card_back.setText(card.getCard_back());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
