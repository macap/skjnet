package skjnet;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map.Entry;

public class Response {
	String status;
	Socket socket;
	boolean valid;
	HashMap<String, String> headers;
	
	public Response(Socket socket) {
		this.socket = socket;
		headers = new HashMap<String,String>();
	}
	
	public void readHeaders() {
		
		try {
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			
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
