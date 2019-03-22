package com.linqinen708.myappwidgetprovider.broadcastreceiver;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.linqinen708.myappwidgetprovider.R;
import com.linqinen708.myappwidgetprovider.TimerThread;
import com.linqinen708.myappwidgetprovider.utils.LogT;

/**
 * Created by Ian on 2019/3/22.
 * <p>
 * 每次接受到广播后就重新创建了MyAppWidgetProvider这个类对象
 * 所以有些变量可以使用static
 *
 * 9.0之后，隐式（静态）广播被禁止，只能动态注册
 *
 * 进阶版（复杂）的可以参考资料：https://www.cnblogs.com/joy99/p/6346829.html
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    private static final String CLICK_NAME_ACTION = "com.action.widget.click";

    private final int colors[] = {Color.BLUE, Color.DKGRAY, Color.GREEN, Color.RED, Color.CYAN,
            Color.WHITE, Color.GRAY, Color.MAGENTA, Color.LTGRAY, Color.YELLOW};

    private static RemoteViews remoteViews;

    private static ComponentName componentName;

    private TimerThread mTimerThread;

    public TimerThread getTimerThread(Context context) {
        if (mTimerThread == null) {
            mTimerThread = new TimerThread(context);
        }
        return mTimerThread;
    }

    public RemoteViews getRemoteViews(Context context) {
        if (remoteViews == null) {
            LogT.i("创建RemoteViews");
            remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_content);
        }
        return remoteViews;
    }

    public ComponentName getComponentName(Context context) {
        if (componentName == null) {
            LogT.i("创建ComponentName");
            componentName = new ComponentName(context, MyAppWidgetProvider.class);
        }
        return componentName;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        LogT.i("刷新AppWidgetProvider");
        setOnClickEvent(appWidgetManager, context);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        LogT.i("启动WidgetProvider");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        LogT.i("销毁WidgetProvider");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        LogT.i("接收广播");
        if (intent != null && CLICK_NAME_ACTION.equals(intent.getAction())) {
            Toast.makeText(context, "触发点击事件", Toast.LENGTH_LONG).show();
            if (TimerThread.isContinue) {
                TimerThread.isContinue = false;
            } else {
                TimerThread.isContinue = true;
                getTimerThread(context).start();
            }
            setOnClickEvent(AppWidgetManager.getInstance(context), context);
        } else if (intent != null && TimerThread.ACTION_TIMER.equals(intent.getAction())) {
            int index = TimerThread.index % colors.length;
//            LogT.i("index:" + index);
            getRemoteViews(context).setTextColor(R.id.tv_content, colors[index]);
            AppWidgetManager appWidgetManger = AppWidgetManager.getInstance(context);
            /*只有触发刷新方法updateAppWidget()才能有效果*/
            appWidgetManger.updateAppWidget(getComponentName(context), getRemoteViews(context));
        }
//        setOnClickEvent(appWidgetManger,context);
    }

    private void setOnClickEvent(AppWidgetManager appWidgeManger, Context context) {
        if (componentName == null || remoteViews == null) {
            LogT.i("刷新点击事件" );
            Intent intentClick = new Intent(CLICK_NAME_ACTION);
            /*如果没有下面这句话，这点击事件无效*/
            intentClick.setComponent(getComponentName(context));

            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intentClick, PendingIntent.FLAG_UPDATE_CURRENT);
            getRemoteViews(context).setOnClickPendingIntent(R.id.tv_content, pendingIntent);
            appWidgeManger.updateAppWidget(getComponentName(context), getRemoteViews(context));
        }
    }
}
