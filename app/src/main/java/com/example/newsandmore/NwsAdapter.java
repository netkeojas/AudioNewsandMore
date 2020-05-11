package com.example.newsandmore;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NwsAdapter extends RecyclerView.Adapter<newsViewHolder> {

    ListOfNews listOfNews;
    ArrayList<newsModel> newslist;
    Context context;

    public NwsAdapter(ListOfNews listOfNews, ArrayList<newsModel> newslist,Context context) {
        this.listOfNews = listOfNews;
        this.newslist = newslist;
        this.context = context;
    }

    @NonNull
    @Override
    public newsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(listOfNews.getBaseContext());
        View view = layoutInflater.inflate(R.layout.nwselements,null,false);
        return new newsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsViewHolder holder, final int position) {

        holder.Ndate.setText(newslist.get(position).getdate());
        holder.Nlink.setText(newslist.get(position).getLink());

        holder.Nplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();

                extras.putString("name",newslist.get(position).getdate());
                extras.putString("link",newslist.get(position).getLink());

                Intent intent = new Intent(context, AudioPlayer.class);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return newslist.size();
    }
}
