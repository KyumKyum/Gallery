package com.example.communitysimuator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Currency;

public class ShowContentsActivity extends AppCompatActivity {
    private static final String CUR_CONTENT = "currentcontent";
    private static final int ERROR = -1;

    private TextView yourTitle;
    private TextView yourContent;
    private ImageView yourImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contents);

        Intent intent = getIntent();
        Upload curUpload = intent.getParcelableExtra(CUR_CONTENT);

        yourTitle = findViewById(R.id.text_view_your_title);
        yourTitle.setText(curUpload.getTitle());
        yourContent = findViewById(R.id.text_view_your_content);
        yourContent.setText(curUpload.getContent());
        yourImage = findViewById(R.id.image_view_image_here);
        Glide.with(this)
                .load(curUpload.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.loading)
                .into(yourImage);

    }
}