package com.example.proj_photouploading;

public class Upload {

    private String mName;
    private String mImageUrl;

    public Upload(){
        //No-args constructor required
    }

    public Upload(String mName, String mImageUrl) {
        this.mName = mName;
        this.mImageUrl = mImageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }
}
