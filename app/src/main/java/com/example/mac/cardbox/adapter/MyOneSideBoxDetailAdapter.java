package com.example.mac.cardbox.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.SelectedCardActivity;
import com.example.mac.cardbox.activity.SelectedOneSideCardActivity;
import com.example.mac.cardbox.activity.SelectedOneSideOthersCardActivity;
import com.example.mac.cardbox.bean.Card;

import java.util.List;

public class MyOneSideBoxDetailAdapter extends RecyclerView.Adapter<MyOneSideBoxDetailAdapter.ViewHolder>  {

    private List<Card> cardList;
    private Context context;
    private boolean IsOthers;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView;
        TextView card_content;

        public ViewHolder(View view) {
            super(view);
            cardView = view;
            card_content = (TextView)view.findViewById(R.id.onesideCardContent);
        }
    }

    public MyOneSideBoxDetailAdapter(List<Card> list, Context con,boolean flag) {
        cardList = list;
        context = con;
        IsOthers = flag;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.oneside_card_item, viewGroup, false);
        final MyOneSideBoxDetailAdapter.ViewHolder holder = new MyOneSideBoxDetailAdapter.ViewHolder(view);

        //点击事件啥的都在这设置
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //判断一下是不是他人的盒子，是他人的就不可修改，本人的才可以修改（即有menu）
                if(IsOthers) {
                    Intent intent = new Intent(context,SelectedOneSideOthersCardActivity.class);
                    //得到点击位置
                    int position = holder.getAdapterPosition();
                    intent.putExtra("selectedCard",cardList.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context,SelectedOneSideCardActivity.class);
                    //得到点击位置
                    int position = holder.getAdapterPosition();
                    intent.putExtra("selectedCard",cardList.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent);
                }

            }
        });

        return holder;
    }

    //会在每个子项被滚动到屏幕时执行
    @Override
    public void onBindViewHolder(@NonNull MyOneSideBoxDetailAdapter.ViewHolder viewHolder, int i) {
        Card card = cardList.get(i);
        viewHolder.card_content.setText(card.getCard_front());
    }

    @Override
    public int getItemCount() {
        return cardList.size();
    }
}
