package com.example.proj_photouploading;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
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
    private OnItemClickListener mListener;

    public ImageViewAdapter(Context context, List<Upload> uploads){
        mContext = context;
        mUploads = uploads;
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
    View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        private TextView textViewName;
        private ImageView imageViewImage;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageViewImage = itemView.findViewById(R.id.image_view_image);

            imageViewImage.setOnClickListener(this);
            imageViewImage.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(mListener != null){
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    mListener.OnItemClickListener(pos);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select");
            MenuItem itemDownload = menu.add(Menu.NONE,1,1,"Download");
            MenuItem itemDelete = menu.add(Menu.NONE,2,2,"Delete");

            itemDownload.setOnMenuItemClickListener(this);
            itemDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){

                    switch (item.getItemId()){

                        case 1:
                            mListener.OnItemDownloadClickListener(pos);
                            return true;

                        case 2:
                            mListener.OnItemDeleteClickListener(pos);
                            return true;

                    }

                }
            }

            return true;

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

    public interface OnItemClickListener{

        void OnItemClickListener(int position);
        void OnItemDownloadClickListener(int position);
        void OnItemDeleteClickListener(int position);

    }

    public void setOnClickListener(OnItemClickListener listener){
        mListener = listener;
    }
}
