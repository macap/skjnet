package skjnet;

public class Mian {

	public static void main(String[] args) {
		
		if (args.length<1) {
			System.out.println("Missing required argument: instanceId");
			System.out.println("Arguments: instanceId <instancesCount=2> <hostname=127.0.0.1>");
			return;
		}
		
		
		
		//APP CONFIGURATION:
		AppData ad = AppData.getInstance();
				
		final int instance = Integer.parseInt(args[0]);
		final int instances = (args.length>1) ? Integer.parseInt(args[1]) : 2;
		final String hostname = (args.length>2) ? args[2] : "127.0.0.1";
		
		ad.setup(instances, instance, hostname);			
		final int serverPort = ad.getPortOffset()+instance;//, clientPort = (instance == 1) ? 10002 : 10001;
	
		
	
	    final String DIR = ad.getDIR();
		
	    

		System.out.println(
				"SKJNET"+'\n'
			   +"Instance ID:"+instance+'\t'+"Host:"+hostname+'\t'+"Listening on port: "+serverPort+'\n'
			   +"Instance DIR:"+DIR+'\t'+"All instances:"+instances
			   +"\nReport: http://localhost:"+(serverPort+1000)
				);
		
		System.out.println(
				"\nCommands:\n "
				+ "list - list files from all available instances \n "
				+ "get <fileId> - download file from one instance or continue download if file already exists \n "
				+ "gets <fileId> - download files from multiple instances simultaneously \n "
				+ "getb <fileId> - gets first few bytes from a file and breaks connection - for testing \"download continue\" feature \n "
				+ "push <instanceId> <fileName> - push file from instance directory to remote directory - instanceId \n\n"
				);
	    
		
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
		
		
		//Start HTPP SERVER
		new Thread( () ->{
			try {
				HttpServer.start(serverPort+1000,DIR);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
		
		
	}

}
