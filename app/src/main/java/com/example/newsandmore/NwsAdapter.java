package com.example.newsandmore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class NwsAdapter extends RecyclerView.Adapter<newsViewHolder> {

    ListOfNews listOfNews;
    ArrayList<newsModel> newslist;

    public NwsAdapter(ListOfNews listOfNews, ArrayList<newsModel> newslist) {
        this.listOfNews = listOfNews;
        this.newslist = newslist;
    }

    @NonNull
    @Override
    public newsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(listOfNews.getBaseContext());
        View view = layoutInflater.inflate(R.layout.nwselements,null,false);
        return new newsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsViewHolder holder, int position) {

        holder.Ndate.setText(newslist.get(position).getdate());
        holder.Nlink.setText(newslist.get(position).getLink());

    }

    @Override
    public int getItemCount() {
        return newslist.size();
    }
}
