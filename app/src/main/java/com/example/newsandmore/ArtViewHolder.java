package com.example.newsandmore;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ArtViewHolder extends RecyclerView.ViewHolder {

    TextView mName,mLink;
    Button mPlay;

    public ArtViewHolder(@NonNull View itemView) {
        super(itemView);

        mName=itemView.findViewById(R.id.nameA);
        mLink=itemView.findViewById(R.id.linkA);
        mPlay=itemView.findViewById(R.id.playA);

    }
}
