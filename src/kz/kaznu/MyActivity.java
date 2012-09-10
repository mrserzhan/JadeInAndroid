package kz.kaznu;

import android.app.*;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import jade.android.AndroidHelper;
import jade.android.MicroRuntimeService;
import jade.android.MicroRuntimeServiceBinder;
import jade.android.RuntimeCallback;
import jade.core.MicroRuntime;
import jade.core.Profile;
import jade.util.leap.Properties;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import kz.kaznu.js.JavaScripts;

import java.util.logging.Level;

public class MyActivity extends Activity {

    WebView UI;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        UI = new WebView(this);
        UI.getSettings().setJavaScriptEnabled(true);
        UI.addJavascriptInterface(new JavaScripts(getApplicationContext(),handler),"Android");
        UI.loadUrl("file:///android_asset/jade_interface/index.html");
        setContentView(UI);

    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message message)
        {
            Bundle data = message.getData();
            if(data.containsKey("notificate"))
            {
                String title = data.getString("title");
                String text = data.getString("text");
                NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new Notification(R.drawable.calculator,"Notificate",System.currentTimeMillis());
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;

                Intent intent = new Intent(MyActivity.this, MyActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyActivity.this, 0, intent, 0);
                notification.setLatestEventInfo(MyActivity.this,title,text,pendingIntent);
                notification.number += 1;
                notificationManager.notify(0, notification);

            }
        }
    };

}
