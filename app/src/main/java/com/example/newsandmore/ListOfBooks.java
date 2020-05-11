package com.example.newsandmore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ListOfBooks extends AppCompatActivity {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    ArrayList<bookModel> booklist;
    BksAdapter bksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_books);
        booklist = new ArrayList<>();
        setUpFB();
        setUpRV();
        dataFromFB();

    }


    private void dataFromFB()
    {
        if(booklist.size()>0)
        {

            booklist.clear();

        }
        db.collection("bookColl").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        for(DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            bookModel bksModel = new bookModel(documentSnapshot.getString("name"),
                                    documentSnapshot.getString("link"));
                            booklist.add(bksModel);
                        }

                        bksAdapter = new BksAdapter(ListOfBooks.this,booklist,ListOfBooks.this);
                        recyclerView.setAdapter(bksAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(ListOfBooks.this, "Something went wrong", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void setUpFB()
    {
        db = FirebaseFirestore.getInstance();
    }

    private void setUpRV()
    {

        recyclerView = findViewById(R.id.recyclerViewB);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}
