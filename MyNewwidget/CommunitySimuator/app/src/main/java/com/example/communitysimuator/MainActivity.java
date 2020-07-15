package com.example.communitysimuator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int WRITE_CONTENT = 101;

    private FloatingActionButton writeButton;
    private RecyclerView mRecyclerView;
    private ContentsAdapter mAdapter;
    private DatabaseReference mDBRef;
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

        mRecyclerView.setAdapter(mAdapter);

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

        }
    }
}