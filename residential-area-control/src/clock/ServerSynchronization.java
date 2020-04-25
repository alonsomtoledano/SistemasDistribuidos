package clock;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class ServerSynchronization {
	//PORTS
	static int serverSynchronizationPort = 5400;
	//LOG VARIABLES

	static String INFO = "info";
	static String ERROR = "error";
	static String className;

	//TIME
	static long time;
	
	public static void main(String[] args) throws IOException, InterruptedException {
    	//CODE
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
   
    	System.out.println("SERVER SINCHRONIZATION LISTENING TO EVENTS.....");
		try {
			ServerSocket server = new ServerSocket(serverSynchronizationPort);
			while (true) {
				Socket clientAccept = server.accept();
				ServerHandler threadServerAccept = new ServerHandler(clientAccept);

				new Thread(threadServerAccept).start();

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
		
	private static class ServerHandler implements Runnable {
		private final Socket socketFromClient;
		
		public ServerHandler(Socket socketFromClient) {
			this.socketFromClient = socketFromClient;
		}
		
		@Override
		public void run() {		
			//THREAD FUNCTIONS
			try {
				ObjectOutputStream oos = new  ObjectOutputStream(socketFromClient.getOutputStream());
	            long serverTime = System.currentTimeMillis();
	            
	            Random r = new Random();
	            
	            int gap = 2;
	            // x 1000 to convert into secods
	            Thread.sleep(gap * 1000);
	            Message messageServerTime = new Message(serverTime);
	            Message messageGap = new Message(gap);
	            oos.writeObject(messageServerTime);
	            oos.writeObject(messageGap);
	            
	    	}catch(IOException | InterruptedException e) {
	    		e.printStackTrace();
	    	} 
		}	
	}
}