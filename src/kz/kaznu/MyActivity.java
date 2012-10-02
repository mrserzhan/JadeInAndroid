package kz.kaznu;

import android.app.*;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.StaticLayout;
import android.util.Log;
import android.view.Menu;
import android.webkit.WebView;
import jade.android.*;
import jade.core.*;
import jade.core.Runtime;
import jade.util.ClassFinder;
import jade.util.ClassFinderFilter;
import jade.util.leap.Properties;
import jade.wrapper.*;
import jade.wrapper.AgentContainer;
import kz.kaznu.js.JavaScripts;

public class MyActivity extends Activity {

    WebView UI;
    ServiceConnection serviceConnection = null;
    String jadeHost = "";
    String jadePort = "";
    static final String PAGE_INDEX = "file:///android_asset/jade_interface/index.html";
    static final String PAGE_PLACE = "file:///android_asset/jade_interface/place.html";
    ProgressDialog dialog = null;
    public static String port;
    public static String serverName = "Serva4ok";

    JadeService jadeService = null;
    JadeService.JadeServiceBinder jadeServiceBinder = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        UI = new WebView(this);
        UI.getSettings().setJavaScriptEnabled(true);
        UI.addJavascriptInterface(new JavaScripts(getApplicationContext(), handler), "Android");
        UI.loadUrl(PAGE_INDEX);
        setContentView(UI);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        MicroRuntime.stopJADE();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        UI.loadUrl("javascript:showMenu();");
        return true;
    }

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Bundle data = message.getData();

            if (data.containsKey("notificate")) {
                String title = data.getString("title");
                String text = data.getString("text");
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                Notification notification = new Notification(R.drawable.calculator, "Notificate", System.currentTimeMillis());
                notification.flags |= Notification.FLAG_AUTO_CANCEL;
                notification.defaults |= Notification.DEFAULT_SOUND;

                Intent intent = new Intent(MyActivity.this, MyActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(MyActivity.this, 0, intent, 0);
                notification.setLatestEventInfo(MyActivity.this, title, text, pendingIntent);
                notification.number += 1;
                notificationManager.notify(0, notification);

            } else if (data.containsKey("startServer")) {
                startServer(data.getString("port"));
            } else if (data.containsKey("connectServer")) {
                String host = data.getString("host");
                jadeHost = host;
                String port = data.getString("port");
                jadePort = port;
                connectServer("Serzhan", host, port);
            } else if (data.containsKey("message")) {
                String text = message.getData().getString("message");
                Log.e("Message", text);
                createNotification(text);
                addChatMessage(text);
            } else if (data.containsKey("stop_loader")) {
                try {
                    dialog.dismiss();
                } catch (Exception e) {

                }
            }
        }
    };

    public void startServer(final String port) {

        dialog = new ProgressDialog(this);
        dialog.setTitle("Starting...");
        dialog.setMessage("Starting Jade server");

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

                MicroRuntime.stopJADE();
                UI.loadUrl(PAGE_INDEX);
                dialogInterface.dismiss();

            }
        });

        dialog.show();

        new Runnable() {
            public void run() {

                final Properties profile = new Properties();

                profile.setProperty(Profile.PLATFORM_ID, serverName);
                profile.setProperty(Profile.MAIN_HOST, AndroidHelper.getLocalIPAddress());
                profile.setProperty(Profile.MAIN_PORT, port);

                MyActivity.port = port;

                MicroRuntime.startJADE(profile, new Runnable() {
                    @Override
                    public void run() {
                        Log.d("Jade", "Jade is stopped");
                    }
                });

                Runtime rt = Runtime.instance();

                Profile p = new ProfileImpl(profile);

                AgentContainer cc = rt.createMainContainer(p);

                Object[] objects = new Object[]{getApplicationContext(), handler};

                AgentController myAgent = null;
                try {
                    myAgent = cc.createNewAgent("an agent name", "kz.kaznu.HelloAgent", objects);
                } catch (StaleProxyException e) {

                }

                try {
                    myAgent.start();
                } catch (StaleProxyException e) {

                }

            }
        }.run();

        UI.loadUrl(PAGE_PLACE);

    }

    public void startJadeService()
    {
        if(jadeServiceBinder == null) {
            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    jadeServiceBinder = (JadeService.JadeServiceBinder)iBinder;
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    jadeServiceBinder = null;
                }
            };
            bindService(new Intent(getApplicationContext(),
                    JadeService.class), serviceConnection,
                    Context.BIND_AUTO_CREATE);
        }
    }

    public void startJade(String platformID)
    {
        startJadeService();
        Properties properties = new Properties();
        properties.setProperty(Profile.PLATFORM_ID, platformID);
        properties.setProperty(Profile.MAIN_HOST, AndroidHelper.getLocalIPAddress());
        properties.setProperty(Profile.MAIN_PORT, port);
        jadeServiceBinder.startJade(properties, new RuntimeCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

            }

            @Override
            public void onFailure(Throwable throwable) {

            }
        });
    }

    public void connectServer(String name, String host, String port) {

    }

    public void createNotification(String message) {
        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);
        Notification notification = new Notification(R.drawable.calculator,
                "A new notification", System.currentTimeMillis());
        // Hide the notification after its selected
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notification.flags |= Notification.DEFAULT_SOUND;

        Intent intent = new Intent(this, MyActivity.class);
        PendingIntent activity = PendingIntent.getActivity(this, 0, intent, 0);
        notification.setLatestEventInfo(this, "Jade for Android",
                message, activity);
        notification.number += 1;
        notificationManager.notify(0, notification);

    }

    public void addChatMessage(String msg) {
        UI.loadUrl("javascript:addMessage('" + msg + "');");
    }

}
