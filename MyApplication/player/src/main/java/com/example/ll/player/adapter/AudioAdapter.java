package com.example.ll.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ll.player.R;
import com.example.ll.player.bean.AudioBean;
import com.example.ll.player.utils.StringUtils;

import java.util.ArrayList;

/**
 * Created by wule on 2017/4/4.
 */

public class AudioAdapter extends RecyclerView.Adapter<AudioViewHolder> implements View.OnClickListener{
    private onRecycleItemClickListener mOnRecycleItemClickListener = null;

    private ArrayList<AudioBean> mList;
    private Context ctx;


    public AudioAdapter(Context ctx, ArrayList<AudioBean> list) {
        mList = list;
        this.ctx = ctx;
    }

    @Override
    public AudioViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(ctx).inflate(R.layout.layout_audio_item, parent, false);
        inflate.setOnClickListener(this);
        return new AudioViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(AudioViewHolder holder, int position) {
        holder.mTvTitle.setText(mList.get(position).getName());
        String s = android.text.format.Formatter.formatFileSize(ctx, mList.get(position).getSize());
        holder.mTvSize.setText(s);
        holder.itemView.setTag(mList.get(position));
        holder.mTvTime.setText(StringUtils.formaterTime(mList.get(position).getTime()));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }


    @Override
    public void onClick(View v) {
        if (mOnRecycleItemClickListener != null){
            mOnRecycleItemClickListener.onItemClick(v, (AudioBean) v.getTag());
        }
    }

    // 定义条目点击事件接口
    public static interface onRecycleItemClickListener{
        void onItemClick(View view,AudioBean bean);
    }

    // 把监听事件暴漏出去
    public void setOnItemClick(onRecycleItemClickListener listener){
        this.mOnRecycleItemClickListener = listener;
    }
}

class AudioViewHolder extends RecyclerView.ViewHolder {

    public TextView mTvTitle;
    public TextView mTvSize;
    public TextView mTvTime;

    public AudioViewHolder(View itemView) {
        super(itemView);
        mTvTitle = (TextView) itemView.findViewById(R.id.tv_audio_title);
        mTvSize = (TextView) itemView.findViewById(R.id.tv_audio_size);
        mTvTime = (TextView) itemView.findViewById(R.id.tv_audio_time);
    }
}
