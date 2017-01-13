package skjnet;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map.Entry;

public abstract class ServerResponse {
	ServerRequest request;
	OutputStream os;
	String status;
	HashMap<String, String> headers;
	
	public ServerResponse(OutputStream os) {
		this.os = os;
		headers = new HashMap<String,String>();
	}
	
	public ServerResponse(OutputStream os, ServerRequest r) {
		this(os);
		request = r;
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public void sendHeaders() throws Exception{
		
		String header = "SKJNET "+status+'\n';
		os.write(header.getBytes());
		
		for(Entry<String, String> e: headers.entrySet()){
			String h = e.getKey()+":"+e.getValue()+'\n';
			os.write(h.getBytes());
		}
		
		os.write('\n');
	}
	
	
	
}
