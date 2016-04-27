package com.brioal.swipetorefreshtest.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.brioal.swipetorefreshtest.R;
import com.brioal.swipetorefreshtest.model.TestModel;

import java.util.List;

/**
 * Created by brioal on 16-4-25.
 */
public class OneAdapter extends RecyclerView.Adapter {
    private List<TestModel> mDatas;
    private int VIEWTYPE_LOAD = 1; //加载的布局
    private int VIEWTYPE_CONTENT = 2; // 内容布局
    private Context mContext;
    private onLoadMoreListener onLoadMoreListener; //加载选项
    private int showItems = 10; //每次显示的item数量
    private int itemsCount = showItems; //应该的data总数,用于判断是否已经到了最大值
    private int minShowLoad = 7; //当数据量多于多少各的时候显示底部提示信息

    public interface onLoadMoreListener {
        void loadMore();
    }

    //设置每次加载新增多少数据
    public void setShowItems(int showItems) {
        this.showItems = showItems;
        itemsCount = showItems;
    }

    public void setMinShowLoad(int minShowLoad) {
        this.minShowLoad = minShowLoad;
    }

    public void setOnLoadMoreListener(OneAdapter.onLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public OneAdapter(Context mContext) {
        this.mContext = mContext;
    }

    //设置数据源
    public void setmDatas(List<TestModel> mDatas) {
        this.mDatas = mDatas;
    }

    public void cleadnCount() {
        itemsCount = 0;
    }

    //添加数据
    public void addDatas(List<TestModel> models) {
        for (int i = 0; i < models.size(); i++) {
            TestModel model = models.get(i);
            if (!mDatas.contains(model)) {
                mDatas.add(model); //添加新数据
            }
        }
        itemsCount += showItems; //如果未加载完,那么itemCount应该等于mData.size

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = null;
        switch (viewType) {
            case 1:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.load_layout, parent, false);
                return new LoadMoreViewHolder(itemView);
            case 2:
                itemView = LayoutInflater.from(mContext).inflate(R.layout.item_test, parent, false);
                return new ContentViewHolder(itemView);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ContentViewHolder) {
            TestModel model = mDatas.get(position);
            ((ContentViewHolder) holder).mTitle.setText(model.getmTitle());
            ((ContentViewHolder) holder).mDesc.setText(model.getmDesc());
            ((ContentViewHolder) holder).mTime.setText(model.getmTime());
        } else if (holder instanceof LoadMoreViewHolder) {
            if (mDatas.size() < itemsCount) { //没有更多
                ((LoadMoreViewHolder) holder).mMsg.setText("没有更多了~~~");
                ((LoadMoreViewHolder) holder).mProgress.setVisibility(View.GONE);
                ((LoadMoreViewHolder) holder).mImage.setVisibility(View.VISIBLE);
            } else {
                onLoadMoreListener.loadMore();
                ((LoadMoreViewHolder) holder).mMsg.setText("正在加载更多....");
                ((LoadMoreViewHolder) holder).mProgress.setVisibility(View.VISIBLE);
                ((LoadMoreViewHolder) holder).mImage.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mDatas.size() > minShowLoad) { //当前item能将屏幕显示满

            return mDatas.size() + 1;  //则默认显示加载或者没有更多
        }
        return mDatas.size();  //如果不能显示满,则不显示加载和没有更多
    }

    @Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return VIEWTYPE_LOAD;
        }
        return VIEWTYPE_CONTENT;
    }

    //内容布局
    private class ContentViewHolder extends RecyclerView.ViewHolder {
        private TextView mTitle;
        private TextView mDesc;
        private TextView mTime;
        private ImageView mHead;
        private View itemView;

        public ContentViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            mTitle = (TextView) itemView.findViewById(R.id.item_title);
            mDesc = (TextView) itemView.findViewById(R.id.item_desc);
            mTime = (TextView) itemView.findViewById(R.id.item_time);
            mHead = (ImageView) itemView.findViewById(R.id.item_head);
        }
    }

    //加载更多的布局   (用于显示正在加载和没有更多
    private class LoadMoreViewHolder extends RecyclerView.ViewHolder {
        private ImageView mImage;
        private ProgressBar mProgress;
        private TextView mMsg;

        public LoadMoreViewHolder(View itemView) {
            super(itemView);
            mImage = (ImageView) itemView.findViewById(R.id.load_image);
            mProgress = (ProgressBar) itemView.findViewById(R.id.load_progress);
            mMsg = (TextView) itemView.findViewById(R.id.load_tv);
        }
    }

}


