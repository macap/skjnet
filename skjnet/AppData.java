package skjnet;

import java.util.ArrayList;

public class AppData {
   private static AppData instance = null;
   private int appsCount, appId, portOffset = 10000;
   private String hostname, DIR;
   
   public ArrayList<FileInfo> fileinfo;
   
   public int getAppsCount() {
	   return appsCount;
	}
	public int getAppId() {
		return appId;
	}
	public String getHostname() {
		return hostname;
	}
	public int getPortOffset() {
		return portOffset;
	}
	public String getDIR() {
		return DIR;
	}
   protected AppData() {
	   fileinfo = new ArrayList<FileInfo>();
   }
   public static AppData getInstance() {
      if(instance == null) {
         instance = new AppData();
      }
      return instance;
   }
   public void setup(int appsCount, int appId, String hostname) {
	   this.appsCount = appsCount;
	   this.appId = appId;
	   this.hostname = hostname;

	   if (isWindows()) {
		   this.DIR = "D:\\TORrent_"+appId+"\\";
	   } else {
		   this.DIR = "/Users/maciek/torrent"+appId+"/";
	   }
	   
   }
   
   public void clearFileList() {
	   fileinfo.clear();
   }
   
   public String getSystemName() {
	   return System.getProperty("os.name");
   }
   
   public boolean isWindows() {
       return (getSystemName().indexOf("win") >= 0);
   }

  
}
