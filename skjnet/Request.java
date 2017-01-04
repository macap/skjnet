package skjnet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

public class Request {
	String command;
	Socket socket =null;
	int targetAppId;
	HashMap<String, String> headers;
	AppData ad;
	Response response;
	
	public Request(int targetAppId) {
		ad = AppData.getInstance();
		this.targetAppId = targetAppId;
		headers = new HashMap<String,String>();
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public boolean connect() {
		
		int port = ad.getPortOffset()+targetAppId;
		
		try {
			socket = new Socket("127.0.0.1", port);
			socket.setTcpNoDelay(true);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		
		return true;
	}
	
	public Response getResponseHeaders() {
		if (socket.isClosed()) return null;
		Response resp = new Response(socket);
		
		resp.readHeaders();

		return resp.valid() ? resp : null;
	}
	
	public boolean send() {
		
		if (socket==null) connect();
		
		try {
			DataOutputStream os = new DataOutputStream(socket.getOutputStream());
			os.writeBytes("SKJNET "+command+'\n');
			
			
			for(Entry<String, String> e: headers.entrySet()){
				os.writeBytes(e.getKey()+":"+e.getValue()+'\n');
			}
			
			os.writeBytes(""+'\n');
		
			sendBody();
			
			getResponse();
			
		} catch (Exception e) {
			System.out.println("--- ERROR sending data:");
			e.printStackTrace();
			
			return false;
		}

		return true;
	}
	
	public void sendBody() {
		
	}
	
	public void getResponse() {
		response = getResponseHeaders();
		
		if (response!=null) {
			getResponseBody();
			return;
		}
		
		try {
			socket.close();
			socket = null;
		} catch (Exception e) {
			System.out.println("Error closing socket");
			e.printStackTrace();
		}
	}
	
	public void getResponseBody() {
		
	}
	
}
