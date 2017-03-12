package com.example.ll.player.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ll.player.ui.UiInterface;

/**
 * Description : 醒目里fragment的基类
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 15:33
 */

public abstract class BaseFragment extends Fragment implements UiInterface{

    private View mMView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mMView = inflater.inflate(getLayout(), null);
        initListener();
        initData();
        initView();
        return mMView;
    }

    /*
        提供公共方法
     */
    public View findViewById(int resId){
        return mMView.findViewById(resId);
    }
}
