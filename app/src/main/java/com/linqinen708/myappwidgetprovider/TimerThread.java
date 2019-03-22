package com.linqinen708.myappwidgetprovider;

import android.content.Context;
import android.content.Intent;

import com.linqinen708.myappwidgetprovider.broadcastreceiver.MyAppWidgetProvider;
import com.linqinen708.myappwidgetprovider.utils.LogT;

/**
 * Created by Ian on 2019/3/22.
 * <p>
 * 参考资料：https://www.cnblogs.com/bokeofzp/p/5968772.html
 */
public class TimerThread extends Thread {

    public static String ACTION_TIMER = "com.action.widget.timer";
//    public static final String ACTION_TIMER = "android.appwidget.action.APPWIDGET_UPDATE";

    public static boolean isContinue = false;

    private Context mContext;

    public static int index = 0;

    /**
     * android8.0之后只能发送显式广播了，
     * 如果还想发送隐式广播，可以用系统自带的一些广播
     * android.appwidget.action.APPWIDGET_UPDATE
     * 是AppWidgetProvider类原本自带的广播，用来更新控件
     * 如果小伙伴们有需要可以使用
     */
    public TimerThread(Context context) {
        mContext = context;
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            LogT.i("8.0系统，使用系统自带的广播");
//            ACTION_TIMER = "android.appwidget.action.APPWIDGET_UPDATE";
//        }
    }

    @Override
    public synchronized void start() {
        super.start();
        LogT.i("启动线程");
    }

    @Override
    public void interrupt() {
        super.interrupt();
        LogT.i("打断线程");
    }


    @Override
    public void run() {
//        super.run();
        while (isContinue) {
            Intent intent = new Intent(ACTION_TIMER);
            try {
                /*原本是每秒改变一次，但是实际使用中，细心的小伙伴会发现是大于1s的
                 * 因为程序在运行代码的时候也是消耗时间的，小伙伴们可以根据自己的需求进行优化
                 * */
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (mContext == null) {
                isContinue = false;
                return;
            }
            intent.setClass(mContext, MyAppWidgetProvider.class);
            mContext.sendBroadcast(intent);
            index++;
//            LogT.i("11index:" + index);
            /*以下代码小伙伴自己根据需求改动，我是设置了180秒后停止
             * 不设置的话理论上来说无限循环，真的是理论上，没有亲测多久停止
             * */
            if (index > 180) {
                isContinue = false;
                index = 0;
            }
        }
    }
}
