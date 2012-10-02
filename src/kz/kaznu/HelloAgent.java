package kz.kaznu;

import android.app.NotificationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import jade.core.Agent;
import jade.core.behaviours.ReceiverBehaviour;
import jade.core.event.*;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;

/**
 * Created with IntelliJ IDEA.
 * User: Serzhan
 * Date: 03.09.12
 * Time: 21:12
 * To change this template use File | Settings | File Templates.
 */
public class HelloAgent extends Agent implements MessageListener {

    private MyActivity context = null;
    private Handler handler = null;

    protected void setup() {

        Object[] args = getArguments();
        if (args != null && args.length > 1) {
            if (args[0] instanceof MyActivity) {
                context = (MyActivity) args[0];
            }
            if (args[1] instanceof Handler) {
                handler = (Handler) args[1];
            }
        }

        try {
            DFService.register(this, new DFAgentDescription());
        } catch (FIPAException e) {
            Log.e("FIPAException", e.getMessage());
            doDelete();
        }

        Log.e("Agent", "agent started");

        while(true)
        {
            ACLMessage message = blockingReceive();
            if(message!=null)
            {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("message", message.getSender().getLocalName() + ": " + message.getContent());
                msg.setData(bundle);
                handler.handleMessage(msg);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                break;
            }
        }

    }


    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            Log.e("FIPAException", e.getMessage());
        }
        Log.e("Agent", "agent died");
        if (context != null)
            context.finish();
    }

    @Override
    public void sentMessage(MessageEvent messageEvent) {

    }

    @Override
    public void postedMessage(MessageEvent messageEvent) {

    }

    @Override
    public void receivedMessage(MessageEvent messageEvent) {
        Log.e("Message", messageEvent.getMessage().getContent());
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("message", "Ready for receiving");
        msg.setData(bundle);
        handler.handleMessage(msg);
    }

    @Override
    public void routedMessage(MessageEvent messageEvent) {

    }

}
