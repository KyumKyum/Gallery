package com.example.communitysimuator;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ContentsAdapter extends RecyclerView.Adapter<ContentsAdapter.ContentsViewHolder>{

    private Context mContext;
    private List<Upload> mUploads;
    private OnItemClickListener mListener;


    public ContentsAdapter (Context context, List<Upload> uploads){
        this.mContext = context;
        this.mUploads = uploads;
    }

    public class ContentsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mTextView;
        public LinearLayout mLinearLayout;

        public ContentsViewHolder(@NonNull View itemView) {
            super(itemView);

            mTextView = itemView.findViewById(R.id.text_view_title_here);
            mLinearLayout = itemView.findViewById(R.id.item_layout);
            mLinearLayout.setOnClickListener(this);
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

    public interface OnItemClickListener{
        void OnItemClickListener(int position);
    }

    public void setOnClickListener(OnItemClickListener listener){
        this.mListener = listener;
    }

}