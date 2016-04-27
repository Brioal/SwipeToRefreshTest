package com.brioal.swipetorefreshtest;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.brioal.swipetorefreshtest.activity.ActivityOne;
import com.brioal.swipetorefreshtest.activity.BaseActivity;
import com.brioal.swipetorefreshtest.util.BrioalUtil;

public class MainActivity extends BaseActivity {

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BrioalUtil.init(this);
        mContext = this;
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

    }

    //swipe + recyclerView type
    public void start1(View view) {
        startActivity(new Intent(mContext, ActivityOne.class));
    }

}
