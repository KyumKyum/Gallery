package com.example.practicingviewpager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import java.util.List;

public class ContentAdapter extends PagerAdapter {

    List<Content> contents;
    Context context;

    public ContentAdapter(List<Content> contents, Context context) {
        this.contents = contents;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contents.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View v = layoutInflater.inflate(R.layout.items,container,false);

        ImageView imageView;
        TextView title;
        TextView description;

        imageView = v.findViewById(R.id.your_image);
        title = v.findViewById(R.id.text_view_title);
        description = v.findViewById(R.id.text_view_desc);

        imageView.setImageResource(contents.get(position).getImage());
        title.setText(contents.get(position).getTitle());
        description.setText(contents.get(position).getDescription());

        container.addView(v,0);

        return v;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        View v = (View)object;
        container.removeView(v);
    }
}
