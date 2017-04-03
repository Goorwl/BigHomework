package com.example.ll.player.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Video.Media;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.ll.player.R;
import com.example.ll.player.adapter.VideoAdapter;
import com.example.ll.player.bean.VideoBean;
import com.example.ll.player.ui.activity.VideoPlayerActivity;
import com.example.ll.player.utils.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Description :  视频内容列表
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 20:01
 */

public class VideoFragment extends BaseFragment {

    private static final String TAG = "VideoFragment";
    private RecyclerView mRecyclerView;
    private List<VideoBean> mList;

    @Override
    public int getLayout() {
        return R.layout.fragment_video;
    }

    @Override
    public void initData() {
        // 利用内容提供者获取视频数据
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DATA, Media.DURATION, Media.TITLE, Media.SIZE}, null, null, null);
        mList = new ArrayList<>();

        while (cursor.moveToNext()) {
            VideoBean videoBean = VideoBean.fromCursor(cursor);
            LogUtils.e(TAG,videoBean.toString());
            mList.add(videoBean);
        }
    }

    @Override
    public void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        mRecyclerView.setHasFixedSize(true);        //当条目高度一致时，不会重新绘制，提高性能
        VideoAdapter adapter = new VideoAdapter(this.getContext(), mList);
        adapter.setOnItemClick(new VideoAdapter.onRecycleItemClickListener() {
            @Override
            public void onItemClick(View view, VideoBean bean) {
                Intent intent = new Intent(getContext(), VideoPlayerActivity.class);
                intent.putExtra("bean",bean);
                startActivity(intent);
            }
        });
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public void initListener() {

    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }
}
