package com.example.ll.player.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.example.ll.player.R;
import com.example.ll.player.adapter.MainAdapter;
import com.example.ll.player.ui.fragment.AudioFragment;
import com.example.ll.player.ui.fragment.VideoFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private ViewPager mViewpage;
    private List<Fragment> mList;

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        mList.add(new VideoFragment());
        mList.add(new AudioFragment());
    }

    @Override
    public void initView() {
        mViewpage = (ViewPager) findViewById(R.id.vp);
    }

    @Override
    public void initListener() {
        mViewpage.setAdapter(new MainAdapter(getSupportFragmentManager(),mList));
    }

    @Override
    public boolean getHideTitleBar() {
        return true;
    }
}
