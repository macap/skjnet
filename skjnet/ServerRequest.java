package skjnet;

import java.io.DataInputStream;
import java.io.InputStream;
import java.util.HashMap;

public class ServerRequest {
	InputStream is;
	HashMap<String, String> headers;
	public String command;
	
	
	public ServerRequest(InputStream is) {
		this.is = is;
		headers = new HashMap<String,String>();
	}
	
	public void read() throws BadRequestException {
		if (!readHeaders()) throw new BadRequestException();
	}
	
	public boolean readHeaders() {
		DataInputStream inFromServer = new DataInputStream(is);
		String line;
		try {
			String requestHeader = inFromServer.readLine();
			String splittedHeader[] =requestHeader.split(" "); 
			
			if (splittedHeader.length!=2 || !splittedHeader[0].equals("SKJNET")) throw new Exception();
			
			command = splittedHeader[1];
			
		    while((line=inFromServer.readLine())!=null){ 
				 if (line.isEmpty()) break;
				 String[] h = line.split(":");
				 headers.put(h[0], h[1]);
			 }
		} catch (Exception e) {
			return false;
		}	
		return true;
	}
	
	public String getHeader(String key) {
		return headers.get(key);
	}
}
