package com.example.hp.tiptip;

import java.io.Serializable;

public class Contact implements Serializable {
    private String mName;
    private String mId;
    private int mType;

    public Contact(String id, int type) {
        mId = id;
        mType = type;
    }

    public Contact(String id, String name, int type){
        mName = name;
        mId = id;
        mType = type;
    }


    public String getmName() {
        return mName;
    }

    public int getmType() {
        return mType;
    }

    public String getmId() {
        return mId;
    }
}
