package kz.kaznu;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;
import jade.core.Agent;
import jade.core.behaviours.ReceiverBehaviour;
import jade.core.event.MessageEvent;
import jade.core.event.MessageListener;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
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

    protected void setup()
    {

        Log.e("Agent","agent started");
        Object[] args = getArguments();
        if (args != null && args.length > 0) {
            if (args[0] instanceof MyActivity) {
                context = (MyActivity) args[0];
            }
        }

        DFAgentDescription dfAgentDescription = new DFAgentDescription();
        ServiceDescription serviceDescription = new ServiceDescription();
        serviceDescription.setName(getLocalName());
        serviceDescription.setType("hello");
        dfAgentDescription.addServices(serviceDescription);
        dfAgentDescription.setName(getAID());

        try {
            DFService.register(this, dfAgentDescription);
        } catch (FIPAException e) {
            Log.e("FIPAException",e.getMessage());
            doDelete();
        }

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("status","Ready for receiving");
        msg.setData(bundle);
        context.handler.handleMessage(msg);

    }



    protected void takeDown()
    {
        try {
            DFService.deregister(this);
        } catch (FIPAException e) {
            Log.e("FIPAException",e.getMessage());
        }
        if(context != null)
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
        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("message","Ready for receiving");
        msg.setData(bundle);
        context.handler.handleMessage(msg);
    }

    @Override
    public void routedMessage(MessageEvent messageEvent) {

    }
}
