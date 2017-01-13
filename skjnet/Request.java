package skjnet;

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;
import java.util.Map.Entry;

public abstract class Request {
	String command;
	Socket socket =null;
	int targetAppId;
	HashMap<String, String> headers;
	AppData ad;
	Response response;
	OutputStream os = null;
	InputStream is = null;
	
	public Request(int targetAppId) {
		ad = AppData.getInstance();
		this.targetAppId = targetAppId;
		headers = new HashMap<String,String>();
	}
	
	public void setHeader(String key, String value) {
		headers.put(key, value);
	}
	
	public boolean connect() {
		//not used
		return true;
	}
	
	public Response getResponseHeaders() {
		if (socket.isClosed()) return null;
		Response resp = new Response(is);
		
		resp.readHeaders();

		return resp.valid() ? resp : null;
	}
	
	public void sendHeaders() {
		
	}
	
	protected boolean send() {
		
		//if (socket==null) connect();
		
		try {
			int port = ad.getPortOffset()+targetAppId;
			socket = new Socket("127.0.0.1", port);
			os = socket.getOutputStream();
			is = socket.getInputStream();
			
			String comm = "SKJNET "+command+'\n';
			os.write(comm.getBytes());
			
			
			for(Entry<String, String> e: headers.entrySet()){
				String header = e.getKey()+":"+e.getValue()+'\n';
				os.write(header.getBytes());
			}
			
			os.write('\n');
		
			sendBody();
			
			getResponse();
			
			is.close();
			is = null;
			os.close();
			os = null;
			
			ad.log.info(this.toString());
			if (response!=null) ad.log.info(this.response.toString());
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (Exception e) {
			System.out.println("ERROR sending data:");
			e.printStackTrace();
			
			return false;
		} finally {
			closeSocket();
		}
		
		

		return true;
	}
	
	public void closeSocket() {
		try {
			if (socket!=null) socket.close();
			socket = null;
		} catch (Exception e) {
			System.out.println("Unable to close socket");
		}
	}
	
	public void sendBody() {
		
	}
	
	public void getResponse() {
		response = getResponseHeaders();
		
		if (response!=null) {
			getResponseBody();
			return;
		}
	
	}
	
	public void getResponseBody() {
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("SKJNET ");
		sb.append(command);
		sb.append(" REQUEST"+'\n');
		
		for(Entry<String, String> e: headers.entrySet()){
			sb.append(e.getKey()+":"+e.getValue()+'\n');
		}
		
		return sb.toString();
	}
	
}
