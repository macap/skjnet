package skjnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class GetServerResponse extends ServerResponse {

	public GetServerResponse(OutputStream os, ServerRequest r) {
		super(os, r);
	}

	public void send() throws Exception{
		
		String filename = request.getHeader("file");
 		String range[] = request.getHeader("range").split("-");
 	
 		File myFile = new File(AppData.getInstance().getDIR()+filename);
 		if(myFile.exists() && !myFile.isDirectory()) { 
 			
 			FileInputStream fis = new FileInputStream(myFile);
 			int start = Integer.parseInt(range[0]),
 				end = (range.length==2) ? Integer.parseInt(range[1]) : (int)myFile.length();
			if (start > (int)myFile.length()) start = (int)myFile.length();
			if (end > (int)myFile.length()) end = (int)myFile.length();		  
			   	   
			status = "OK";
			setHeader("file", myFile.getName());
			setHeader("size", String.valueOf(myFile.length()));
			setHeader("range", start+"-"+end);
			setHeader("length", String.valueOf(end-start));
			setHeader("sum", MD5.checksum(myFile));
			sendHeaders();
			  
			  
	 		fis.skip(start);
	 		byte[] bytes = new byte[8192];
	 		AppData.getInstance().log.info("SERVER: Sending " + myFile.getName() + "(" +(end-start) + " bytes)");
	 		
	 		int count;				         
	        long size = (end-start);
				
			while (size > 0 && (count = fis.read(bytes,0, (int)Math.min(bytes.length, size))) != -1)
			{
			  os.write(bytes, 0, count);		
			  size-=count;
			}

			fis.close();
 			
 		} else {
 			status = "NOK";
 			setHeader("error", "File not found");
 			sendHeaders();
 		}
 		
		
	}
}
