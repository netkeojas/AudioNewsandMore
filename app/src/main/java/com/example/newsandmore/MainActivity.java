package com.example.newsandmore;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

        public void opnArticles(View view)
    {
        Intent intent = new Intent(getApplicationContext(),Articles.class);
        startActivity(intent);
    }

    public void opnNews(View view)
    {
        Intent intent = new Intent(getApplicationContext(),News.class);
        startActivity(intent);
    }

    public void opnBooks(View view)
    {
        Intent intent = new Intent(getApplicationContext(),Books.class);
        startActivity(intent);
    }

    public void opnFeedback(View view)
    {
        Intent intent = new Intent(getApplicationContext(),Feedback.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
