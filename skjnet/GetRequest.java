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
	
		//if file exists - will continue download
		FileInfo fi = ad.fileinfo.get(fileIndex);
		File myFile = new File(ad.getDIR()+fi.name);
	
 		if (myFile.exists()) {
 			append = true;
 			setHeader("range", myFile.length()+"-");
 		}
 		
	}
	
	public void getResponseBody() {

//		System.out.println(response);
		String fname = (customFileName==null) ? response.headers.get("file") : customFileName;
	 	try {
			InputStream sis = socket.getInputStream();				 		
			FileOutputStream fos = new FileOutputStream(ad.getDIR()+fname, append);
				
			int count;
			byte[] buffer = new byte[8192]; 
			while ((count = sis.read(buffer)) > 0)
			{
			  fos.write(buffer, 0, count);
			  if (breakDownload && !append && (new File(ad.getDIR()+fname).length())>16000) break;
				  
			}
			fos.flush();
			fos.close();
			sis.close();	
		 	System.out.println("File ok:"+fname);
	 	} catch (Exception e) {
	 		System.out.println("error");
	 		e.printStackTrace();
	 	}
	
	}
}
