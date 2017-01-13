package skjnet;

import java.io.*;
import java.net.*;

public class TCPServer {
	
	
	public static void start(int port, String DIR) throws Exception {
		ServerSocket welcomeSocket = null;
		
        try {
        	welcomeSocket = new ServerSocket(port);
        } catch (IOException ex) {
            System.out.println("Can't setup server on this port number. ");
        }
		while(true) {
			Socket connectionSocket = null;
			InputStream is = null;
			OutputStream os = null;
			
			try {
			
				try {
		            connectionSocket = welcomeSocket.accept();
		        } catch (IOException ex) {
		            System.out.println("Can't accept client connection. ");
		        }
				AppData.getInstance().log.info("SERVER: Accepted connection: " + connectionSocket);
			
				try {
		            is = connectionSocket.getInputStream();
		        } catch (IOException ex) {
		            System.out.println("Can't get socket input stream. ");
		        }
				
				os = connectionSocket.getOutputStream();
				ServerRequest serverRequest = new ServerRequest(is);
				
				try {
					serverRequest.read();
				} catch (BadRequestException e) {
					 connectionSocket.close();
					 AppData.getInstance().log.info("SERVER: Bad request");
					 continue;
				}
				
				AppData.getInstance().log.info("SERVER: Client command: "+serverRequest.command);
					
			 
			 switch(serverRequest.command.toLowerCase()) {
			 
			 	case "list":			 		
			 		ListServerResponse lsr = new ListServerResponse(os, serverRequest);
			 		lsr.send();
		 		break;
			 	case "get":		
			 		GetServerResponse gsr = new GetServerResponse(os, serverRequest);
			 		gsr.send();
		 		break;
			 	case "push":
			 		String fname = serverRequest.getHeader("file");
					FileOutputStream fos = new FileOutputStream(DIR+fname);
					int count;
					byte[] buffer = new byte[8192]; // or 4096, or more
					while ((count = is.read(buffer)) > 0)
					{
					  fos.write(buffer, 0, count);
					}
					fos.close();			 		
		 		break;
		 		default:
		 			//outToClient.writeBytes("SKJNET NOK"+'\n');
		 			//outToClient.writeBytes("error:Command not found"+'\n'+'\n');
			 }
			 
			} catch (SocketException e) {
				
			} finally {
				is.close();
				os.close();
				connectionSocket.close();
			}
		}
		//TODO: Thread interruption support;
		//welcomeSocket.close();
		
		
	}
}
