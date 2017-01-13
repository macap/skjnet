package skjnet;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;

public class PushRequest extends Request {
	File file;

	public PushRequest(int targetAppId, String filename) {
		super(targetAppId);
		
		file = new File(ad.getDIR()+filename);
		
		if (!file.exists()) {
			System.out.println("File not found.");
			return;
		}
		
		command = "PUSH";
		setHeader("file", file.getName());
		setHeader("size", file.length()+"");
		setHeader("sum", MD5.checksum(file));
	}
	
	public void sendBody() {
		try {
	 		FileInputStream fis = new FileInputStream(file);
	 		
	 		OutputStream out = socket.getOutputStream();
	 		byte[] bytes = new byte[16*1024];
	 		System.out.println("Sending " + file.getName() + "(" + file.length() + " bytes)");
	         int count;
	         while ((count = fis.read(bytes)) > 0) {
	             out.write(bytes, 0, count);
	         }
			fis.close();
			System.out.println("file sent OK");
		} catch (Exception e) {
			System.out.println("Error sending file");
			e.printStackTrace();
		}
	}
	public void getResponse() { 
	
	}
}
