package skjnet;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;


public class GetRequest extends Request {
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
	}
	
	public void getResponseBody() {
		String fname = (customFileName==null) ? response.headers.get("file") : customFileName;		
	 	try {
	 		
			InputStream sis = socket.getInputStream();				 		
			FileOutputStream fos = new FileOutputStream(ad.getDIR()+fname, append);
			
			long size =  Long.parseLong(response.headers.get("length"));
				
			int count;
			byte[] buffer = new byte[8192]; 
			while (size > 0 && (count = sis.read(buffer,0, (int)Math.min(buffer.length, size))) != -1)
			{
			  fos.write(buffer, 0, count);
			  if (breakDownload && (new File(ad.getDIR()+fname).length())>16000) break;
			  size-=count;
			}
			
			fos.close();	
			
			if (!breakDownload && (size>0 || (!append && (new File(ad.getDIR()+fname)).length() != Integer.parseInt(response.headers.get("length"))))) {
				throw new Exception();
			} else {
				System.out.println("File ok:"+fname);
			}
	 	} catch (Exception e) {
	 		System.out.println("Error getting file");
	 		e.printStackTrace();
	 	}
	
	}

}
