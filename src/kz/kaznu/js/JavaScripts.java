package kz.kaznu.js;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
        Log.e("toast","toast");
    }

    public void createNotification(String host, String port)
    {
        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("notificate","notificate");
        bundle.putString("title",host);
        bundle.putString("text",port);
        message.setData(bundle);
        handler.handleMessage(message);
    }

}
