package skjnet;

public class Mian {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			
			if ("s".equalsIgnoreCase(args[0])) {
				TCPServer.start(Integer.valueOf(args[1]));
			} else {
				TCPClient.start(Integer.valueOf(args[1]));
			}
			
			//
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
