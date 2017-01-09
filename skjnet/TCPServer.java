package skjnet;

import java.io.*;
import java.net.*;
import java.util.HashMap;

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
			
			//Read client command
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(is)); 
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
			 
			 AppData.getInstance().log.info("SERVER: Client command: "+command);
			 
			 //DataOutputStream outToClient =new DataOutputStream(os);
			
			 
			 switch(command.toLowerCase()) {
			 
			 	case "list":			 		
			 		//outToClient.writeBytes();
			 		String response = "SKJNET OK"+'\n'+'\n';
			 		os.write(response.getBytes());
			 		
			 		
			 		File f = new File(DIR);
			 		File[] fileList = f.listFiles();
			 		
			 		for(File p:fileList ) {
			 			String filedata = p.getName()+'\t'+MD5.checksum(p)+'\t'+(int)p.length()+'\n';
//			 			outToClient.writeBytes(filedata);
			 			os.write(filedata.getBytes());
			 		}
			 		
					//outToClient.flush();
					//outToClient.close();
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
//	 					outToClient.writeBytes("SKJNET OK"+'\n');
//				 		outToClient.writeBytes("file:"+myFile.getName()+'\n');
//				 		outToClient.writeBytes("size:"+(int)myFile.length()+'\n');
//				 		outToClient.writeBytes("range:"+start+"-"+end+'\n');
//				 		outToClient.writeBytes("length:"+(end-start)+'\n');
//				 		outToClient.writeBytes("sum:"+MD5.checksum(myFile)+'\n'+'\n');
//			 	        
	
			 	          String head[] = {
				 					"SKJNET OK"+'\n',
							 		"file:"+myFile.getName()+'\n',
							 		"size:"+(int)myFile.length()+'\n',
							 		"range:"+start+"-"+end+'\n',
							 		"length:"+(end-start)+'\n',
							 		"sum:"+MD5.checksum(myFile)+'\n'+'\n'
			 	          }; 
			 	          
			 	          for(String h : head) {
			 	        	 os.write(h.getBytes());
			 	          }
				 		
			 	          
			 	          
				 		
				 		fis.skip(start);

				 		 byte[] bytes = new byte[8192];
				 		 AppData.getInstance().log.info("SERVER: Sending " + myFile.getName() + "(" +(end-start) + " bytes)");
				 		 
				 		 int readedSoFar = start;
				         int count;
				        				         
				         long size = (end-start);
							
						while (size > 0 && (count = fis.read(bytes,0, (int)Math.min(bytes.length, size))) != -1)
						{
						  os.write(bytes, 0, count);		
						  size-=count;
						}
						
						//LOG: System.out.println("SIZE:"+size);
				         

				        //os.flush();
						connectionSocket.close();
				 		os.close();
				 		fis.close();
			 	        AppData.getInstance().log.info("SERVER: Done.");

						
			 			
			 			
			 		} else {
			 			//outToClient.writeBytes("SKJNET NOK"+'\n');
			 			//outToClient.writeBytes("error:File not found"+'\n'+'\n');
						//outToClient.flush();
						//outToClient.close();
			 		}
			 		
			 		
			 		
		 		break;
			 	case "push":
			 		
			 		String fname = headers.get("file");
			 		
					FileOutputStream fos = new FileOutputStream(DIR+fname);
					
					int count;
					byte[] buffer = new byte[8192]; // or 4096, or more
					while ((count = is.read(buffer)) > 0)
					{
					  fos.write(buffer, 0, count);
					}
					
					fos.close();
			 		//outToClient.flush();
			 		is.close();				 		
			 		
		 		break;
		 		default:
		 			//outToClient.writeBytes("SKJNET NOK"+'\n');
		 			//outToClient.writeBytes("error:Command not found"+'\n'+'\n');
					

			 }
			 
			 //outToClient.close();
			 
			 //outToClient.flush();
			 
			} catch (SocketException e) {
				
			} finally {

				 connectionSocket.close();
			}
			
		}
		
		
	}
}
