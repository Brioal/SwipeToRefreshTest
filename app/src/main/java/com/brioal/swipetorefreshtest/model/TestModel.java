package com.brioal.swipetorefreshtest.model;

/**
 * Created by brioal on 16-4-25.
 */
public class TestModel {
    private String mTitle;
    private String mDesc;
    private String mTime;

    public TestModel(String mTitle, String mDesc, String mTime) {
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mTime = mTime;
    }

    public String getmTitle() {
        return mTitle;
    }

    public void setmTitle(String mTitle) {
        this.mTitle = mTitle;
    }


    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmTime() {
        return mTime;
    }

    public void setmTime(String mTime) {
        this.mTime = mTime;
    }


    @Override
    public boolean equals(Object o) {
        TestModel model = (TestModel) o;
        if (!mTitle.equals(model.getmTitle())) {
            return false;
        } else if (!mDesc.equals(model.getmDesc())) {
            return false;
        } else if (!mTime.equals(model.getmTitle())) {
            return false;
        }
        return true;
    }
}
