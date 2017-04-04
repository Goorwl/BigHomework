package com.example.ll.player.ui.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore.Audio.Media;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.ll.player.R;
import com.example.ll.player.adapter.AudioAdapter;
import com.example.ll.player.bean.AudioBean;
import com.example.ll.player.ui.activity.AudioActivity;

import java.util.ArrayList;

/**
 * Description :  音频内容
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 19:58
 */

public class AudioFragment extends BaseFragment {
    private static final String TAG = "AudioFragment";

    private ArrayList<AudioBean> mList;
    private RecyclerView mAudioRv;

    @Override
    public int getLayout() {
        return R.layout.fragment_audio;
    }

    @Override
    public void initData() {
        mList = new ArrayList<>();
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor = resolver.query(Media.EXTERNAL_CONTENT_URI, new String[]{Media.DURATION,Media.DATA,Media.SIZE,Media.DISPLAY_NAME}, null, null, null);
        while (cursor.moveToNext()){
            mList.add(AudioBean.fromCursor(cursor));
            Log.e(TAG, "initData:  " + AudioBean.fromCursor(cursor).toString());
        }
    }

    @Override
    public void initView() {
        mAudioRv = (RecyclerView) findViewById(R.id.audio_rv);
        mAudioRv.setLayoutManager(new LinearLayoutManager(getContext()));
        mAudioRv.setHasFixedSize(true);
        AudioAdapter adapter = new AudioAdapter(getContext(), mList);
        adapter.setOnItemClick(new OnRecycleItemClickListener());
        mAudioRv.setAdapter(adapter);
    }

    @Override
    public void initListener() {
    }

    @Override
    public boolean getHideTitleBar() {
        return false;
    }

    // REcycleview设置点击事件
    private class OnRecycleItemClickListener implements AudioAdapter.onRecycleItemClickListener {
        @Override
        public void onItemClick(View view, AudioBean bean) {
            Intent intent = new Intent(getContext(), AudioActivity.class);
            intent.putExtra("position",mList.indexOf(bean));
            intent.putExtra("list",mList);
            startActivity(intent);
        }
    }
}
