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
			 System.out.println("SERVER: Accepted connection: " + connectionSocket);
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
			 
			 System.out.println("SERVER: Client command: "+command);
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
			 			  
		 					
	 					  
			 			  
			 			  byte [] mybytearray  = new byte [end-start];
			 	          fis = new FileInputStream(myFile);
//			 	          bis = new BufferedInputStream(fis);
			 	          
			 	          
			 	       //response:  
	 					outToClient.writeBytes("SKJNET OK"+'\n');
				 		outToClient.writeBytes("file:"+myFile.getName()+'\n');
				 		outToClient.writeBytes("size:"+(int)myFile.length()+'\n');
				 		outToClient.writeBytes("range:"+start+"-"+end+'\n');
				 		outToClient.writeBytes("length:"+mybytearray.length+'\n');
				 		outToClient.writeBytes("sum:"+MD5.checksum(myFile)+'\n'+'\n');
			 	        
				 		
				 		fis.skip(start);
//			 	          bis.skip(start);
//			 	          bis.read(mybytearray,0,mybytearray.length);

				 		 OutputStream out = connectionSocket.getOutputStream();
				 		
				 		 byte[] bytes = new byte[16*1024];

				 		System.out.println("SERVER: Sending " + myFile.getName() + "(" + mybytearray.length + " bytes)");
				 		 
				         int count;
				         while ((count = fis.read(bytes)) > 0) {
				             out.write(bytes, 0, count);
				         }
				 		
				 		
			 	          
//			 	          outToClient.write(mybytearray,0,mybytearray.length);
			 	        
			 	          System.out.println("SERVER: Done.");
//			 	         outToClient.writeBytes("0"+'\n');
						outToClient.flush();
						outToClient.close();
						fis.close();
						
			 			
			 			
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
					
					System.out.println("SERVER: > total:"+(new File(DIR+fname).length()));
					
					fos.close();
					sis.close();	
			 		
//			 		System.out.println("SERVER: File ok:"+fname);
			 		

			 		
			 		outToClient.writeBytes("SKJNET OK"+'\n');
			 		
			 		outToClient.flush();
					outToClient.close();
			 		

		 		break;
		 		default:
		 			outToClient.writeBytes("SKJNET NOK"+'\n');
		 			outToClient.writeBytes("error:Command not found"+'\n'+'\n');
					outToClient.flush();
					outToClient.close();

	 			break;
		 		
			 }
			} catch (SocketException e){
				
				
				
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