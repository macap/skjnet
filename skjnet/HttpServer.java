package skjnet;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;


public class HttpServer {
	public static void start(int port, String DIR) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(port); 
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			try {
			 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
		
			 String line=inFromClient.readLine();		
			 

			 if (line.indexOf("GET") <0) {
				 connectionSocket.close();
				 continue;
			 }
			 
			 /*
			  * HTTP/1.0 200 OK
				Connection: close
				Content-Type: text/html
			  */
			 
			 AppData ad = AppData.getInstance();
			 
			 File logFile = new File(ad.getLogFileName());
			 
			 
			 

			 DataOutputStream outToClient =new DataOutputStream(connectionSocket.getOutputStream());
		 		
			 outToClient.writeBytes("HTTP/1.0 200 	OK\n"
			 		+ "Connection: close\n"
			 		+ "Content-Type: text/plain\n"
			 		+ "Content-Length: "+logFile.length()+"\n\n");
		 		
			 FileInputStream fis = new FileInputStream(logFile);
		 		
		 		
			 	byte[] bytes = new byte[16*1024];
		 		
 
		         int count;
		         while ((count = fis.read(bytes)) > 0) {
		        	
		             outToClient.write(bytes, 0, count);
		         }
			 
			 fis.close();
			outToClient.flush();
			outToClient.close();
		 	
			 
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
