package com.bao.wifidemo.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;


/**
 * Fragment基础类
 */

@SuppressWarnings("WeakerAccess")   //弱引用
public abstract class BaseFragment extends Fragment {
    protected Context mContext;
    protected View mRoot;
    protected Bundle mBundle;
    protected LayoutInflater mInflater;
    private Fragment mFragment;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBundle = getArguments();
        initBundle(mBundle);    //拿到传过来的数据参数
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (mRoot != null) {        //复用自己本身的View,如果没有就进行重新加载
            ViewGroup parent = (ViewGroup) mRoot.getParent();
            if (parent != null)
                parent.removeView(mRoot);
        } else {
            mRoot = inflater.inflate(getLayoutId(), container, false);
            mInflater = inflater;
            // Do something
            onBindViewBefore(mRoot);
            // Bind view
            // Get savedInstanceState
            if (savedInstanceState != null)
                onRestartInstance(savedInstanceState);
            // Init  --> 初始化
            initWidget(mRoot);
            initData();
        }
        return mRoot;
    }

    //控制Fragment显示和隐藏
    public void addFragment(int frameLayoutId, Fragment fragment) {  //应该对Fragment进行管理,使用一个实例Fragment

        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            //已经有过该fragment(没有带参数的),
            if (fragment.getArguments() == null && getChildFragmentManager().findFragmentByTag(fragment.getClass().getName()) != null) {
                if (mFragment != null && mFragment.getClass().getName().equals(fragment.getClass().getName())) { //添加的是同一个Fragment(不同对象) 不改变
                    return; // hide和add 为同一个Fragment不同对象
                }
                if (mFragment != null) {
                    Fragment fragmentByTag = getChildFragmentManager().findFragmentByTag(fragment.getClass().getName());
                    fragment = fragmentByTag;   //必须进行赋值,不然以后隐藏 mFragment 会进行出错
                    transaction.hide(mFragment).show(fragmentByTag);
                } else {
                    transaction.add(frameLayoutId, fragment, fragment.getClass().getName());
                }
            } else {    //有参数的fragment不管有没有添加过相同的显示新的fragment
                if (mFragment != null) {
                    if (mFragment.getClass().getName().equals(fragment.getClass().getName())) { //添加的为同一个fragment
                        transaction.remove(mFragment);
                        transaction.add(frameLayoutId,fragment,fragment.getClass().getName());
                    } else {
                        transaction.hide(mFragment).add(frameLayoutId, fragment,fragment.getClass().getName());
                    }
                } else {
                    transaction.add(frameLayoutId, fragment, fragment.getClass().getName());
                }
            }
            mFragment = fragment;
            transaction.commitAllowingStateLoss();
        }
    }

    public void replaceFragment(int frameLayoutId, Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
            if (fragment.isAdded()){
                transaction.remove(fragment).add(frameLayoutId, fragment,fragment.getClass().getName());
            }else {
                transaction.replace(frameLayoutId, fragment, fragment.getClass().getName());
            }
            transaction.commit();
            mFragment = fragment;
        }
    }
    protected void onBindViewBefore(View root) {
        // 在View生成前就进行一系列设置
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mBundle = null;
    }

    protected abstract int getLayoutId(); //强制子类实现 给予布局

    protected void initBundle(Bundle bundle) {

    }

    protected void initWidget(View root) {

    }

    protected void initData() {

    }

    protected <T extends View> T findView(int viewId) {
        return (T) mRoot.findViewById(viewId);
    }

    protected <T extends Serializable> T getBundleSerializable(String key) {
        if (mBundle == null) {
            return null;
        }
        return (T) mBundle.getSerializable(key);
    }






    protected void setText(int viewId, String text) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            return;
        }
        textView.setText(text);
    }

    protected void setText(int viewId, String text, String emptyTip) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            textView.setText(emptyTip);
            return;
        }
        textView.setText(text);
    }

    protected void setTextEmptyGone(int viewId, String text) {
        TextView textView = findView(viewId);
        if (TextUtils.isEmpty(text)) {
            textView.setVisibility(View.GONE);
            return;
        }
        textView.setText(text);
    }

    protected <T extends View> T setGone(int id) {
        T view = findView(id);
        view.setVisibility(View.GONE);
        return view;
    }

    protected <T extends View> T setVisibility(int id) {
        T view = findView(id);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    protected void setInVisibility(int id) {
        findView(id).setVisibility(View.INVISIBLE);
    }

    protected void onRestartInstance(Bundle bundle) {

    }
}
