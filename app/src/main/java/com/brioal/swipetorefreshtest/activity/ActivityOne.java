package com.brioal.swipetorefreshtest.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.brioal.swipetorefreshtest.R;
import com.brioal.swipetorefreshtest.adapter.OneAdapter;
import com.brioal.swipetorefreshtest.model.TestModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ActivityOne extends BaseActivity {

    private static final int REFRESH = 10;
    private static final int LOAD_MORE = 19;
    private static final String TAG = "OneInfo";
    @Bind(R.id.one_recyclerView)
    RecyclerView mRecyclerView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.one_refresh)
    SwipeRefreshLayout mRefresh;

    private Context mContext;
    private GetData getData;
    private OneAdapter mAdapter;
    private List<TestModel> mLists;
    private int itemCountLoad = 10; //每次加载的时候加载多少条数据
    private int minLoadCount = 7; //当数据多于多少个的时候显示底部信息

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            initData();
        }
    };
    private Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == REFRESH) {
                if (mAdapter == null) {
                    mAdapter = new OneAdapter(mContext);
                    mAdapter.setmDatas(mLists);
                    mAdapter.setShowItems(itemCountLoad);
                    mAdapter.setMinShowLoad(minLoadCount);
                    mAdapter.setOnLoadMoreListener(new OneAdapter.onLoadMoreListener() {
                        @Override
                        public void loadMore() {
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    addData();

                                }
                            }, 1500);
                        }
                    });
                    mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
                    mRecyclerView.setAdapter(mAdapter);
                } else {
                    mAdapter.setmDatas(mLists);
                    mAdapter.cleadnCount();
                    mAdapter.notifyDataSetChanged();
                }
                initRefresh();
            } else if (msg.what == LOAD_MORE) {
                mAdapter.notifyDataSetChanged();
            }

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);
        ButterKnife.bind(this);
        mContext = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("下拉刷新上拉加载");
        setSupportActionBar(toolbar);
        new Thread(mRunnable).start();
        mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Thread(mRunnable).start();
            }
        });
    }

    private void initRefresh() {
        if (mRefresh.isRefreshing()) {
            mRefresh.setRefreshing(false);
        }

    }

    public void initData() {
        if (getData == null) {
            getData = new GetData();
        }
        getData.setStart(0);
        mLists = getData.initData(itemCountLoad);
        mhandler.sendEmptyMessage(REFRESH);
    }

    public void addData() {
        mAdapter.addDatas(getData.initData(itemCountLoad));
        mhandler.sendEmptyMessage(LOAD_MORE);
    }


    private class GetData {
        int start = 0;
        int max = 25;

        public void setStart(int start) {
            this.start = start;
        }

        public List<TestModel> initData(int size) {
            List<TestModel> mDatas = new ArrayList<>();
            TestModel model = null;
            for (int i = start; i < ((size + start) > max ? max : (size + start)); i++) {
                model = new TestModel("Title" + i, "Desc" + i, "今天 11:30");
                mDatas.add(model);
            }
            start += size;
            return mDatas;

        }
    }
}
