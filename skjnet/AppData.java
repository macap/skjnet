package skjnet;

import java.io.File;
import java.util.*;
import java.util.logging.*;

public class AppData {
	private static AppData instance = null;
	private int appsCount, appId, portOffset = 10000;
	private String hostname, DIR, logFile;
	public Logger log;

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
	
	public String getLogFileName() {
		return logFile;
	}

	public void loggerInit() {
		logFile = DIR+"log" + appId + ".log";
		
		//remove old logfile if exists:
		File f = new File(logFile);
		if (f.exists()) f.delete();
		
		log = Logger.getLogger("app");
		// log.setLevel(Level.OFF);
		log.setUseParentHandlers(false);
		FileHandler fh;
		try {

			// This block configure the logger with handler and formatter
			fh = new FileHandler(logFile);
			log.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected AppData() {
		fileinfo = new ArrayList<FileInfo>();
		loggerInit();
	}

	public static AppData getInstance() {
		if (instance == null) {
			instance = new AppData();
		}
		return instance;
	}

	public void setup(int appsCount, int appId, String hostname) {
		this.appsCount = appsCount;
		this.appId = appId;
		this.hostname = hostname;

		if (isWindows()) {
			this.DIR = "D:\\TORrent_" + appId + "\\";
		} else {
			this.DIR = "/Users/maciek/torrent" + appId + "/";
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
