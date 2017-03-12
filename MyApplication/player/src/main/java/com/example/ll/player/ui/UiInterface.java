package com.example.ll.player.ui;

/**
 * Description :  项目里UI接口
 * Copyright   : (c) 2016
 * Author      : Goorwl
 * Email       : goorwl@163.com
 * Date        : 2017/3/12 15:32
 */

public interface UiInterface {

    /*
        加载布局
     */
    public int getLayout();

    /*
        初始化数据
     */
    public void initData();

    /*
        添加控件
     */
    public void initView();

    /*
        初始化监听事件
     */
    public void initListener();
    /*
        是否隐藏标题栏
     */
    public boolean getHideTitleBar();
}
