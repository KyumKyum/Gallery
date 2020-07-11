package com.example.proj_photouploading;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

    //Uri
    private Uri mImageUri; //Points the selected image, shows to us, uploads to firebase.

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
                break;

            case R.id.text_show_uploads:
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
}
