package com.example.newsandmore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MusicPlayer extends AppCompatActivity {

    Button btn_pause,btn_next,btn_prev;
    TextView name;
    SeekBar seekBar;
    static MediaPlayer mediaPlayer;
    int position;
    String aName,aLink;

    Thread updateSeek;

    private StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference aColl = db.collection("articleColl");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        btn_pause = (Button) findViewById(R.id.pause);
        btn_next = (Button) findViewById(R.id.next);
        btn_prev = (Button) findViewById(R.id.prev);
        name = (TextView) findViewById(R.id.nText);
        seekBar =(SeekBar) findViewById(R.id.seekBar);


        updateSeek = new Thread(){
            @Override
            public void run() {
                super.run();

                int totalDuration = mediaPlayer.getDuration();
                int currentPosition = 0;
                while(currentPosition < totalDuration)
                {
                    try {
                        sleep(500);
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);

                    }
                    catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        };

        if(mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }

        storageReference = FirebaseStorage.getInstance().getReference();

        Intent i = getIntent();
        Bundle bundle =i.getExtras();
        aName = i.getStringExtra("aName");
        aLink=i.getStringExtra("aLink");

        name.setText(aName);
        name.setSelected(true);

        Uri uri = Uri.parse(aLink);

        mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeek.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());

            }
        });

        btn_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBar.setMax(mediaPlayer.getDuration());

                if(mediaPlayer.isPlaying())
                {
                    mediaPlayer.pause();
                    btn_pause.setBackgroundResource(R.drawable.ic_play_arrow_black_24dp);
                }
                else {

                    btn_pause.setBackgroundResource(R.drawable.ic_pause_black_24dp);
                    mediaPlayer.start();
                }
            }
        });
    }

}
