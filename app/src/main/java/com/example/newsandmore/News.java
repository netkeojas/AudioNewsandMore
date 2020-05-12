package com.example.newsandmore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Calendar;

public class News extends AppCompatActivity implements View.OnClickListener{

    private Button chooseBtn,uploadBtn,viewNwsBtn,delNwsBtn;
    public static final int PICK_AUDIO_REQUEST = 123;
    private Uri filePath;
    private TextView fileName;
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;
    private TextView newsDate,newsToDel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String date="";

    private DatePickerDialog.OnDateSetListener dateSetListener1,dateSetListener2;


    private static final String Key_Link="Link";
    private static final String Key_Name="News Date";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        storageReference = FirebaseStorage.getInstance().getReference();

        chooseBtn = (Button) findViewById(R.id.chooseN);
        uploadBtn = (Button) findViewById(R.id.uploadN);
        //Displays name of the file
        fileName=(TextView) findViewById(R.id.fileNameN);
        //Displays Date
        newsDate = (TextView) findViewById(R.id.newsDate);
        viewNwsBtn =(Button) findViewById(R.id.viewNews);
        delNwsBtn = (Button) findViewById(R.id.delbtnN);
        newsToDel = (TextView) findViewById(R.id.delNameN);


        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        viewNwsBtn.setOnClickListener(this);
        delNwsBtn.setOnClickListener(this);
        newsDate.setOnClickListener(this);
        newsToDel.setOnClickListener(this);

        setDate1();
        setDate2();


    }

    //Date Picker
    private void setDate1()
    {
        newsDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        News.this,
                        android.R.style.Theme_Holo_Dialog,
                        dateSetListener1,
                        year,month,day
                );
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener1 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month+1;

                String date = month + "-" + day + "-" + year;
                Log.d("Date","onDateSet : Date (dd-mm-yyyy):" + day + "-" + month + "-" + year);
                newsDate.setText(date);

            }
        };
    }

    private void setDate2()
    {
        newsToDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(
                        News.this,
                        android.R.style.Theme_Holo_Dialog,
                        dateSetListener2,
                        year,month,day
                );
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        dateSetListener2 = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {

                month = month+1;

                String date = month + "-" + day + "-" + year;
                Log.d("Date","onDateSet : Date (dd-mm-yyyy):" + day + "-" + month + "-" + year);
                newsToDel.setText(date);

            }
        };
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

                fileName.setText(getFilename(filePath));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //Uploading file to Firestore
    private void uploadFile()
    {
        final String dateOfNews = newsDate.getText().toString();
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
                                    db.collection("newsColl").document(name).set(nm);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(News.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            newsDate.setText("");
                            fileName.setText("");
                            filePath = null;

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


    //Deleting a file from FireStore
    private void delFile()
    {
        final String delNews = newsToDel.getText().toString();

        if(!delNews.equals(""))
        {
            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Deleting.....");
            progressDialog.show();

            StorageReference delRef = storageReference.child("News/"+delNews+".mp3");


            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Success
                    //Delete it from Database as well.

                    db.collection("newsColl").document(delNews).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Deleted from DB
                            Toast.makeText(News.this, "Deleted from DB", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to deleted from db
                            Toast.makeText(News.this, "Failed to delete from Db", Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressDialog.dismiss();
                    Toast.makeText(News.this, "Deleted", Toast.LENGTH_SHORT).show();
                    newsToDel.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Failed
                    progressDialog.dismiss();
                    Toast.makeText(News.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
            });


        }
        else
        {
            Toast.makeText(this, "Enter news Date", Toast.LENGTH_SHORT).show();
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
        else if(view == delNwsBtn)
        {
            delFile();
        }
//        else if(view == newsToDel || view == newsDate)
//        {
//            Calendar cal = Calendar.getInstance();
//                int year = cal.get(Calendar.YEAR);
//                int month = cal.get(Calendar.MONTH);
//                int day = cal.get(Calendar.DAY_OF_MONTH);
//
//                DatePickerDialog dialog = new DatePickerDialog(
//                        News.this,
//                        android.R.style.Theme_Holo_Dialog,
//                        dateSetListener,
//                       year,month,day
//                );
//                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
//                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                dialog.show();
//
//                if(view == newsToDel)
//                {
//                    newsToDel.setText(setDate(view));
//                }
//                else
//                {
//                    newsDate.setText(setDate(view));
//                }
//        }

    }
}
