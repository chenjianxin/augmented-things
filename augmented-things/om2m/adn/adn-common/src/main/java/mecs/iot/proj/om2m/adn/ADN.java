package mecs.iot.proj.om2m.adn;

import mecs.iot.proj.om2m.Client;
import mecs.iot.proj.om2m.dashboard.Console;
import mecs.iot.proj.om2m.dashboard.DebugStream;
import mecs.iot.proj.om2m.dashboard.ErrStream;
import mecs.iot.proj.om2m.dashboard.OutStream;

import java.util.List;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.server.resources.CoapExchange;

public class ADN extends CoapResource {
	
	public Client client;
	
	protected String name;
	protected String context;
	
	protected Subscriber subscriber;
	
	protected OutStream outStream;
	protected DebugStream debugStream;
	protected ErrStream errStream;
	
	protected Console console;
	
	protected int i;

	protected ADN(String name, String uri, String context, boolean debug, Console console) {
		super(uri);
		setObservable(true);
		this.name = name;
		this.context = context;
		outStream = new OutStream(name);
		debugStream = new DebugStream(name,debug);
		errStream = new ErrStream(name);
		i = 1;
		this.console = console;
	}

	protected String getUriValue(CoapExchange exchange, String attribute, int index) {
		List<String> query = exchange.getRequestOptions().getUriQuery();
		return query.get(index).substring(attribute.length()+1);
	}
	
	protected boolean isValidSerial(String serial) {
		// TODO
		return serial!=null && !serial.isEmpty();
	}
	
	protected boolean isValidId(String id) {
		// TODO (really needed?)
		return id!=null && !id.isEmpty();
	}
	
	protected boolean isValidType(String type) {
		// TODO
		return type!=null && !type.isEmpty();
	}
	
	protected boolean areValidAttributes(String[] attributes, Integer k) {
		for (int i=0; i<attributes.length; i++) {
			if (attributes[i]==null || attributes[i].isEmpty()) {
				k = new Integer(i);
				return false;
			}
		}
		return true;
	}
	
	protected boolean isValidLocation(String location) {
		if (location==null || location.isEmpty())
			return false;
		try {
			Integer.parseInt(location);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
	
	protected boolean isValidAddress(String address) {
		// TODO
		return address!=null && !address.isEmpty();
	}

}
