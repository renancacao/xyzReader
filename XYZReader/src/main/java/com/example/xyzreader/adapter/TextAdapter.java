package com.example.xyzreader.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.xyzreader.R;

import java.util.List;

public class TextAdapter extends RecyclerView.Adapter<TextAdapter.TextViewHoder> {

    private List<String> data;


    public TextAdapter(List<String> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public TextViewHoder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View root;
        root= LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_text,parent,false);

        return new TextViewHoder(root);
    }


    @Override
    public void onBindViewHolder(@NonNull TextViewHoder holder, int position) {
        holder.textView.setText(data.get(position).trim());
    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    class TextViewHoder extends RecyclerView.ViewHolder {

        TextView textView;

        TextViewHoder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
        }
    }

}
