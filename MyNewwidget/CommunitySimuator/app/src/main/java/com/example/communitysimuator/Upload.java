package com.example.communitysimuator;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

public class Upload implements Parcelable {

    private String title;
    private String content;
    private String imageUrl;
    private String key;

    public Upload(){
        //No-args constructor needed
    }

    public Upload(String title, String content, String imageUri) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUri;
    }

    protected Upload(Parcel in) {
        title = in.readString();
        content = in.readString();
        imageUrl = in.readString();
        key = in.readString();
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(imageUrl);
        dest.writeString(key);
    }
}
