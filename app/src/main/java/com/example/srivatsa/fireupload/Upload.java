package com.example.srivatsa.fireupload;

/**
 * Created by srivatsa on 07-04-2018.
 */

public class Upload {
    private String mName;
    private String mImageUrl;

    public Upload() {
        // empty constructor needed!
    }
    public Upload(String name, String imageUrl) {
        if(name.trim().equals("")) {
            name="No Name";
        }
        mName=name;
        mImageUrl=imageUrl;
    }
    public String getName() {
        return mName;
    }
    public void setName(String name) {
        mName=name;
    }
    public String getmImageUrl() {
        return mImageUrl;
    }
    public void setmImageUrl(String imageUrl) {
        mName=imageUrl;
    }
}
