package com.example.communitysimuator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class WriteContent extends AppCompatActivity {

    private final static int SELECT_UPLOAD = 512;

    private DatabaseReference mDBRef;
    private StorageReference mStorageRef;

    private ImageView mImageView;
    private EditText mTitle;
    private EditText mContent;
    private Uri mImageUrl;
    private ProgressBar mProgressBar;
    private Button submitButton;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_UPLOAD && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUrl = data.getData();

            Glide.with(this)
                    .load(mImageUrl)
                    .centerCrop()
                    .sizeMultiplier((float) 0.5)
                    .placeholder(R.drawable.loading)
                    .into(mImageView);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_content);

        mImageView = findViewById(R.id.image_view_your_image);
        mTitle = findViewById(R.id.edit_text_your_title);
        mContent = findViewById(R.id.edit_text_your_content);
        submitButton = findViewById(R.id.button_submit);
        mProgressBar = findViewById(R.id.progress_bar_your_progress);
        mProgressBar.setVisibility(View.INVISIBLE);

        mDBRef = FirebaseDatabase.getInstance().getReference("Uploads");
        mStorageRef = FirebaseStorage.getInstance().getReference("Uploads");

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, SELECT_UPLOAD);
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String title = mTitle.getText().toString();
                final String content = mContent.getText().toString();

                if (title.trim().length() > 0 && content.trim().length() > 0) {

                    if (mImageUrl != null) {
                        //Saving Imagefile in Storage
                        final String fileName = System.currentTimeMillis() + "." + getFileExtension(mImageUrl);
                        final StorageReference fileRef = mStorageRef.child(fileName.trim());

                        fileRef.putFile(mImageUrl)
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        mProgressBar.setVisibility(View.VISIBLE);
                                        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                                    }
                                })
                                .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                    @Override
                                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                        if (!task.isSuccessful()) {
                                            throw task.getException();
                                        } else {
                                            return fileRef.getDownloadUrl();
                                        }
                                    }
                                })
                                .addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        //Saving Image file Complete; Saving content information in DB
                                        if (task.isSuccessful()) {


                                            String uploadId = mDBRef.push().getKey();

                                            Uri downloadUri = task.getResult();
                                            Upload upload = new Upload(title, content, downloadUri.toString());
                                            upload.setKey(uploadId);
                                            mDBRef.child(uploadId).setValue(upload);
                                        }
                                    }
                                })
                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Intent intent = new Intent();
                                        setResult(Activity.RESULT_OK);
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WriteContent.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(WriteContent.this, "You didn't uploaded any image file.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(WriteContent.this, "You have to enter at least on character on title or context.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private String getFileExtension(Uri imageUri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(imageUri));
    }
}