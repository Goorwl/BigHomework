package com.example.ll.player.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ll.player.R;
import com.example.ll.player.bean.VideoBean;
import com.example.ll.player.utils.StringUtils;

import java.util.List;

/**
 * Created by wule on 2017/4/3.
 */

public class VideoAdapter extends RecyclerView.Adapter<VideoViewholder> implements View.OnClickListener {
    private onRecycleItemClickListener mOnRecycleItemClickListener = null;
    List<VideoBean> list;
    Context ctx;
    public VideoAdapter(Context ctx,List<VideoBean> list) {
        this.list = list;
        this.ctx = ctx;
    }

    @Override
    public VideoViewholder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(ctx).inflate(R.layout.layout_video_item, parent, false);
        inflate.setOnClickListener(this);
        return new VideoViewholder(inflate);
    }

    @Override
    public void onBindViewHolder(VideoViewholder holder, int position) {
        holder.mTvTitle.setText(list.get(position).getTitle());
        holder.mTvTime.setText(StringUtils.formaterTime(list.get(position).getTime()));
        String s = android.text.format.Formatter.formatFileSize(ctx, list.get(position).getSize());
        holder.mTvSize.setText(s);
        holder.itemView.setTag(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnRecycleItemClickListener != null){
            mOnRecycleItemClickListener.onItemClick(v, (VideoBean) v.getTag());
    }
    }

    // 定义条目点击事件接口
    public static interface onRecycleItemClickListener{
        void onItemClick(View view,VideoBean bean);
    }

    // 把监听事件暴漏出去
    public void setOnItemClick(onRecycleItemClickListener listener){
        this.mOnRecycleItemClickListener = listener;
    }
}

class VideoViewholder extends RecyclerView.ViewHolder{

    public TextView mTvTitle;
    public TextView mTvTime;
    public TextView mTvSize;

    public VideoViewholder(View itemView) {
        super(itemView);
        mTvTitle = (TextView) itemView.findViewById(R.id.tv_video_title);
        mTvTime = (TextView) itemView.findViewById(R.id.tv_video_time);
        mTvSize = (TextView) itemView.findViewById(R.id.tv_video_size);
    }
}
