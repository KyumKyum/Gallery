package com.example.practicingviewpager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.animation.ArgbEvaluator;
import android.media.Image;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    ViewPager viewPager;
    List<Content> contentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        contentList = new ArrayList<>();
        contentList.add(new Content(R.drawable.donut_chocolate_frosted, "Chocolate Frosted Donut", "Donut frosted with high - quality chocolate cream.\nYou gonna like it!"));
        contentList.add(new Content(R.drawable.donut, "Strawberry Frosted Donut", "Donut frosted with strawberry cream!\nThe savor of this donut will make you feel happy!"));
        contentList.add(new Content(R.drawable.donut_cream_frosted, "Cream Frosted Donut", "A harmonious combination of donut and gorgeous cream.\nOnly available in Lim's Donut!"));
        contentList.add(new Content(R.drawable.donut_lists, "Want to see more donuts?", "Visit our store!\nLim's Donut!'"));

        final ContentAdapter mAdapter = new ContentAdapter(contentList, this);

        viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(mAdapter);
        viewPager.setPadding(130, 0, 130, 0);
    }
}