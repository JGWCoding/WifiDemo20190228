package com.bao.wifidemo.base;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.bao.wifidemo.R;


/**
 * //Fragment里填充了RecyclerView,并用EmptyLayout处理空,错误等页面,还有控制刷新页面
 * 基本列表类，重写getLayoutId()自定义界面
 * Created by huanghaibin_dev
 * on 2016/4/12.
 */
public abstract class BaseRecyclerViewFragment<T> extends BaseFragment implements
        BaseRecyclerRefreshLayout.SuperRefreshLayoutListener,
        BaseRecyclerAdapter.OnItemClickListener {
    private final String TAG = this.getClass().getSimpleName();
    protected BaseRecyclerAdapter<T> mAdapter;
    protected RecyclerView mRecyclerView;
    protected BaseRecyclerRefreshLayout mRefreshLayout;
    protected boolean isRefreshing;
    protected String CACHE_NAME = getClass().getName();
    private View.OnLayoutChangeListener onLayoutChangeListener;

    @Override
    public int getLayoutId() {
        return R.layout.fragment_base_recycler_view;
//        <?xml version="1.0" encoding="utf-8"?>    xxx.xxx为包名
//<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
//        xmlns:tools="http://schemas.android.com/tools"
//        android:id="@+id/layout_list_container"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:orientation="vertical">
//
//    <xxx.xxx.BaseRecyclerRefreshLayout
//        android:id="@+id/refreshLayout"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:visibility="visible"
//        tools:visibility="visible">
//
//        <android.support.v7.widget.RecyclerView
//        android:id="@+id/recyclerView"
//        android:layout_width="match_parent"
//        android:layout_height="match_parent"
//        android:scrollbars="vertical" />
//
//    </xxx.xxx.BaseRecyclerRefreshLayout>
//
//</FrameLayout>
    }

    @Override
    protected void initWidget(View root) {
        mRecyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        ((SimpleItemAnimator) mRecyclerView.getItemAnimator()).setSupportsChangeAnimations(false); //设置recyclerView 不会闪屏
        mRefreshLayout = (BaseRecyclerRefreshLayout) root.findViewById(R.id.refreshLayout);
    }

    @Override
    public void initData() {
        mAdapter = getRecyclerAdapter();    //得到这个Fragment的Adapter   mAdapter=SubFragment 第一次的时候
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);   //设置刷新状态 设置不去刷新RecyclerView
        mRecyclerView.setAdapter(mAdapter);     //准备填充这个内容进去
        mAdapter.setOnItemClickListener(this);  //设置item点击事件
        mRefreshLayout.setSuperRefreshLayoutListener(this);
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, false);
        mRecyclerView.setLayoutManager(getLayoutManager()); //设置线性布局
//        mRecyclerView.addItemDecoration(new SpacesItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));//设置分割线
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() { //滑动监听
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (RecyclerView.SCROLL_STATE_DRAGGING == newState && getActivity() != null
                        && getActivity().getCurrentFocus() != null) {
//                    TDevice.hideSoftKeyboard(getActivity().getCurrentFocus());  //拖拉状态直接隐藏键盘
//                    public static void hideSoftKeyboard(View view) {
//                        if (view == null) return;
//                        View mFocusView = view;
//
//                        Context context = view.getContext();
//                        if (context != null && context instanceof Activity) {
//                            Activity activity = ((Activity) context);
//                            mFocusView = activity.getCurrentFocus();
//                        }
//                        if (mFocusView == null) return;
//                        mFocusView.clearFocus();
                        InputMethodManager manager = (InputMethodManager) getContext()
                                .getSystemService(Context.INPUT_METHOD_SERVICE);
                        manager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
//                    }
                }
            }
        });
        mRefreshLayout.onRefresh(); //加载数据  ---- onRefreshing
//        onRefreshing();
    }

    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        if (layoutManager == null) {
            mRecyclerView.setLayoutManager(getLayoutManager());
        } else {
            mRecyclerView.setLayoutManager(layoutManager);
        }
        mRecyclerView.setAdapter(mAdapter); //必须重新设置Adapter,不然会有有时可以有时设置不了问题
    }

    @Override
    public void onItemClick(int position, long itemId) {

    }

    @Override
    public void onRefreshing() {
        isRefreshing = true;
        mAdapter.setState(BaseRecyclerAdapter.STATE_HIDE, true);    //隐藏正文页面,显示刷新页面, 刷新状态不用更新Recycler条目
        mRefreshLayout.setCanLoadMore(true);
        requestData(true);      //在子类中请求数据 (位于主线程中)---> 在本mHandle处理请求的数据
    }

    @Override
    public void onLoadMore() { // TODO 主要这里有不刷新的item情况
        mAdapter.setState(isRefreshing ? BaseRecyclerAdapter.STATE_HIDE : BaseRecyclerAdapter.STATE_LOADING, true);
        requestData(false);
//        refreshState(OkHttpManager.NetWorkTimeOut); //注意设置超时时长 ---- 可能由于这里加载时间过长 体验差
    }

//    private void refreshState(int delayMillis) {
//        BaseApplication.mHandler.postDelayed(new Runnable() {   //主线程刷新UI
//            @Override
//            public void run() {
//                int state = mAdapter.getState();
//                switch (state) {
//                    case BaseRecyclerAdapter.STATE_LOADING:
//                        mAdapter.setState(BaseRecyclerAdapter.STATE_INVALID_NETWORK, true);
//                        break;
//                }
//            }
//        }, delayMillis * 1000);
//    }

    protected abstract void requestData(boolean isRefreshing);


    protected void onComplete() {
        mRefreshLayout.onComplete();
        isRefreshing = false;
        if (onLayoutChangeListener == null) {
            onLayoutChangeListener = new View.OnLayoutChangeListener() { //需要修复第一次刷新填不满页面,需要提示没数据了
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (mRefreshLayout.getLastVisiblePosition() == mAdapter.getItemCount() - 1) {
                        if (BaseRecyclerAdapter.STATE_HIDE == mAdapter.getState()) {
                            mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true); //设置没有更多数据
                        }
                    }
                }
            };
            mRecyclerView.addOnLayoutChangeListener(onLayoutChangeListener);
        }
    }

    protected void onRequestSuccess() {
        onComplete();
    }

    protected void onNoRequest() {
        onComplete();
        mRefreshLayout.setCanLoadMore(false);
        // 隐藏最后一条
        mAdapter.setState(BaseRecyclerAdapter.STATE_NO_MORE, true); //设置没有更多数据
    }

    protected void onRequestError() {
        onComplete();
        if (mAdapter.getItems().size() == 0) {  //一开始是为0的 --->默认为0,不为0就显示以前的数据
            mAdapter.setState(BaseRecyclerAdapter.STATE_LOAD_ERROR, true);  //
        }
    }

    protected RecyclerView.LayoutManager getLayoutManager() {
        return new LinearLayoutManager(getActivity());
    }

    protected abstract BaseRecyclerAdapter<T> getRecyclerAdapter();

}
