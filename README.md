#Android 自定义下拉刷新上拉加载
###实现的方式是SwipeRefreshLayout + RecyclerView 的VIewType
###首先看效果:
![这里写图片描述](http://img.blog.csdn.net/20160427194702362)
###总的思路:
![这里写图片描述](http://img.blog.csdn.net/20160427195031778)

####布局文件
```
<android.support.v4.widget.SwipeRefreshLayout
        android:layout_marginTop="?attr/actionBarSize"
        android:id="@+id/one_refresh"
        android:layout_width="match_parent"
        android:layout_height="match_parent">

        <android.support.v7.widget.RecyclerView
            android:id="@+id/one_recyclerView"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
           >

        </android.support.v7.widget.RecyclerView>

    </android.support.v4.widget.SwipeRefreshLayout>
```

###下拉刷新的实现思路
![这里写图片描述](http://img.blog.csdn.net/20160427195943945)
###用于测试的Model
```
public class TestModel {
    private String mTitle;
    private String mDesc;
    private String mTime;

    public TestModel(String mTitle, String mDesc, String mTime) {
        this.mTitle = mTitle;
        this.mDesc = mDesc;
        this.mTime = mTime;
    }
    //...一堆getXxx ,setXxx方法
    //equals必写,添加数据时候用于判断
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
```
###模拟获取网络数据的代码
```
private class GetData {
		int size = 0 ;
        int max = 25; //数据的最大值

        public void setStart(int size) {
            this.size = size;
        }
		//根据size获取指定大小的List,最大不能超过max
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
```
####数据获取并通知初始化RecyclerView
```
public void initData() {
        if (getData == null) {
            getData = new GetData();
        }
        mLists = getData.initData(size); //获取默认显示的数量的item
        mhandler.sendEmptyMessage(REFRESH); //通知handler更新
    }
```
####Handler中用于处理第一次显示数据和以后刷新操作的代码
```
if (msg.what == REFRESH) {
if (mAdapter == null) {
mAdapter = new OneAdapter(mContext);
mAdapter.setmDatas(mLists);//设置数据
//...对适配器的设置,这里先省去,免得混淆
mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
mRecyclerView.setAdapter(mAdapter);
       } else {
          mAdapter.setmDatas(mLists);
          mAdapter.cleadnCount();
          mAdapter.notifyDataSetChanged();
      }
    initRefresh(); //判断refreshLayout是否在刷新,是的话取消刷新操作 .就不贴代码了显的乱糟糟
```
####RefreshLayout的刷新事件
```
mRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
     @Override
     public void onRefresh() {
         new Thread(mRunnable).start();//runnable调用了initData()方法;
      }
     });
```
###此时就可以对刷新操作做出响应了,与平时使用RefreshLayout的操作一样
###上拉刷新的实现思路(主要在适配器中,activity中只需要一个当需要加载更多的时候更新数据源就行)
![这里写图片描述](http://img.blog.csdn.net/20160427225216487)
####普通内容的布局
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.v7.widget.CardView xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp"
    android:orientation="vertical"
    tools:context=".MainActivity">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:orientation="horizontal">

        <ImageView
            android:scaleType="centerInside"
            android:id="@+id/item_head"
            android:layout_width="70dp"
            android:layout_height="70dp"
            android:layout_gravity="center"
            android:layout_margin="5dp"
            android:src="@mipmap/ic_launcher" />

        <LinearLayout
            android:layout_width="0dp"
            android:layout_height="match_parent"
            android:layout_marginLeft="5dp"
            android:layout_weight="1"
            android:orientation="vertical">

            <TextView
                android:id="@+id/item_title"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical"
                android:layout_weight="1"
                android:gravity="center_vertical"
                android:text="Title"
                android:textSize="20sp" />

            <TextView
                android:id="@+id/item_desc"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical"
                android:layout_weight="1"
                android:gravity="center_vertical"
                android:text="Desc"
                android:textSize="16sp" />

            <TextView
                android:id="@+id/item_time"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:layout_gravity="center_vertical|right"
                android:layout_marginRight="14dp"
                android:layout_weight="1"
                android:gravity="center_vertical|right"
                android:text="Time"
                android:textSize="20sp" />
        </LinearLayout>

    </LinearLayout>

</android.support.v7.widget.CardView>
```
####加载更多的内容布局(默认显示ProgressBar,没有更多的图标隐藏)
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="40dp"
    android:orientation="horizontal">

    <ImageView
        android:id="@+id/load_image"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical|right"
        android:layout_weight="1"
        android:scaleType="centerInside"
        android:src="@mipmap/ic_launcher"
        android:visibility="gone" />

    <ProgressBar
        android:id="@+id/load_progress"
        style="@style/Widget.AppCompat.ProgressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center_vertical|right"
        android:layout_weight="1" />

    <TextView
        android:id="@+id/load_tv"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:layout_gravity="center"
        android:layout_weight="1"
        android:gravity="center_vertical|left"
        android:text="正在加载更多...."
        android:textColor="@color/colorBlank"
        android:textSize="20sp" />

</LinearLayout>
```
####itemCount(因为我们要在最后显示信息,所以item的总数应该是加1,但是也是分情况的:
```
@Override
    public int getItemCount() {
        if (mDatas.size() > minShowLoad) { //当前item能将屏幕显示满

            return mDatas.size() + 1;  //则默认显示加载或者没有更多
        }
        return mDatas.size();  //如果不能显示满,则不显示加载和没有更多
    }
```
####getViewType(根据不同的位置显示不同的`type`)
```
@Override
    public int getItemViewType(int position) {
        if (position == mDatas.size()) {
            return VIEWTYPE_LOAD; //最后一个显示加载信息
        }
        return VIEWTYPE_CONTENT;//否则显示正常布局
    }
```
####正常内容的ViewHolder
```
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
```
####加载信息的`ViewHolder`
```
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
```
####onCreateViewHolder中初始化不同的ViewHolder
```

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
```
####定义一个回调,用于当显示加载的时候通知activity更新数据
```
public interface onLoadMoreListener {
        void loadMore();
    }
    //全局变量
private onLoadMoreListener onLoadMoreListener;
```
####onBindViewHolder(对不同的情况进行数据显示)
```
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
```
#####加载更多的回调在Activity中的使用
```
mAdapter.setOnLoadMoreListener(new OneAdapter.onLoadMoreListener() {
       @Override
        public void loadMore() {
      //增加数据到数据源中
      //调用adapter的addData方法
      //更新适配器显示
	}
}

```
####至此下拉刷新上拉加载就完成了
