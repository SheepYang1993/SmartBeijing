package com.sheepyang.smartbeijing;

import android.app.Application;
import org.xutils.x;

/**
 * Created by SheepYang on 2016/6/24 20:27.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
//        x.Ext.setDebug(BuildConfig.DEBUG); // 是否输出debug日志, 开启debug会影响性能.
    }
}
