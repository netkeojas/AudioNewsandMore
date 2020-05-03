package com.example.newsandmore;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

public class ArtAdapter extends RecyclerView.Adapter<ArtAdapter.ArtViewHolder> {

    //------------
    ListOfArt listOfArt;
    //MainActivity mainActivity;
    //------------
    ArrayList<articleModel> list;
    private OnItemClickListener mlistener;


    public interface OnItemClickListener{
        void onItemClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener)
    {
        mlistener = listener;
    }


    //Constructor
    public ArtAdapter(ListOfArt listOfArt,ArrayList<articleModel> list) {
        this.listOfArt = listOfArt;
        //this.mainActivity = mainActivity;
        this.list = list;
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
            public void onClick(View view) {
                pAudio(holder.mName.getContext(),list.get(position).getLink());
            }
        });

        //Stop button function
        holder.mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sAudio(holder.mName.getContext(),list.get(position).getLink());
            }
        });
    }

    MediaPlayer mediaPlayer = new MediaPlayer();
    public void pAudio(Context context,String url)  {
        Uri uri= Uri.parse(url);

        try {
            mediaPlayer.setDataSource(context,uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaPlayer.start();
    }

    public void sAudio(Context context,String url)  {
        Uri uri= Uri.parse(url);
        //MediaPlayer mediaPlayer = new MediaPlayer();
        mediaPlayer.stop();
        mediaPlayer.release();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ArtViewHolder extends RecyclerView.ViewHolder {

        TextView mName,mLink;
        Button mPlay,mStop;

        public ArtViewHolder(@NonNull View itemView) {
            super(itemView);
            mName=itemView.findViewById(R.id.nameA);
            mLink=itemView.findViewById(R.id.linkA);
            mPlay=itemView.findViewById(R.id.playA);
            mStop=itemView.findViewById(R.id.stopA);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if(mlistener!=null)
                    {
                        int position = getAdapterPosition();
                        if(position!=RecyclerView.NO_POSITION){
                            mlistener.onItemClick(position);
                        }
                    }

                }
            });

        }
    }


}
