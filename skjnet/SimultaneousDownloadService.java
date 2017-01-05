package skjnet;

import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

		ArrayList<Thread> threads = new ArrayList<Thread>();
		ArrayList<String> filesToJoin = new ArrayList<String>();
		
//		ExecutorService executor = Executors.newFixedThreadPool(files.size());
		
		
		//Foreach targetApp send get request (in background) and put data in randomAccessFile
		for (FileInfo f: files) {
		
			int threadIndex = files.indexOf(f);
			int fileIndex = ad.fileinfo.indexOf(f);
			int start = pointer;
			int end = (files.indexOf(f)+1 == files.size()) ? f.size : (pointer+divider);
			String customFileName = f.name+".part"+threadIndex;
			filesToJoin.add(customFileName);
			
			
//			GetRequest gr = new GetRequest(fileIndex, start, end);	
//			gr.customFileName = customFileName;
//			System.out.println(gr);
//			gr.send();
//			
//			Runnable worker = new MyRunnable(fileIndex, start,end, customFileName);
//			executor.execute(worker);
//			
			
			threads.add(
					new Thread( () ->{
						try {
							
							GetRequest gr = new GetRequest(fileIndex, start, end);	
							gr.customFileName = customFileName;
							System.out.println(gr);
							gr.send();
							return;
							
						} catch (Exception e) {
							e.printStackTrace();
						}
					})
					
					);
			
			threads.get(threads.size()-1).start();

			pointer+=divider;
		}
		
//		executor.shutdown();
		
//		while (!executor.isTerminated()) {
			 
//		}
		System.out.println("all threads finished downloading");
	
		//if any part download failed, try with another targetApp

		
		//join all threads
		for (Thread thread : threads) {
		    try {
				thread.join();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//join all parts
		
		

		
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
		
		
//		try
//		{
//		  // Target file:
//		  FileOutputStream outFile = new FileOutputStream(ad.getDIR()+fi.name);
//		  
//		  for (String fname : filesToJoin)
//		  {
//			  File f = new File(ad.getDIR()+fname);
//		      FileInputStream inFile = new FileInputStream(f);
//		      Integer b = null;
//		      while ((b = inFile.read()) != -1)
//		          outFile.write(b);
//		      inFile.close();
//		     f.delete();
//		  }
//		  outFile.close();
//		}
//		catch (Exception e)
//		{
//		  e.printStackTrace();
//		}
		
		//checksum
		File outFile = new File(ad.getDIR()+fi.name);
		if (!MD5.checksum(outFile).equals(fi.hash)) {
			System.out.println(">>>ERROR: hash mismatch");
			//resultFile.delete();
		}
		 
		//Close randomAccessFile
	}
	
	public static class MyRunnable implements Runnable {
		private final int fileIndex, start,end;
		private final String newFilename;
 
		MyRunnable(int fileIndex, int start, int end, String newFilename) {
			this.fileIndex = fileIndex;
			this.start = start;
			this.end = end;
			this.newFilename = newFilename;
		}
 
		@Override
		public void run() {
 
	
			try {
				
				GetRequest gr = new GetRequest(fileIndex, start, end);	
				gr.customFileName = newFilename;
				System.out.println(gr);
				gr.send();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
}
