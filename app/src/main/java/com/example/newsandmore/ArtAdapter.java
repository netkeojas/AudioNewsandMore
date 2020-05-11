package com.example.newsandmore;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtViewHolder> {

    //------------
    ListOfArt listOfArt;
    //MainActivity mainActivity;
    //------------
    ArrayList<articleModel> list;
    private OnItemClickListener mlistener;
    Context context;


    public interface OnItemClickListener{
        void onItemClick(int position);

    }

//    public void setOnItemClickListener(OnItemClickListener listener)
//    {
//        mlistener = listener;
//    }


    //Constructor
    public ArtAdapter(ListOfArt listOfArt,ArrayList<articleModel> list, Context context) {
        this.listOfArt = listOfArt;
        //this.mainActivity = mainActivity;
        this.list = list;
        this.context=context;
    }


    @NonNull
    @Override
    public ArtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //LayoutInflater layoutInflater = LayoutInflater.from(mainActivity.getBaseContext());
        LayoutInflater layoutInflater1 = LayoutInflater.from(listOfArt.getBaseContext());
        View view = layoutInflater1.inflate(R.layout.elements,null,false);

        return new ArtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ArtViewHolder holder, final int position) {
        holder.mName.setText(list.get(position).getName());
        holder.mLink.setText(list.get(position).getLink());


        // Play button function
        holder.mPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle extras = new Bundle();

                extras.putString("name",list.get(position).getName());
                extras.putString("link",list.get(position).getLink());

                Intent intent = new Intent(context, AudioPlayer.class);
                intent.putExtras(extras);
                v.getContext().startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }

}
