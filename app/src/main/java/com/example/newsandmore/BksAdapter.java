package com.example.newsandmore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BksAdapter extends RecyclerView.Adapter<bookViewHolder> {

    ListOfBooks listOfBooks;
    ArrayList<bookModel> booklist;

    public BksAdapter(ListOfBooks listOfBooks, ArrayList<bookModel> booklist) {
        this.listOfBooks = listOfBooks;
        this.booklist = booklist;
    }

    @NonNull
    @Override
    public bookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(listOfBooks.getBaseContext());
        View view = layoutInflater.inflate(R.layout.bokelements,null,false);
        return new bookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull bookViewHolder holder, int position) {

        holder.bname.setText(booklist.get(position).getName());
        holder.blink.setText(booklist.get(position).getLink());

    }

    @Override
    public int getItemCount() {
        return booklist.size();
    }
}
