package com.example.newsandmore;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class newsViewHolder extends RecyclerView.ViewHolder {

    TextView Ndate;
    TextView Nlink;
    Button Nplay;

    public newsViewHolder(@NonNull View itemView) {
        super(itemView);

        Nplay = itemView.findViewById(R.id.playN);
        Ndate = itemView.findViewById(R.id.date);
        Nlink = itemView.findViewById(R.id.linkN);
    }
}
