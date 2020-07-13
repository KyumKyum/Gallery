package com.example.proj_photouploading;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class ImagesActivity extends AppCompatActivity implements ImageViewAdapter.OnItemClickListener {
    private static final int WRITE_PERMISSION = 1001;
    private RecyclerView mRecyclerView;
    private ProgressBar mProgressBar;

    private FirebaseStorage mStorage;
    private ImageViewAdapter mAdapter;
    private DatabaseReference mDBRef;
    private ValueEventListener mDBListener;
    private List<Upload> mUploads;
    private String mUri;
    private String mFileName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // Default: Vertical

        mProgressBar = findViewById(R.id.progress_bar_circular);

        mUploads = new ArrayList<>();

        mAdapter = new ImageViewAdapter(ImagesActivity.this, mUploads);

        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnClickListener(ImagesActivity.this);

        mStorage = FirebaseStorage.getInstance();

        mDBRef = FirebaseDatabase.getInstance().getReference("uploads");

        mDBListener = mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                mUploads.clear();

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Upload upload = postSnapshot.getValue(Upload.class);
                    upload.setKey(postSnapshot.getKey());
                    mUploads.add(upload);
                }

                mAdapter.notifyDataSetChanged();
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //EX) the user doesn't have permission to read data.
                Toast.makeText(ImagesActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    @Override
    public void OnItemClickListener(int position) {
        //Toast.makeText(this, "Debug: Item Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void OnItemDownloadClickListener(int position) {
        final Upload targetItem = mUploads.get(position);
        StorageReference targetRef = mStorage.getReference()
                .child("uploads")
                .child(targetItem.getFileName());

        targetRef.getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (ContextCompat.checkSelfPermission(ImagesActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                    == PackageManager.PERMISSION_GRANTED) {
                                //Permission Granted; Start Download
                                String downloadUri = uri.toString();
                                startDownload(ImagesActivity.this, targetItem.getFileName(), downloadUri);
                            } else {
                                //Permission Not Granted.
                                mUri = uri.toString();
                                mFileName = targetItem.getFileName();
                                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_PERMISSION);
                            }
                        } else {
                            //Android Version lesser than 'M'
                            String downloadUri = uri.toString();
                            startDownload(ImagesActivity.this, targetItem.getFileName(), downloadUri);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void OnItemDeleteClickListener(final int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete this photo?");
        builder.setMessage("Deleted item is not recoverable!");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Upload targetItem = mUploads.get(position);
                final String targetKey = targetItem.getKey();

                StorageReference targetRef = mStorage.getReferenceFromUrl(targetItem.getImageUrl());
                targetRef.delete() // automatically call onDataChange() Method, notifying changes.
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDBRef.child(targetKey).removeValue();
                                Toast.makeText(ImagesActivity.this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ImagesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private void startDownload(Context context, String fileName, String uri) {
        Uri targetUri = Uri.parse(uri.trim());

        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        String targetMime = mime.getExtensionFromMimeType(cR.getType(targetUri));

        try {

            //Creating Request
            DownloadManager.Request request = new DownloadManager.Request(targetUri);

            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI) //Required Networks to downloads
                    .setTitle(fileName)
                    .setDescription("Downloading " + fileName + "...")
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                    .setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, "" + System.currentTimeMillis())
                    .setMimeType(targetMime);


            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            Toast.makeText(context, "Downloading Started!", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == WRITE_PERMISSION) {
            if (grantResults != null && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startDownload(ImagesActivity.this, mFileName, mUri);
            } else {
                Toast.makeText(this, "Perimission Denied", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mDBRef.removeEventListener(mDBListener);
    }
}
