package kz.kaznu;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import jade.android.RuntimeCallback;
import jade.core.*;
import jade.core.Runtime;
import jade.util.leap.Properties;
import jade.wrapper.*;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Created with IntelliJ IDEA.
 * User: Serzhan
 * Date: 12.09.12
 * Time: 23:32
 * To change this template use File | Settings | File Templates.
 */
public class JadeService extends Service {

    private final IBinder mBinder = new JadeServiceBinder();

    private ContainerController containerController = null;
    private String containerName = null;
    private HashSet<String> agents = new HashSet<String>();

    public void onCreate() {

    }

    public void onDestroy() {

    }

    public void startJade(Properties properties, final RuntimeCallback<Void> callback) {
        MicroRuntime.startJADE(properties,new Runnable() {
            @Override
            public void run() {
                callback.onFailure(new Exception("123"));
            }
        });
        callback.onSuccess(null);
    }

    public void stopJade() {
        MicroRuntime.stopJADE();
    }

    public void startAgentContainer(Properties properties, RuntimeCallback<Void> callback){
        Runtime rt = Runtime.instance();
        Profile p = new ProfileImpl(properties);
        try {
            containerController = rt.createAgentContainer(p);
            containerName = containerController.getContainerName();
            callback.onSuccess(null);
        } catch (ControllerException e) {
            containerController = null;
            containerName = null;
            callback.onFailure(e);
        }
    }

    public void stopAgentContainer(RuntimeCallback<Void> callback) {
        try {
            containerController.kill();
            agents = new HashSet<String>();
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    public void startAgent(String nickname, String classname, Object[] args, RuntimeCallback<Void> callback) {
        AgentController myAgent = null;
        if(containerController == null)
        {
            callback.onFailure(new Throwable("Container not found"));
            return;
        }
        try {
            myAgent = containerController.createNewAgent(nickname, classname, args);
        } catch (StaleProxyException e) {
            callback.onFailure(e);
            return;
        }

        try {
            myAgent.start();
        } catch (StaleProxyException e) {
            callback.onFailure(e);
        }
        try {
            agents.add(myAgent.getName());
            callback.onSuccess(null);
        } catch (StaleProxyException e) {
            callback.onFailure(e);
        }
    }

    public void killAgent(String nickname, RuntimeCallback<Void> callback) {
        try {
            AgentController agentController = containerController.getAgent(nickname);
            agentController.kill();
            agents.remove(nickname);
            callback.onSuccess(null);
        } catch (Exception e) {
            callback.onFailure(e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    public class JadeServiceBinder extends Binder {

        JadeService getService() {
            return JadeService.this;
        }

        public void startJade(Properties properties, final RuntimeCallback<Void> callback) {
            JadeService.this.startJade(properties, callback);
        }

        public void stopJade() {
            JadeService.this.stopJade();
        }

        public void startAgent(String nickname, String classname, Object[] args, RuntimeCallback<Void> callback) {
            JadeService.this.startAgent(nickname, classname, args, callback);
        }

        public void killAgent(String nickname, RuntimeCallback<Void> callback){
            JadeService.this.killAgent(nickname, callback);
        }

        public void startAgentContainer(Properties properties, RuntimeCallback<Void> callback) {
            JadeService.this.startAgentContainer(properties, callback);
        }

        public void stopAgentContainer(RuntimeCallback<Void> callback) {
            JadeService.this.stopAgentContainer(callback);
        }

    }

}
