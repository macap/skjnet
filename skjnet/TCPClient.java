package skjnet;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class TCPClient {
	
	public final static String DIR = "/Users/maciek/torrent2/";
	
	public static void start(int port) throws Exception {
		
		while (true) {
		
			BufferedReader userCommand = new BufferedReader(new InputStreamReader(System.in));
			String command[] = userCommand.readLine().split(" ");
			
			Socket clientSocket = new Socket("127.0.0.1", port);
			clientSocket.setTcpNoDelay(true);
			
			DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
			BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			
			String line, status,response;
			HashMap<String, String> headers;
			
//			outToServer.writeBytes(sentence + '\n');
//			
//			String response= "";
//		
//			while(!modifiedSentence.equals("0")) {
//				System.out.println(">" + modifiedSentence);
//				modifiedSentence= inFromServer.readLine();
//			};
//			
			
			
			switch (command[0].toLowerCase()) {
			
				case "list":
					outToServer.writeBytes("SKJNET LIST"+'\n'+'\n');
					
					line = inFromServer.readLine();
					headers = new HashMap<String, String>();
					response="";
					
//					System.out.println(">"+line);
					
					//if not SKJNET protocol close connection:
					 if (!line.startsWith("SKJNET")) {
						 clientSocket.close();
						 continue;
					 }
					 
					
				    status = line;
				    

				    while((line=inFromServer.readLine())!=null){ 
//				    	 System.out.println(">"+line);
						 if (line.isEmpty()) break;
						 String[] h = line.split(":");
						 headers.put(h[0], h[1]);
						 
					 }
				    
				    
					if (status.equals("SKJNET OK")) {
						
						while((line=inFromServer.readLine())!=null){ 
//							 System.out.println(">"+line);
							 response = response.concat('\n'+line);
						 }
						
						System.out.println(response);
						
					} else {
						System.out.println("Error:"+headers.get("error"));
					}
					 
					
					
				break;
				case "get":
					
					//if file exists - will continue download
					File myFile = new File(DIR+command[1]);
					boolean append = myFile.exists();
			 		int start = (int) ((myFile.exists()) ? myFile.length() : 0); 
			 		
			 		System.out.println("started get"+command[1]+" from "+start);
					
					outToServer.writeBytes("SKJNET GET"+'\n');
			 		outToServer.writeBytes("file:"+command[1]+'\n');
			 		outToServer.writeBytes("range:"+start+"-"+'\n'+'\n');

				
					line = inFromServer.readLine();
					headers = new HashMap<String, String>();
					response="";
					
					//if not SKJNET protocol close connection:
					 if (!line.startsWith("SKJNET")) {
						 clientSocket.close();
						 continue;
					 }
					
				    status = line;
				    
				    //get response headers:
				    while((line=inFromServer.readLine())!=null){ 
				    	 System.out.println(">"+line);
						 if (line.isEmpty()) break;
						 String[] h = line.split(":");
						 headers.put(h[0], h[1]);
					 }
				    
				   
					if (status.equals("SKJNET OK")) {
					
						String fname = headers.get("file");
				 		
				 		
						InputStream sis = clientSocket.getInputStream();				 		
						
						FileOutputStream fos = new FileOutputStream(DIR+fname, append);
						
						int count;
						byte[] buffer = new byte[8192]; // or 4096, or more
						while ((count = sis.read(buffer)) > 0)
						{
						  fos.write(buffer, 0, count);

						  //FUNC: check download continue:
//						  if (!append && (new File(DIR+fname).length())>16000) {
//							  break;
//						  }
						}
						
						System.out.println("> total:"+(new File(DIR+fname).length()));
						
						fos.close();
						sis.close();	
				 		
				 		System.out.println("File ok:"+fname);
					} else {
						System.out.println("Error");
					}
					
				
			 		
			 		
			 		outToServer.flush();
					outToServer.close();
					
					
				break;
				case "push":
					
					File file = new File(DIR+command[1]);
					
					if (!file.exists()) {
						System.out.println("File not found.");
						continue;
					}
					
					outToServer.writeBytes("SKJNET PUSH"+'\n');
			 		outToServer.writeBytes("file:"+file.getName()+'\n');
			 		outToServer.writeBytes("size:"+file.length()+'\n');
			 		outToServer.writeBytes("sum:"+MD5.checksum(file)+'\n'+'\n');
			 		
			 		FileInputStream fis = new FileInputStream(file);
			 		
			 		OutputStream out = clientSocket.getOutputStream();
			 		byte[] bytes = new byte[16*1024];
			 		System.out.println("Sending " + file.getName() + "(" + file.length() + " bytes)");
			 		 
			         int count;
			         while ((count = fis.read(bytes)) > 0) {
			             out.write(bytes, 0, count);
			         }
			 		
			 		
			        out.flush();
					out.close();
					fis.close();
			 		
			 		
				break;
				default:
					System.out.println("Command not found");
					clientSocket.close();
					continue;
				
			
			}
			
			
			
			
			
			clientSocket.close();
		}
	}
}
