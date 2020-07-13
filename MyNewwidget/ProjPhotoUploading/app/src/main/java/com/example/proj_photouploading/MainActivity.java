package com.example.proj_photouploading;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //Keys
    private static final int PICK_IMAGE_REQUEST = 1;

    //UI
    private Button mButtonChooseImage;
    private Button mButtonUploadImage;
    private TextView mTextViewShowUploads;
    private EditText mEditTextFileName;
    private ImageView mImageView;
    private ProgressBar mProgressBar;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    //Uri
    private Uri mImageUri; //Points the selected image, shows to us, uploads to firebase.

    //Task
    private Task<Uri> mUploadTask;

    //Toast
    private Toast mToast;

    //Else
    private String fileName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonChooseImage = findViewById(R.id.button_choose_file);
        mButtonUploadImage = findViewById(R.id.button_upload);
        mTextViewShowUploads = findViewById(R.id.text_show_uploads);
        mEditTextFileName = findViewById(R.id.edit_text_enter_file_name);
        mImageView = findViewById(R.id.image_view_uploading_image);
        mProgressBar = findViewById(R.id.progressbar_upload_progress);

        mStorageRef = FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef = FirebaseDatabase.getInstance().getReference("uploads");

        mButtonChooseImage.setOnClickListener(this);
        mButtonUploadImage.setOnClickListener(this);
        mTextViewShowUploads.setOnClickListener(this);
    }

    //Implemented Methods
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id) {
            case R.id.button_choose_file:
                openFileChooser();
                break;

            case R.id.button_upload:
                if (mUploadTask != null) {
                    if (mToast != null) mToast.cancel();
                    mToast = Toast.makeText(this, "Please Wait; Upload is in progress.", Toast.LENGTH_SHORT);
                    mToast.show();
                } else {
                    uploadFile();
                }
                break;

            case R.id.text_show_uploads:
                openImagesActivity();
                break;
        }
    }

    //User-Defined Methods
    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {

            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
        }
    }

    private String getFileExtension(Uri uri) {//get file extension we picked: jpg img -> .jpg
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadFile() {
        if (mImageUri != null) { //User picked image

            fileName = System.currentTimeMillis() + "." + getFileExtension(mImageUri);
            final StorageReference fileRef = mStorageRef.child(fileName); //uploads/43542...... .jpg for example

            mUploadTask = fileRef.putFile(mImageUri)
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            mProgressBar.setProgress((int) progress);
                        }
                    })
                    .continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw task.getException();
                            }
                            return fileRef.getDownloadUrl();
                        }
                    })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downloadUri = task.getResult();

                                Upload upload = new Upload(mEditTextFileName.getText().toString().trim(),
                                        downloadUri.toString());
                                upload.setFileName(fileName);

                                //Creates DB entry contains metadata of upload
                                String uploadId = mDatabaseRef.push().getKey(); //Create new entry in DB, create unique id.
                                mDatabaseRef.child(uploadId).setValue(upload); //Set the value (name of upload & image url) with unique id
                            } else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgressBar.setProgress(0); //The user wouldn't see the progress being 100% (too fast!)
                                }
                            }, 500); //Delaying for 0.5 sec; giving visual feedback to users.

                            Toast.makeText(MainActivity.this, "Upload Successful!", Toast.LENGTH_SHORT).show();

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        } else {
            Toast.makeText(this, "No file selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void openImagesActivity(){
        Intent intent = new Intent(this,ImagesActivity.class);
        startActivity(intent);
    }
}
