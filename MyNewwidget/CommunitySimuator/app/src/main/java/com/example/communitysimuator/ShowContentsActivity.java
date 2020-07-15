package com.example.communitysimuator;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import java.util.Currency;

public class ShowContentsActivity extends AppCompatActivity {
    private static final String CUR_CONTENT = "currentcontent";
    private static final int ERROR = -1;

    private TextView yourTitle;
    private TextView yourContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_contents);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String content = intent.getStringExtra("content");

        yourTitle = findViewById(R.id.text_view_your_title);
        yourContent = findViewById(R.id.text_view_your_content);

        yourTitle.setText(title);
        yourContent.setText(content);
    }
}