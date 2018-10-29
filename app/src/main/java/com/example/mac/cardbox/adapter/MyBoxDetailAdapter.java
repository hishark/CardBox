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

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.activity.SelectedCardActivity;
import com.example.mac.cardbox.activity.SelectedOthersCardActivity;
import com.example.mac.cardbox.bean.Card;

import java.util.List;

public class MyBoxDetailAdapter extends RecyclerView.Adapter<MyBoxDetailAdapter.ViewHolder>  {

    private List<Card> cardList;
    private Context context;
    private boolean IsOthers;

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

    public MyBoxDetailAdapter(List<Card> list, Context con,boolean flag) {
        cardList = list;
        context = con;
        IsOthers = flag;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup,int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.twoside_card_item, viewGroup, false);
        final MyBoxDetailAdapter.ViewHolder holder = new MyBoxDetailAdapter.ViewHolder(view);

        //点击事件啥的都在这设置
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //如果是点击别人盒子里的卡片，那么就跳转到不可以编辑删除的卡片查看界面
                if(IsOthers) {
                    Intent intent = new Intent(context,SelectedOthersCardActivity.class);
                    //得到点击位置
                    int position = holder.getAdapterPosition();
                    intent.putExtra("selectedCard",cardList.get(position));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
                    context.startActivity(intent);
                } else {
                    //如果是自己的盒子，就跳转到可编辑可删除的卡片查看界面
                    Intent intent = new Intent(context,SelectedCardActivity.class);
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
