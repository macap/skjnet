package skjnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class GetRequest extends Request {
	
	private final static int RETRY_LIMIT = 3;
	private int retries = 0;
	private boolean failed = false;
	
	public boolean breakDownload = false; // terminate transfer to test if download can continue later
	private boolean append = false; //if true appends data to exising file
	public String customFileName = null;
	
	
	public GetRequest(int fileIndex, int start, int end) {
		super(-1);
		
		command = "GET";
		
		FileInfo fi = ad.fileinfo.get(fileIndex);
		
		targetAppId = fi.appId;
		setHeader("file", fi.name);
		setHeader("range", start+"-"+((end==-1)?"":end));
	}
	
	public GetRequest(int fileIndex, int start, int end, boolean append) {
		this(fileIndex,start,end);
		this.append = append;
	}
	
	public GetRequest(int fileIndex) {
		this(fileIndex, 0, -1);
	
		//if file exists - will continue download
		FileInfo fi = ad.fileinfo.get(fileIndex);
		File myFile = new File(ad.getDIR()+fi.name);
	
 		if (myFile.exists()) {
 			append = true;
 			setHeader("range", myFile.length()+"-");
 		}
	}
	
	public void getResponseBody() {

		//LOG: System.out.println(response);
		String fname = (customFileName==null) ? response.headers.get("file") : customFileName;
	 	try {
			InputStream sis = socket.getInputStream();				 		
			FileOutputStream fos = new FileOutputStream(ad.getDIR()+fname, append);
			
			long size =  Long.parseLong(response.headers.get("length"));
				
			int count;
			byte[] buffer = new byte[8192*4]; 
			while (size > 0 && (count = sis.read(buffer,0, (int)Math.min(buffer.length, size))) != -1)
			{
			  fos.write(buffer, 0, count);
			  if (breakDownload && !append && (new File(ad.getDIR()+fname).length())>16000) break;
			  size-=count;
			}

			
			fos.flush();
			fos.close();
			sis.close();	
			if (!breakDownload && (size>0 || (new File(ad.getDIR()+fname)).length() != Integer.parseInt(response.headers.get("length")))) {
				//transfer failed - should retry
				//LOG: System.out.println("SIZE "+fname+" MISMATCH "+(new File(ad.getDIR()+fname)).length()+'\t'+response.headers.get("length"));
				System.out.println("Retrying...");
				failed =true;
				
				
			} else {
				System.out.println("File ok:"+fname);
			}
	 	} catch (Exception e) {
	 		System.out.println("error");
	 		e.printStackTrace();
	 	}
	
	}
	
	public void closeSocket() {
		super.closeSocket();
		
		if (failed) retry();
	}
	
	public void retry() {
		failed = false;
		
		if (retries++ > RETRY_LIMIT) {
			System.out.println("File download failed after "+retries+" attempts.");
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {

		}
		
		//delete old file
		String fname = (customFileName==null) ? response.headers.get("file") : customFileName;
		File f = new File(ad.getDIR()+fname);
		f.delete();
		//clear response
		response = null;
		
		//send again
		send();
	}
}
