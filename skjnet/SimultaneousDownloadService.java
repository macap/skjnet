package skjnet;

import java.io.*;
import java.util.ArrayList;

public class SimultaneousDownloadService {
	ArrayList<FileInfo> files = new ArrayList<FileInfo>();
	AppData ad = null;
	
	public SimultaneousDownloadService(int fileIndex) {
		//1. select files
		ad = AppData.getInstance();	
		FileInfo fi = ad.fileinfo.get(fileIndex);
		
		files.add(fi);
		
		for (FileInfo f : ad.fileinfo) 
			if (f.hash.equals(fi.hash) && f.appId!=fi.appId && f.name.equals(fi.name) ) 
				files.add(f);
			
		
			
	}
	
	public void send() {
		if (files.isEmpty()) return;
		
		System.out.println("Sending "+files.size()+" requests ");
		
		FileInfo fi = files.get(0);
		//Prepare ranges to download for each targetApp
		int requestsCount = files.size();
		
		int divider = fi.size/requestsCount;
		int pointer = 0;
		if (divider<8192) divider = 8192; //min buffer size

		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<String> filesToJoin = new ArrayList<String>();
		
		//Foreach targetApp send get request (in background) and put data in randomAccessFile
		for (FileInfo f: files) {
		
			int threadIndex = files.indexOf(f);
			int fileIndex = ad.fileinfo.indexOf(f);
			int start = pointer;
			int end = (files.indexOf(f)+1 == files.size()) ? f.size : (pointer+divider);
			String customFileName = f.name+".part"+threadIndex;
			filesToJoin.add(customFileName);
			
			threads.add(
					new Thread( () ->{
						try {
							
							GetRequest gr = new GetRequest(fileIndex, start, end);	
							gr.customFileName = customFileName;
							gr.send();
							return;
						} catch (Exception e) {
					
						}
					})
					
					);

			threads.get(threads.size()-1).start();
			pointer+=divider;
		}
		
		//join all threads
		for (Thread thread : threads) {
		    try {
				thread.join();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			FileOutputStream fos = new FileOutputStream(ad.getDIR()+fi.name);
			
			for (String fname : filesToJoin) {
				File t = new File(ad.getDIR()+fname);
				try {
				InputStream sis = new FileInputStream(t);		
				int count;
				byte[] buffer = new byte[8192]; // or 4096, or more
				while ((count = sis.read(buffer)) > 0)
				{
				  fos.write(buffer, 0, count);
				}
				
				fos.flush();
				sis.close();	
				t.delete();
				} catch( Exception e) {
					e.printStackTrace();
				}
			}
			
			fos.close();
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	
		//checksum
		File outFile = new File(ad.getDIR()+fi.name);
		if (!MD5.checksum(outFile).equals(fi.hash)) {
			System.out.println("ERROR: file hash mismatch. Try again");
			//resultFile.delete();
		} else {
			System.out.println("File ok:" + fi.name);
		}
		 
	}
	

	
}
