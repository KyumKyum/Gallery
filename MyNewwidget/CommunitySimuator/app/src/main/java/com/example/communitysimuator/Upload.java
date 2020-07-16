package com.example.communitysimuator;

import android.os.Parcel;
import android.os.Parcelable;

public class Upload implements Parcelable {

    private String title;
    private String content;

    public Upload(){
        //No-args constructor needed
    }

    public Upload(String title, String content) {
        this.title = title;
        this.content = content;
    }

    protected Upload(Parcel in) {
        title = in.readString();
        content = in.readString();
    }

    public static final Creator<Upload> CREATOR = new Creator<Upload>() {
        @Override
        public Upload createFromParcel(Parcel in) {
            return new Upload(in);
        }

        @Override
        public Upload[] newArray(int size) {
            return new Upload[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
    }
}
