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
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class Articles extends AppCompatActivity implements View.OnClickListener{

    private Button chooseBtn,uploadBtn,viewArtBtn,delArtBtn;
    public static final int PICK_AUDIO_REQUEST = 123;
    private Uri filePath;
    private TextView fileName;
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;
    private EditText articeName,artToDel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static final String Key_Link="Link";
    private static final String Key_Name="Article Name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

        storageReference = FirebaseStorage.getInstance().getReference();

        chooseBtn = (Button) findViewById(R.id.chooseA);
        uploadBtn = (Button) findViewById(R.id.uploadA);
        fileName=(TextView) findViewById(R.id.fileNameA);
        articeName = (EditText) findViewById(R.id.artName);
        viewArtBtn =(Button) findViewById(R.id.viewArticle);
        delArtBtn = (Button) findViewById(R.id.delbtnA);
        artToDel = (EditText) findViewById(R.id.delNameA);


        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        viewArtBtn.setOnClickListener(this);
        delArtBtn.setOnClickListener(this);


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
        final String artName = articeName.getText().toString();
        Log.i("check",artName);
        if(filePath != null && !artName.equals(""))
        {

            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Uploading.....");
            progressDialog.show();

            final StorageReference articleRef = storageReference.child("Articles/"+artName+".mp3");

            articleRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                            articleRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //textViewTest.setText(uri.toString());
                                    String link = uri.toString();
                                    String name = artName;

                                    /*Map<String,Object> map = new HashMap<>();
                                    map.put(Key_Name,name);
                                    map.put(Key_Link,link);*/

                                    articleModel am = new articleModel(name,link);
                                    db.collection("articleColl").document(name).set(am);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(Articles.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            articeName.setText("");
                            fileName.setText("File Name");
                            filePath = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(Articles.this, "Upload Failed", Toast.LENGTH_SHORT).show();
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
            if(filePath == null)
            //error toast
            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
            if(artName.equals(""))
                Toast.makeText(this, "Please Give a name", Toast.LENGTH_SHORT).show();
        }
    }


    private void delFile()
    {
        final String delArticle = artToDel.getText().toString();

        if(!delArticle.equals(""))
        {
            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Deleting.....");
            progressDialog.show();

            StorageReference delRef = storageReference.child("Articles/"+delArticle+".mp3");


            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Success
                    //Delete it from Database as well.

                    db.collection("articleColl").document(delArticle).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Deleted from DB
                            Toast.makeText(Articles.this, "Deleted from DB", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to deleted from db
                            Toast.makeText(Articles.this, "Failed to delete from Db", Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressDialog.dismiss();
                    Toast.makeText(Articles.this, "Deleted", Toast.LENGTH_SHORT).show();
                    artToDel.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //Failed
                    progressDialog.dismiss();
                    Toast.makeText(Articles.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
            });


        }
        else
        {
            Toast.makeText(this, "Enter article name", Toast.LENGTH_SHORT).show();
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
        else if(view == viewArtBtn)
        {
            Intent intent = new Intent(getApplicationContext(),ListOfArt.class);
            startActivity(intent);
        }
        else if(view == delArtBtn)
        {
            delFile();
        }

    }
}
