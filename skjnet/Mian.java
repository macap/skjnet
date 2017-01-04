package skjnet;

public class Mian {

	public static void main(String[] args) {
		
		
		//APP CONFIGURATION:
		AppData ad = AppData.getInstance();
				
		final int instance = Integer.parseInt(args[0]);
		final int instances = (args.length>1) ? Integer.parseInt(args[1]) : 2;
		final String hostname = (args.length>2) ? args[2] : "127.0.0.1";
		
		ad.setup(instances, instance, hostname);			
		final int serverPort = ad.getPortOffset()+instance;//, clientPort = (instance == 1) ? 10002 : 10001;
		
	
	    final String DIR = ad.getDIR();
		
		
	    //START TCP SERVER
		new Thread( () ->{
			try {
				TCPServer.start(serverPort, DIR);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();
		
		//START TCP CLIENT
		new Thread( () ->{
			try {
				TCPClient.start(DIR);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		
	}

}
