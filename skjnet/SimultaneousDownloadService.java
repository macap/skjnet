package skjnet;

import java.util.ArrayList;

public class SimultaneousDownloadService {
	ArrayList<FileInfo> files = new ArrayList<FileInfo>();
	
	
	public SimultaneousDownloadService(int fileIndex) {
		//1. select files
		AppData ad = AppData.getInstance();	
		FileInfo fi = ad.fileinfo.get(fileIndex);
		
		files.add(fi);
		
		//search for other file locations 
		for (FileInfo f : ad.fileinfo) 
			if (f.hash.equals(fi.hash) && f.appId!=fi.appId && f.name.equals(fi.name) )  //TODO: support different filenames
				files.add(f);
			
		
			
	}
	
	public SimultaneousDownloadService(int fileIndex, int[] targetAppsIds) { 
		this(fileIndex);//TODO: specify targetAppsIds
	}
	
	public void send() {
		if (files.isEmpty()) return;
		
		
		//TODO: remove file if already exists!
		
		System.out.println("Will create "+files.size()+" requests ");
		
		AppData ad = AppData.getInstance();	
		
		FileInfo fi = files.get(0);
		//Prepare ranges to download for each targetApp
		int requestsCount = files.size();
		
		int divider = fi.size/requestsCount;
		int pointer = 0;
		
		if (divider<8192) divider = 8192; //min buffer size
		

		//Create randomAccessFile with file size (and temp extension)
		
		//Foreach targetApp send get request (in background) and put data in randomAccessFile
		for (FileInfo f: files) {
		
			int end = (files.indexOf(f)+1 == files.size()) ? f.size : (pointer+divider);
		
			GetRequest gr = new GetRequest(ad.fileinfo.indexOf(f), pointer, end, true);	
			gr.customFileName = f.name+".part"+files.indexOf(f);
			gr.send();
		
//			System.out.println("WILL GET "+pointer+"-"+end+" from S"+f.appId);
//			System.out.println(gr);
		
			
			pointer+=divider;
		}
	
		//if any part download failed, try with another targetApp
		
		//Close randomAccessFile
	}
}
