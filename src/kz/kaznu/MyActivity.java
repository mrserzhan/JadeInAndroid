package kz.kaznu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
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

import java.util.logging.Level;

public class MyActivity extends Activity {

    MicroRuntimeServiceBinder microRuntimeServiceBinder = null;
    ServiceConnection serviceConnection = null;
    public MyHandler myHandler;
    TextView textView = null;
    ListView listView = null;
    LinearLayout mainBox = null;
    String text = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mainBox = (LinearLayout)findViewById(R.id.mainBox);
        textView = (TextView)findViewById(R.id.statusBox);
        listView = new ListView(this);
        text = "Application started";

        myHandler = new MyHandler();
        startChat("Serzhan", "192.168.1.5","1099", agentStartupCallback);

    }

    @Override
    public void onResume()
    {
        super.onResume();
        textView.setText(text);
    }

    private RuntimeCallback<AgentController> agentStartupCallback = new RuntimeCallback<AgentController>() {
        @Override
        public void onSuccess(AgentController agent) {
        }

        @Override
        public void onFailure(Throwable throwable) {
            Log.d("Nickname", "Nickname already in use!");
            myHandler.postError("Serzhan");
            finish();
        }
    };

    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle.containsKey("error")) {
                String message = bundle.getString("error");
                ShowDialog(message);
            }
            else if(bundle.containsKey("message"))
            {
                TextView tv = new TextView(MyActivity.this);
                tv.setText(bundle.getString("message"));
                listView.addView(tv);
            }
            else if(bundle.containsKey("status"))
            {
                TextView tv = new TextView(MyActivity.this);
                textView.setText(bundle.getString("status"));
            }
            return true;
        }
    });

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            if (bundle.containsKey("error")) {
                String message = bundle.getString("error");
                ShowDialog(message);
            }
        }

        public void postError(String error) {
            Message msg = obtainMessage();
            Bundle b = new Bundle();
            b.putString("error", error);
            msg.setData(b);
            sendMessage(msg);
        }
    }

    public void ShowDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        builder.setMessage(message).setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startChat(final String nickname, final String host, final String port, final RuntimeCallback<AgentController> agentControllerRuntimeCallback)
    {
        final Properties profile = new Properties();
        profile.setProperty(Profile.MAIN_HOST, host);
        profile.setProperty(Profile.MAIN_PORT, port);
        profile.setProperty(Profile.MAIN, Boolean.FALSE.toString());
        profile.setProperty(Profile.JVM, Profile.ANDROID);

        if(AndroidHelper.isEmulator())
            profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.LOOPBACK);
        else
            profile.setProperty(Profile.LOCAL_HOST, AndroidHelper.getLocalIPAddress());

        profile.setProperty(Profile.LOCAL_PORT, "2000");

        if(microRuntimeServiceBinder == null)
        {

            serviceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    microRuntimeServiceBinder = (MicroRuntimeServiceBinder)iBinder;
                    Log.d("MicroRuntimeService", "Gateway successfully bound to MicroRuntimeService");
                    startContainer(nickname, profile, agentControllerRuntimeCallback);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {
                    microRuntimeServiceBinder = null;
                    Log.d("MicroRuntimeService", "Gateway unbound from MicroRuntimeService");
                }
            };

            Log.d("MicroRuntimeService", "Binding Gateway to MicroRuntimeService...");
            bindService(new Intent(getApplicationContext(),
                    MicroRuntimeService.class), serviceConnection,
                    Context.BIND_AUTO_CREATE);
        }
        else
        {
            Log.d("MicroRuntimeGateway", "MicroRuntimeGateway already binded to service");
            startContainer(nickname, profile, agentControllerRuntimeCallback);
        }

    }

    public void startContainer(final String nickname, final Properties profile, final RuntimeCallback<AgentController> agentControllerRuntimeCallback)
    {
        if(!MicroRuntime.isRunning())
        {
            microRuntimeServiceBinder.startAgentContainer(profile, new RuntimeCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Container", "Successfully start of the container...");
                    startAgent(nickname, agentControllerRuntimeCallback);
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d("Container", "Failed to start the container...");
                }
            });
        }
        else
        {
            startAgent(nickname, agentControllerRuntimeCallback);
        }
    }

    private void startAgent(final String nickname, final RuntimeCallback<AgentController> agentControllerRuntimeCallback)
    {
        microRuntimeServiceBinder.startAgent(nickname, HelloAgent.class.getName(),
            new Object[] { this },
            new RuntimeCallback<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.d("Agent", "Successfully start of the "
                            + HelloAgent.class.getName() + "...");
                    try {
                        agentControllerRuntimeCallback.onSuccess(MicroRuntime.getAgent(nickname));
                    } catch (ControllerException e) {
                        // Should never happen
                        agentControllerRuntimeCallback.onFailure(e);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Log.d("Agent", "Failed to start the "
                            + HelloAgent.class.getName() + "...");
                    agentControllerRuntimeCallback.onFailure(throwable);
                }
            }
        );

    }

    RuntimeCallback<Void> runtimeCallback = new RuntimeCallback<Void>() {
        @Override
        public void onSuccess(Void aVoid) {
            finish();
        }

        @Override
        public void onFailure(Throwable throwable) {
            finish();
        }
    };

    public void onPause()
    {
        super.onPause();
        microRuntimeServiceBinder.stopAgentContainer(runtimeCallback);

    }

    public void onDestroy()
    {
        super.onDestroy();
        unbindService(serviceConnection);
    }

}
