package com.example.newsandmore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class News extends AppCompatActivity implements View.OnClickListener{

    private Button chooseBtn,uploadBtn,viewNwsBtn;
    public static final int PICK_AUDIO_REQUEST = 123;
    private Uri filePath;
    private TextView textView;
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;
    private EditText editText;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static final String Key_Link="Link";
    private static final String Key_Name="News Date";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        storageReference = FirebaseStorage.getInstance().getReference();

        chooseBtn = (Button) findViewById(R.id.chooseN);
        uploadBtn = (Button) findViewById(R.id.uploadN);
        textView=(TextView) findViewById(R.id.textView2N);
        editText = (EditText) findViewById(R.id.editText4N);
        viewNwsBtn =(Button) findViewById(R.id.viewNews);


        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        viewNwsBtn.setOnClickListener(this);

    }


    //Opens the mobile storage to choose file
    private void showfileChooser()
    {
        Intent intent = new Intent();
        intent.setType("audio/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select an audio"),PICK_AUDIO_REQUEST);
    }


    //Getting file name from uri
    String getFilename(Uri uri)
    {
        String result = null;
        if(uri.getScheme().equals("content"))
        {
            Cursor cursor = getContentResolver().query(uri,null,null,null,null);
            try {
                if(cursor != null && cursor.moveToFirst())
                {
                    result= cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }


        if(result==null)
        {
            result=uri.getPath();
            int cut = result.lastIndexOf('/');
            if(cut != -1)
            {
                result=result.substring(cut+1);
            }
        }

        return result;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==PICK_AUDIO_REQUEST && resultCode==RESULT_OK && data !=null && data.getData() !=null)
        {
            filePath = data.getData();

            try {

                textView.setText(getFilename(filePath));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Uploading file to Firestore
    private void uploadFile()
    {
        final String dateOfNews = editText.getText().toString();
        if(filePath != null && !dateOfNews.equals(""))
        {

            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Uploading.....");
            progressDialog.show();

            final StorageReference newsRef = storageReference.child("News/"+dateOfNews+".mp3");

            newsRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                            newsRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //textViewTest.setText(uri.toString());
                                    String link = uri.toString();
                                    String name = dateOfNews;

                                    /*Map<String,Object> map = new HashMap<>();
                                    map.put(Key_Name,name);
                                    map.put(Key_Link,link);*/

                                    newsModel nm = new newsModel(name,link);
                                    db.collection("newsColl").document().set(nm);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(News.this, "Uploaded", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(News.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                    double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(((int)progress) + "% uploaded");
                }
            });


        }
        else
        {
            if (filePath == null)
            //error toast
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();

            if(dateOfNews.equals(""))
                Toast.makeText(this, "Enter a date", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {

        if(view == chooseBtn)
        {
            showfileChooser();
        }

        else if(view == uploadBtn)
        {
            uploadFile();
        }
        else if(view == viewNwsBtn)
        {
            Intent intent = new Intent(getApplicationContext(),ListOfNews.class);
            startActivity(intent);
        }

    }
}
