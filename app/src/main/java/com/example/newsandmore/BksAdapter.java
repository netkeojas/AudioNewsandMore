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

public class BksAdapter extends RecyclerView.Adapter<bookViewHolder> {

    ListOfBooks listOfBooks;
    ArrayList<bookModel> booklist;
    Context context;

    public BksAdapter(ListOfBooks listOfBooks, ArrayList<bookModel> booklist,Context context) {
        this.listOfBooks = listOfBooks;
        this.booklist = booklist;
        this.context = context;
    }

    @NonNull
    @Override
    public bookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(listOfBooks.getBaseContext());
        View view = layoutInflater.inflate(R.layout.bokelements,null,false);
        return new bookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookViewHolder holder, final int position) {

        holder.bname.setText(booklist.get(position).getName());
        holder.blink.setText(booklist.get(position).getLink());

        holder.Bplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();

                extras.putString("name",booklist.get(position).getName());
                extras.putString("link",booklist.get(position).getLink());

                Intent intent = new Intent(context, AudioPlayer.class);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return booklist.size();
    }
}
