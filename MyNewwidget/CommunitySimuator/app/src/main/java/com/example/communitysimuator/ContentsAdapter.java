package com.example.communitysimuator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ContentsViewHolder>{

    public Context mContext;
    public List<Upload> mUploads;

    public ContentsAdapter (Context context, List<Upload> uploads){
        this.mContext = context;
        this.mUploads = uploads;
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder {

        public TextView mTextView;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.text_view_title_here);
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
        holder.mTextView.setText(uploadCur.getTitle());
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }
}