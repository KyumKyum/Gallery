package com.example.communitysimuator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, ContentsAdapter.OnItemClickListener {

    private static final int WRITE_CONTENT = 101;
    private static final String CUR_CONTENT = "currentcontent";

    private FloatingActionButton writeButton;
    private RecyclerView mRecyclerView;
    private ContentsAdapter mAdapter;
    private DatabaseReference mDBRef;
    private FirebaseStorage mStorageRef;
    private ValueEventListener mValueListener;

    private List<Upload> mUploads;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        writeButton = findViewById(R.id.button_write_content);
        writeButton.setOnClickListener(this);

        mRecyclerView = findViewById(R.id.recycler_view_contents);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mUploads = new ArrayList<>();

        mAdapter = new ContentsAdapter(this,mUploads);
        mAdapter.setOnClickListener(this);

        mRecyclerView.setAdapter(mAdapter);

        mStorageRef = FirebaseStorage.getInstance();
        mDBRef = FirebaseDatabase.getInstance().getReference("Uploads");

        mValueListener = mDBRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUploads.clear();
                for(DataSnapshot postDataSnapshots : dataSnapshot.getChildren()){
                    Upload uploadCur = postDataSnapshots.getValue(Upload.class);
                    mUploads.add(uploadCur);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch (id){
            case R.id.button_write_content:
                Intent intent = new Intent(this, WriteContent.class);
                startActivityForResult(intent,WRITE_CONTENT);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == WRITE_CONTENT && resultCode == RESULT_OK){
            Toast.makeText(this, "Uploaded!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void OnItemClickListener(int position) {
        Intent intent = new Intent(getApplicationContext(),ShowContentsActivity.class);
        Upload curUpload = mUploads.get(position);
        intent.putExtra(CUR_CONTENT,curUpload);
        startActivity(intent);
    }

    @Override
    public void OnItemDeleteClickListener(int position) {

        final int targetPos = position;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Are you going to delete this content?");
        builder.setMessage("The result is not recoverable!");

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Upload target = mUploads.get(targetPos);
                final String targetKey = target.getKey();

                StorageReference targetRef = mStorageRef.getReferenceFromUrl(target.getImageUrl());

                targetRef.delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                mDBRef.child(targetKey).removeValue();
                                Toast.makeText(MainActivity.this, "Successfully Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}