package skjnet;


import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map.Entry;

public class Response {
	String status;
	InputStream is;
	boolean valid;
	HashMap<String, String> headers;
	
	public Response(InputStream is) {
		this.is = is;
		headers = new HashMap<String,String>();
	}
	
	public void readHeaders() {
		DataInputStream inFromServer = new DataInputStream(is);
		String line;
		try {
			status = inFromServer.readLine();
			if (!ok()) throw new Exception();
		    while((line=inFromServer.readLine())!=null){ 
				 if (line.isEmpty()) break;
				 String[] h = line.split(":");
				 headers.put(h[0], h[1]);
			 }
		    valid = true;
		} catch (Exception e) {
			valid = false;
			System.out.println("Cannot read header");
			return;
		}	
	}
	
	public boolean valid() {
		return valid;
	}
	
	public boolean ok() {
		return status.equals("SKJNET OK");
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("RESPONSE: ");
		sb.append(status+'\n');
		
		for(Entry<String, String> e: headers.entrySet()){
			sb.append(e.getKey()+":"+e.getValue()+'\n');
		}
		
		sb.append("----");
		
		return sb.toString();
	}
}
