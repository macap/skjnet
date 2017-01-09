package skjnet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
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
		
		try {
//			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(is));
			DataInputStream inFromServer = new DataInputStream(is);
			
			
			String line;
			status = inFromServer.readLine();
			//if not SKJNET protocol close connection:
			 if (!ok()) {
				valid = false;
				return;
			 }
			
		    while((line=inFromServer.readLine())!=null){ 
				 if (line.isEmpty()) break;
				 String[] h = line.split(":");

				 headers.put(h[0], h[1]);
			 }
		    
//		    inFromServer.close();
		    
		    valid = true;
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
