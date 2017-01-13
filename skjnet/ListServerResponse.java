package skjnet;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ListServerResponse extends ServerResponse {

	public ListServerResponse(OutputStream os) {
		super(os);
	}
	
	public ListServerResponse(OutputStream os, ServerRequest sr) {
		super(os,sr);
	}

	
	public void send() throws Exception {
	
		status = "OK";
		sendHeaders();
		
		File f = new File(AppData.getInstance().getDIR());
 		File[] fileList = f.listFiles();
 		
 		for(File p:fileList ) {
 			String filedata = p.getName()+'\t'+MD5.checksum(p)+'\t'+(int)p.length()+'\n';
 			os.write(filedata.getBytes());
 		}
	}
}
