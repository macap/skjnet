package skjnet;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
	
//	public final static String DIR = "/Users/maciek/torrent2/";

	public static void list() {
		list(false);
	}
	
	public static void list(boolean silent) {
		AppData ad = AppData.getInstance();
		ad.clearFileList();
		for (int i=1; i<=ad.getAppsCount(); i++) {
			if (i==ad.getAppId()) continue;
			ListRequest r = new ListRequest(i);
			r.silent = silent;
			r.send();
		}
		
	}
	
	public static void get(int index) {
		GetRequest gr = new GetRequest(index);
		gr.send();
	}
	
	public static void gets(int index) {
		SimultaneousDownloadService sds = new SimultaneousDownloadService(index);
		sds.send();
	}
	
	public static void push(int targetInstance, String filename) {
		PushRequest pr = new PushRequest(targetInstance, filename);
		pr.send();
	}
	
	public static void start(String DIR) throws Exception {
		AppData ad = AppData.getInstance();
		
		
		while (true) {
			BufferedReader userCommand = new BufferedReader(new InputStreamReader(System.in));

			String command[] = userCommand.readLine().split(" ");
			
			if (!command[0].toLowerCase().equals("list") && ad.fileinfo.isEmpty())  //TODO: what if there's no files on any server
				list(true);
			
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
				case "gets":
					if (command.length>1) 
						gets(Integer.parseInt(command[1]));
					else 
						System.out.println("Not enough arguments");
				break;
				default:
					System.out.println("Command not found");
			}
		
		}
	}
}
