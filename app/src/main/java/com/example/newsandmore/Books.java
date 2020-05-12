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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Books extends AppCompatActivity implements View.OnClickListener{

    private Button chooseBtn,uploadBtn,viewBksBtn,delBksBtn;
    public static final int PICK_AUDIO_REQUEST = 123;
    private Uri filePath;
    private TextView fileName;
    private MediaPlayer mediaPlayer;
    private StorageReference storageReference;
    private EditText nameOfBook,bookToDel;
    FirebaseFirestore db = FirebaseFirestore.getInstance();


    private static final String Key_Link="Link";
    private static final String Key_Name="Book Name";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books);

        storageReference = FirebaseStorage.getInstance().getReference();

        chooseBtn = (Button) findViewById(R.id.chooseB);
        uploadBtn = (Button) findViewById(R.id.uploadB);
        fileName=(TextView) findViewById(R.id.fileNameB);
        nameOfBook = (EditText) findViewById(R.id.bookName);
        viewBksBtn =(Button) findViewById(R.id.viewBooks);
        delBksBtn = (Button) findViewById(R.id.delbtnB);
        bookToDel = (EditText) findViewById(R.id.delNameB);


        chooseBtn.setOnClickListener(this);
        uploadBtn.setOnClickListener(this);
        viewBksBtn.setOnClickListener(this);
        delBksBtn.setOnClickListener(this);

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
        final String bookName = nameOfBook.getText().toString();
        if(filePath != null && !bookName.equals(""))
        {

            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Uploading.....");
            progressDialog.show();

            final StorageReference bookRef = storageReference.child("Books/"+bookName+".mp3");

            bookRef.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Get a URL to the uploaded content

                            bookRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //textViewTest.setText(uri.toString());
                                    String link = uri.toString();
                                    String name = bookName;

                                    bookModel bm = new bookModel(name,link);
                                    db.collection("bookColl").document(name).set(bm);
                                }
                            });
                            progressDialog.dismiss();
                            Toast.makeText(Books.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            nameOfBook.setText("");
                            fileName.setText("Book");
                            filePath = null;

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            // ...
                            progressDialog.dismiss();
                            Toast.makeText(Books.this, "Upload Failed", Toast.LENGTH_SHORT).show();
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

            if(bookName.equals(""))
                Toast.makeText(this, "Please Enter Book name", Toast.LENGTH_SHORT).show();
        }
    }

    //Deleting a file from FireStore
    private void delFile()
    {
        final String delBook = bookToDel.getText().toString();

        if(!delBook.equals(""))
        {
            final ProgressDialog progressDialog =new ProgressDialog(this);
            progressDialog.setTitle("Deleting.....");
            progressDialog.show();

             StorageReference delRef = storageReference.child("Books/"+delBook+".mp3");


            delRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    //Success
                    //Delete it from Database as well.

                    db.collection("bookColl").document(delBook).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            //Deleted from DB
                            Toast.makeText(Books.this, "Deleted from DB", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //Failed to deleted from db
                            Toast.makeText(Books.this, "Failed to delete from Db", Toast.LENGTH_SHORT).show();
                        }
                    });

                    progressDialog.dismiss();
                    Toast.makeText(Books.this, "Deleted", Toast.LENGTH_SHORT).show();
                    bookToDel.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                   //Failed
                    progressDialog.dismiss();
                    Toast.makeText(Books.this, "Deletion Failed", Toast.LENGTH_SHORT).show();
                }
            });


        }
        else
        {
            Toast.makeText(this, "Enter book name", Toast.LENGTH_SHORT).show();
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
        else if(view == viewBksBtn)
        {
            Intent intent = new Intent(getApplicationContext(),ListOfBooks.class);
            startActivity(intent);

        }
        else if(view == delBksBtn)
        {
            delFile();
        }

    }
}
