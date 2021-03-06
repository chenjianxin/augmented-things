package mecs.iot.proj.om2m.asn.user_direct;

import mecs.iot.proj.om2m.dashboard.Command;
import mecs.iot.proj.om2m.dashboard.Console;
import mecs.iot.proj.om2m.dashboard.DebugStream;
import mecs.iot.proj.om2m.dashboard.ErrStream;
import mecs.iot.proj.om2m.dashboard.Severity;
import mecs.iot.proj.om2m.structures.Constants;
import mecs.iot.proj.om2m.structures.Format;

import java.net.URISyntaxException;

public class App 
{
	
	final private static String id = "user";
	final private static String host = Constants.computerName();
	final private static boolean debug = true;
	
	final private static DebugStream debugStream = new DebugStream(Format.joinIdHost(id+"/main",host),debug);
	final private static ErrStream errStream = new ErrStream(Format.joinIdHost(id+"/main",host));
	
	final private static String context = Constants.context;
	final private static String address = Constants.protocol + Constants.inAddressASN(debugStream,0) + Constants.inADNRoot;
	final private static String ip = Constants.ip(debugStream,0);
	
    public static void main( String[] args )
    {
    	/* 
    	 * To use a custom shell, implement the mecs.iot.proj.Interface interface and replace 'true' with an instance of such implementation.
    	 * When working with default AT Shell, be sure that a sensor with serial "0x0001" exists.
    	 */
    	final Console console = new Console(id,host,true,debug);
		try {
			final RemoteInterface remote = new RemoteInterface(id,host,address,context,debug,console,ip,5691);
			Command exit = (s) -> {console.terminate(); remote.terminate(); return "Exiting";};
			console.add("exit",exit,0,"Terminate this asn","exit",false);
			remote.start();
		} catch (URISyntaxException e) {
			errStream.out(e,0,Severity.MEDIUM);
		}
    }

}