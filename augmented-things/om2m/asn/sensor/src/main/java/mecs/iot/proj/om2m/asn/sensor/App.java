package mecs.iot.proj.om2m.asn.sensor;

import mecs.iot.proj.om2m.dashboard.DebugStream;
import mecs.iot.proj.om2m.dashboard.ErrStream;
import mecs.iot.proj.om2m.dashboard.Severity;
import mecs.iot.proj.om2m.structures.Constants;
import mecs.iot.proj.om2m.structures.Format;
import mecs.iot.proj.om2m.structures.ASN;

import java.net.URISyntaxException;

public class App 
{
	
	final private static String id = "sensor";
	final private static String host = Constants.computerName();
	final private static boolean debug = true;
	
	final private static DebugStream debugStream = new DebugStream(Format.joinIdHost(id+"/main",host),debug);
	final private static ErrStream errStream = new ErrStream(Format.joinIdHost(id+"/main",host));
	
	final private static String context = Constants.context;
	final private static String address = Constants.protocol + Constants.inAddressASN(debugStream,0) + Constants.inADNRoot;
	
	final private static ASN tag = new ASN(Format.joinIdHost(id,host),"0x0001","tempC",new String[]{"event"});
	final private static int location = 0;
	
    public static void main( String[] args )
    {
		try {
			final RemoteInterface remote = new RemoteInterface(tag,location,address,context,debug,36.0,0.05,0,5000);
			remote.start();
		} catch (URISyntaxException e) {
			errStream.out(e,0,Severity.MEDIUM);
		}
    }
    
}
