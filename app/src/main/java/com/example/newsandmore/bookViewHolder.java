package com.example.newsandmore;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class bookViewHolder extends RecyclerView.ViewHolder {

    TextView bname;
    TextView blink;
    Button Bplay;

    public bookViewHolder(@NonNull View itemView) {
        super(itemView);

        Bplay = itemView.findViewById(R.id.playB);
        bname = itemView.findViewById(R.id.bname);
        blink = itemView.findViewById(R.id.linkB);


    }
}
