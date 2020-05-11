package com.example.newsandmore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Value;

import java.util.ArrayList;
import java.util.List;

public class ListOfArt extends AppCompatActivity {

    RecyclerView recyclerView;
    //ProgressBar progressBar;
    ArrayList<articleModel> list;
    private StorageReference storageReference;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference aColl = db.collection("articleColl");
    ArtAdapter artAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_art);

        storageReference = FirebaseStorage.getInstance().getReference();

        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        //progressBar = (ProgressBar)findViewById(R.id.progressBarShowArticles);
        list = new ArrayList<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataFromFb();


    }

    private void dataFromFb()
    {
        if(list.size()>0)
        {
            list.clear();
        }

        aColl.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            articleModel am = new articleModel(documentSnapshot.getString("name")
                                    ,documentSnapshot.getString("link"));
                            list.add(am);
                        }

                        artAdapter = new ArtAdapter(ListOfArt.this,list,ListOfArt.this);
                        recyclerView.setAdapter(artAdapter);

//                        artAdapter.setOnItemClickListener(new ArtAdapter.OnItemClickListener() {
//                            @Override
//                            public void onItemClick(int position) {
//
//                                Intent intent = new Intent(getApplicationContext(),MusicPlayer.class);
//                                intent.putExtra("aName",list.get(position).getName());
//                                intent.putExtra("aLink",list.get(position).getLink());
//                                startActivity(intent);
//                            }
//                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ListOfArt.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
