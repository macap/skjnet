package skjnet;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ListRequest extends Request {
	public boolean silent = false;
	
	public ListRequest(int targetAppId) {
		super(targetAppId);
		command = "LIST";
	}
	
	public void getResponseBody() {
		try {
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(is));
		
		String line;

		while((line=inFromServer.readLine())!=null){ 
			
			 String d[] = line.split("\t",-1); 
			 FileInfo f = new FileInfo(d[0],d[1], Integer.parseInt(d[2]));
			 f.appId = targetAppId;
			 
			 ad.fileinfo.add(f);
			 if (!silent) System.out.println("#"+ad.fileinfo.indexOf(f)+'\t'+f);

		 }
		
		
		inFromServer.close();
		
		
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("ERROR getting filelist");
		}
	}
	
}
