package com.example.proj_photouploading;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<ImageViewAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Upload> mUploads;

    public ImageViewAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder{
        private TextView textViewName;
        private ImageView imageViewImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageViewImage = itemView.findViewById(R.id.image_view_image);
        }
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.items,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Upload uploadCur = mUploads.get(position);
        holder.textViewName.setText(uploadCur.getName());
        Picasso.with(mContext)
                .load(uploadCur.getImageUrl())
                .placeholder(R.mipmap.loading_dots)
                .fit()
                .centerCrop()
                .into(holder.imageViewImage);

    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
}
