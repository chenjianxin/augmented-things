package mecs.iot.proj.om2m.adn;

import java.util.HashMap;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;

import mecs.iot.proj.om2m.Client;
import mecs.iot.proj.om2m.dashboard.DebugStream;
import mecs.iot.proj.om2m.dashboard.ErrStream;
import mecs.iot.proj.om2m.structures.Constants;
import mecs.iot.proj.om2m.structures.Node;
import mecs.iot.proj.om2m.structures.Severity;

import java.net.URISyntaxException;
import java.util.ArrayList;

public class Subscriber {
	
	private HashMap<String,ArrayList<Subscription>> subscriptionMap;										// resource -> list of subscriptions
	private HashMap<String,String> piMap;
	private String lastResource;
	
	private DebugStream debugStream;
	private ErrStream errStream;
	private Client cseClient;
	private String context;
	
	public Subscriber(DebugStream debugStream, ErrStream errStream, Client cseClient, String context) {
		subscriptionMap = new HashMap<String,ArrayList<Subscription>>();
		piMap = new HashMap<String,String>();
		this.debugStream = debugStream;
		this.errStream = errStream;
		this.cseClient = cseClient;
		this.context = context;
	}
	
	public void insert(String sender, String receiver, String address) {
		Subscription ref = new Subscription(sender,receiver,address);
		if (subscriptionMap.containsKey(sender)) {
			subscriptionMap.get(sender).add(ref);
		} else {
			ArrayList<Subscription> subs = new ArrayList<Subscription>();
			subs.add(ref);
			subscriptionMap.put(sender,subs);
		}
		lastResource = sender;
	}
	
	public void insert(String sender, String event, String rule, String receiver, String address, String action) {
		Subscription ref = new Subscription(sender,event,rule,receiver,address,action);
		if (subscriptionMap.containsKey(sender)) {
			subscriptionMap.get(sender).add(ref);
		} else {
			ArrayList<Subscription> subs = new ArrayList<Subscription>();
			subs.add(ref);
			subscriptionMap.put(sender,subs);
		}
		lastResource = sender;
	}
	
	// TODO: push subscriptionMap pair (sensor,subs) into CSE for IN-MN synchronization
	
	public boolean containsResource(String sender) {
		return piMap.containsValue(sender);
	}
	
	public boolean containsKey(String pi) {
		return piMap.containsKey(pi);
	}
	
	public void bindToLastResource(String pi) {
		piMap.put(pi,lastResource);
	}
	
	public ArrayList<Subscription> get(String pi) {
		return subscriptionMap.get(piMap.get(pi));
	}
	
	public String getName(String pi) {
		return piMap.get(pi);
	}
	
	public void remove(String id, Node node, int k) throws URISyntaxException {
		switch(node) {
			case SENSOR:
				subscriptionMap.remove(id);
				break;
			case ACTUATOR:
			case USER:
				String[] resources = subscriptionMap.keySet().toArray(new String[]{});
				ArrayList<Subscription> subs;
				for (int i=0; i<resources.length; i++) {
					subs = subscriptionMap.get(resources[i]);
					for (int j=0; j<subs.size(); j++) {
						if (subs.get(j).receiver.id.equals(id))
							subs.remove(j);																	// Remove all subscriptions containing the receiver
					}
					if (subs.size()==0) {																	// If there are no subscriptions anymore, remove the subscription to the corresponding resource
//						debugStream.out("Deleting subscription on \"" + resources[i] + "\"", k);
//						String[] uri = new String[] {context + Constants.mnPostfix, resources[i], "data", "subscription"};
//						CoapResponse response_ = null;
//						cseClient.stepCount();
//						response_ = cseClient.services.deleteSubscription(uri,cseClient.getCount());
//						if (response_==null || response_.getCode()!=ResponseCode.DELETED) {
//							errStream.out("Unable to delete subscription on \"" + resources[i] + "\", response: " + response_.getCode(), //
//									k, Severity.LOW);
//						}
//						subscriptionMap.remove(resources[i]);
						deleteSubscription(resources[i], k);
					}
				}
				break;
		}
	}
	
	public void remove(String sender, String receiver, int k) throws URISyntaxException {
		ArrayList<Subscription> subs = subscriptionMap.get(sender);
		for (int j=0; j<subs.size(); j++) {
			if (subs.get(j).receiver.id.equals(receiver))
				subs.remove(j);																				// Remove all subscriptions containing the receiver
		}
		if (subs.size()==0) {
			deleteSubscription(sender, k);
		}
	}
	
	public void remove(String sender, String event, String receiver, String action, int k) throws URISyntaxException {
		ArrayList<Subscription> subs = subscriptionMap.get(sender);
		Subscription ref;
		for (int j=0; j<subs.size(); j++) {
			ref = subs.get(j);
			if (ref.receiver.id.equals(receiver) && ref.event.equals(event) && ref.action.equals(action))
				subs.remove(j);																				// Remove all subscriptions both containing the receiver and matching the pair event/action
		}
		if (subs.size()==0) {
			deleteSubscription(sender, k);
		}
	}
	
	private void deleteSubscription(String resource, int k) throws URISyntaxException {
		debugStream.out("Deleting subscription on \"" + resource + "\"", k);
		String[] uri = new String[] {context + Constants.mnPostfix, resource, "data", "subscription"};
		CoapResponse response_ = null;
		cseClient.stepCount();
		response_ = cseClient.services.deleteSubscription(uri,cseClient.getCount());
		if (response_==null || response_.getCode()!=ResponseCode.DELETED) {
			errStream.out("Unable to delete subscription on \"" + resource + "\", response: " + response_.getCode(), //
					k, Severity.LOW);
		}
		subscriptionMap.remove(resource);
	}

}
