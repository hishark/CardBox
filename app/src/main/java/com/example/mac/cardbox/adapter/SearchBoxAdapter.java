package com.example.mac.cardbox.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mac.cardbox.R;
import com.example.mac.cardbox.bean.Box;

import java.util.List;

public class SearchBoxAdapter extends RecyclerView.Adapter<SearchBoxAdapter.ViewHolder> {

    private List<Box> mBoxList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView box_textView;
        public ViewHolder(View view) {
            super(view);
            box_textView = (TextView)view.findViewById(R.id.tv_searchBox_boxitem);
        }
    }

    public SearchBoxAdapter(List<Box> boxList) {
        mBoxList = boxList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.searchbox_item, viewGroup, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Box box = mBoxList.get(i);
        viewHolder.box_textView.setText(box.getBox_name());
    }

    @Override
    public int getItemCount() {
        return mBoxList.size();
    }


}
