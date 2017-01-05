package skjnet;

import java.io.*;
import java.net.*;
import java.util.HashMap;

public class TCPServer {
	
	
	public static void start(int port, String DIR) throws Exception {
		ServerSocket welcomeSocket = new ServerSocket(port); 
		
		while(true) {
			Socket connectionSocket = welcomeSocket.accept();
			try {
			 //Connect client:
			 //LOG: System.out.println("SERVER: Accepted connection: " + connectionSocket);
			 //Read client command
			 BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
			 
			 String line=inFromClient.readLine();
			 
			 //if not SKJNET protocol close connection:
			 if (!line.startsWith("SKJNET")) {
				 connectionSocket.close();
				 continue;
			 }
			 //Command:
			 String command = line.split(" ")[1];
			 
			 //HEADERS:
			 HashMap<String, String> headers = new HashMap<String, String>();
			 
			 while((line=inFromClient.readLine())!=null){ 
				 if (line.isEmpty()) break;
				 String[] h = line.split(":");
				 headers.put(h[0], h[1]);
			 }
			 
			 //LOG: System.out.println("SERVER: Client command: "+command);
			 DataOutputStream outToClient =new DataOutputStream(connectionSocket.getOutputStream());
		 		
			 switch(command.toLowerCase()) {
			 
			 	case "list":			 		
			 		outToClient.writeBytes("SKJNET OK"+'\n'+'\n');
			 		
			 		File f = new File(DIR);
			 		File[] fileList = f.listFiles();
			 		
			 		for(File p:fileList ) {
			 			outToClient.writeBytes(p.getName()+'\t'+MD5.checksum(p)+'\t'+(int)p.length()+'\n');
			 		}
			 		
					outToClient.flush();
					outToClient.close();
		 		break;
			 	case "get":		
			 		String filename = headers.get("file");
			 		
			 		String range[] = headers.get("range").split("-");
			 	
			 		
			 		File myFile = new File(DIR+filename);
			 		if(myFile.exists() && !myFile.isDirectory()) { 
			 	
			 			  FileInputStream fis = null;
			 			  BufferedInputStream bis = null;
			 			  
			 			  int start = Integer.parseInt(range[0]),
		 					  end = (range.length==2) ? Integer.parseInt(range[1]) : (int)myFile.length();
			 			  
		 					if (start > (int)myFile.length()) start = (int)myFile.length();
		 					if (end > (int)myFile.length()) end = (int)myFile.length();
	 					  
			 			  
			 	          fis = new FileInputStream(myFile);
			 	         
			 	          
			 	       //response:  
	 					outToClient.writeBytes("SKJNET OK"+'\n');
				 		outToClient.writeBytes("file:"+myFile.getName()+'\n');
				 		outToClient.writeBytes("size:"+(int)myFile.length()+'\n');
				 		outToClient.writeBytes("range:"+start+"-"+end+'\n');
				 		outToClient.writeBytes("length:"+(end-start)+'\n');
				 		outToClient.writeBytes("sum:"+MD5.checksum(myFile)+'\n'+'\n');
			 	        
				 		outToClient.flush();
				 		
				 		fis.skip(start);

				 		 OutputStream out = connectionSocket.getOutputStream();
				 		
				 		 byte[] bytes = new byte[8192];

				 		 //LOG: System.out.println("SERVER: Sending " + myFile.getName() + "(" +(end-start) + " bytes)");
				 		 
				 		 int readedSoFar = start;
				         int count;
				         
				         
				         long size = (end-start);
							
						

						while (size > 0 && (count = fis.read(bytes,0, (int)Math.min(bytes.length, size))) != -1)
						{
						  out.write(bytes, 0, count);		
						  size-=count;
						}
						
						//LOG: System.out.println("SIZE:"+size);
				         

				        out.flush();
						connectionSocket.close();
				 		out.close();
				 		fis.close();
			 	          
			 	        
			 	        //LOG: System.out.println("SERVER: Done.");

						
			 			
			 			
			 		} else {
			 			outToClient.writeBytes("SKJNET NOK"+'\n');
			 			outToClient.writeBytes("error:File not found"+'\n'+'\n');
						outToClient.flush();
						outToClient.close();
			 		}
			 		
			 		
			 		
		 		break;
			 	case "push":
			 		
			 		String fname = headers.get("file");
			 		
			 		
					InputStream sis = connectionSocket.getInputStream();				 		
					
					FileOutputStream fos = new FileOutputStream(DIR+fname);
					
					int count;
					byte[] buffer = new byte[8192]; // or 4096, or more
					while ((count = sis.read(buffer)) > 0)
					{
					  fos.write(buffer, 0, count);
					}
					
					//LOG: System.out.println("SERVER: > total:"+(new File(DIR+fname).length()));
					
					System.out.println(">Received file "+fname);
					
					
					fos.close();
			 		
			 		outToClient.flush();
			 		sis.close();				 		
			 		
//			 		outToClient.close();
			 		

		 		break;
		 		default:
		 			outToClient.writeBytes("SKJNET NOK"+'\n');
		 			outToClient.writeBytes("error:Command not found"+'\n'+'\n');
					outToClient.flush();
					outToClient.close();

	 			
	 			
		 		
			 }
			 
		
			 
			 	
			 
			} catch (SocketException e){
				e.printStackTrace();
				
				
			} catch (Exception e) {
				DataOutputStream outToClient =new DataOutputStream(connectionSocket.getOutputStream());
				
				outToClient.writeBytes("SKJNET NOK"+'\n');
	 			outToClient.writeBytes("error: Bad request or Internal server error"+'\n'+'\n');
				outToClient.flush();
				outToClient.close();
			}
			
			 
		}
		
		
	}
}
