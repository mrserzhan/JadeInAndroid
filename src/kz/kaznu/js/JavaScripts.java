package kz.kaznu.js;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import jade.android.AndroidHelper;
import kz.kaznu.MyActivity;
import kz.kaznu.R;

/**
 * Created with IntelliJ IDEA.
 * User: Serzhan
 * Date: 07.09.12
 * Time: 21:26
 * To change this template use File | Settings | File Templates.
 */
public class JavaScripts {

    private Context mContext;
    private Handler handler;

    public JavaScripts(Context context, Handler handler)
    {
        this.mContext = context;
        this.handler = handler;
    }

    public void makeToast(String msg)
    {
        Toast.makeText(mContext,msg,Toast.LENGTH_SHORT).show();
    }

    public void connectServer(String host, String port)
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("connectServer","connectServer");
        bundle.putString("host",host);
        bundle.putString("port",port);
        message.setData(bundle);
        handler.handleMessage(message);
    }

    public void startServer(String port)
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("startServer","startServer");
        bundle.putString("port",port);
        message.setData(bundle);
        handler.handleMessage(message);
    }

    public void stopProgress()
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("stop_loader","stop_loader");
        message.setData(bundle);
        handler.handleMessage(message);
    }

    public String getIPAddress()
    {
        return AndroidHelper.getLocalIPAddress();
    }
    public String getPort()
    {
        return MyActivity.port;
    }

    public String getServerName()
    {
        return MyActivity.serverName;
    }
}
