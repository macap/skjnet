package skjnet;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
	
//	public final static String DIR = "/Users/maciek/torrent2/";

	
	public static void list() {
		AppData ad = AppData.getInstance();
		ad.clearFileList();
		for (int i=1; i<=ad.getAppsCount(); i++) {
			if (i==ad.getAppId()) continue;
			Request r = new ListRequest(i);
			r.send();
		}
		
	}
	
	public static void get(int index) {
		GetRequest gr = new GetRequest(index);
		gr.send();
	}
	
	public static void push(int targetInstance, String filename) {
		PushRequest pr = new PushRequest(targetInstance, filename);
		pr.send();
	}
	
	public static void start(String DIR) throws Exception {
		
		
		
		while (true) {
			BufferedReader userCommand = new BufferedReader(new InputStreamReader(System.in));

			String command[] = userCommand.readLine().split(" ");
			
			switch (command[0].toLowerCase()) {
			
				case "list":
					list();
				break;
				case "get":
					if (command.length>1) 
						get(Integer.parseInt(command[1]));
					else 
						System.out.println("Not enough arguments");
				break;
				case "push":
					if (command.length>2) 
						push(Integer.parseInt(command[1]), command[2]);
					else 
						System.out.println("Not enough arguments");		
				break;
				default:
					System.out.println("Command not found");
			}
			
			
			
//				System.out.println("Select host you want to reach:");
//				int appId = Integer.parseInt(userCommand.readLine());
//				int port = 10000+appId;
//				
//				Socket clientSocket = new Socket("127.0.0.1", port);
//				clientSocket.setTcpNoDelay(true);
//				
//				DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
//				BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//				
//				String line, status,response;
//				HashMap<String, String> headers;
//	
//				clientSocket.close();
				
		}
	}
}
