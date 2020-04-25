package centralServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import log.Log;
import cameraRing.Message;
import clock.ProxyClock;

public class centralServer {
	
	//PORTS
    static int puertoCameraRing = 6000;
    
    //LOG VARIBLES
    static String logPath = "./src/cameraRing/logs/centralServer.log";
    static String INFO = "info";
    static String ERROR = "error";
    static String className;
    static long time;
    
    //LOCKS
    static ReentrantLock logLock = new ReentrantLock();
    
    static HandlerCameraRing handlerCameraRing = new HandlerCameraRing();

	public static void main(String[] args) {
		//LOG DATA
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		
		//THREADS
    	new Thread(handlerCameraRing).start();
	}
	
	//THREAD FUNCTIONS
    public static void cameraRing() {	
		time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "CameraRing thread started", className, time);
		}finally {
			logLock.unlock();
		}
    	
    	while(true) {
    		try {
    			ServerSocket socketCameraRing = new ServerSocket(puertoCameraRing);
    			Socket sCameraRing;
    			    			
    			while((sCameraRing = socketCameraRing.accept()) != null) {
    				ObjectInputStream inputCameraRing = new ObjectInputStream(sCameraRing.getInputStream());
					
					time = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, logPath, "Message recieved", className, time);
					}finally {
						logLock.unlock();
					}
    				
    				try {
    					Message message = (Message)inputCameraRing.readObject();
    					
    					Thread.sleep(2000);
            			System.out.println("\n");
            			System.out.println("***********************************");
            			System.out.println("MESSAGE RECEIVED");
            			System.out.println("matriculasInLog: " + message.getMatriculasInLog());
    					System.out.println("matriculasOutLog: " + message.getMatriculasOutLog());
    					System.out.println("***********************************");
    					
					} catch (ClassNotFoundException | InterruptedException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
			}
    	}
    }
	
	//THREAD HANDLERS
	private static class HandlerCameraRing implements Runnable {
		@Override
		public void run() {
			cameraRing();
		}
	}
}
