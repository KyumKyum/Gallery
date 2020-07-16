package com.example.communitysimuator;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ContentsViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;


    public ContentsAdapter (Context context, List<Upload> uploads){
        this.mContext = context;
        this.mUploads = uploads;
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
            , View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {

        public TextView mTextView;
        public LinearLayout mLinearLayout;
        public ImageView mThumbnail;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.text_view_title_here);
            mThumbnail = itemView.findViewById(R.id.image_view_thumbnail_here);
            mLinearLayout = itemView.findViewById(R.id.item_layout);
            mLinearLayout.setOnClickListener(this);
            mLinearLayout.setOnCreateContextMenuListener(this);
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
            menu.setHeaderTitle("Delete this file?");
            MenuItem itemDelete = menu.add(Menu.NONE,1,1,"DELETE");

            itemDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(mListener != null){
                int pos = getAdapterPosition();
                if(pos != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            mListener.OnItemDeleteClickListener(pos);
                    }
                }
            }
            return true;
        }
    }

    @NonNull
    @Override
    public ContentsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View v = inflater.inflate(R.layout.item_layout,parent,false);
        return new ContentsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ContentsViewHolder holder, int position) {
        Upload uploadCur = mUploads.get(position);
        holder.mTextView.setText("Title: " + uploadCur.getTitle());
        Glide.with(mContext)
                .load(uploadCur.getImageUrl())
                .placeholder(R.drawable.loading)
                .centerCrop()
                .into(holder.mThumbnail);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public interface OnItemClickListener{
        void OnItemClickListener(int position);
        void OnItemDeleteClickListener(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}