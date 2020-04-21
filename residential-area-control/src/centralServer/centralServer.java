package centralServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import cameraRing.Log;
import cameraRing.Message;

public class centralServer {

	public static void main(String[] args) {
		//PORTS
	    int puertoCameraRing = 6000;
	    int puertoAdministration = 6001;
	    
	    //LOG VARIBLES
	    String logPath = "./src/cameraRing/node.log";
	    
	    //LOCKS
	    ReentrantLock logLock = new ReentrantLock();
	    
	    HandlerCameraRing handlerCameraRing = new HandlerCameraRing(puertoCameraRing, logPath, logLock);
    	new Thread(handlerCameraRing).start();

    	HandlerAdministration handlerAdministration = new HandlerAdministration(puertoAdministration, logPath, logLock);
    	new Thread(handlerAdministration).start();
	}
	
	//THREAD FUNCTIONS
    public static void cameraRing(int puertoCameraRing, String logPath, ReentrantLock logLock) {
    	logLock.lock();
		try {
			Log.log("info", logPath, "CameraRing thread started");
		} finally {
			logLock.unlock();
		}
    	
    	while(true) {
    		try {
    			ServerSocket socketCameraRing = new ServerSocket(puertoCameraRing);
    			Socket sCameraRing;
    			    			
    			while((sCameraRing = socketCameraRing.accept()) != null) {
    				ObjectInputStream inputCameraRing = new ObjectInputStream(sCameraRing.getInputStream());
    				
    				logLock.lock();
					try {
    					try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Log.log("info", logPath, "Message recieved");
					} finally {
						logLock.unlock();
					}
    				
    				try {
    					Message message = (Message)inputCameraRing.readObject();
    					System.out.println("matriculasInLog: " + message.getMatriculasInLog());
    					System.out.println("matriculasOutLog: " + message.getMatriculasOutLog());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
			}
    	}
    }
    
    public static void administration(int puertoAdministration, String logPath, ReentrantLock logLock) {
    	logLock.lock();
		try {
			Log.log("info", logPath, "Administration thread started");
		} finally {
			logLock.unlock();
		}
    	
    	while(true) {
    		try {
    			ServerSocket socketAdministration = new ServerSocket(puertoAdministration);
    			Socket sAdministration;
    			    			
    			while((sAdministration = socketAdministration.accept()) != null) {
    				ObjectInputStream inputAdministration = new ObjectInputStream(sAdministration.getInputStream());
    				
    				logLock.lock();
					try {
    					try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						Log.log("info", logPath, "Message recieved");
					} finally {
						logLock.unlock();
					}
    				
    				try {
    					Message message = (Message)inputAdministration.readObject();
    					System.out.println("matriculasInLog: " + message.getMatriculasInLog());
    					System.out.println("matriculasOutLog: " + message.getMatriculasOutLog());
					} catch (ClassNotFoundException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
			}
    	}
    }
	
	//THREAD HANDLERS
	private static class HandlerCameraRing implements Runnable {
		
		private int puertoCameraRing;
		String logPath;
		ReentrantLock logLock;
		
		public HandlerCameraRing(int puertoCameraRing, String logPath, ReentrantLock logLock) {
			this.puertoCameraRing = puertoCameraRing;
			this.logPath = logPath;
			this.logLock = logLock;
		}
		
		public void run() {
			cameraRing(puertoCameraRing, logPath, logLock);
		}
	}
	
	private static class HandlerAdministration implements Runnable {
		
		private int puertoAdministration;
		String logPath;
		ReentrantLock logLock;
		
		public HandlerAdministration(int puertoAdministration, String logPath, ReentrantLock logLock) {
			this.puertoAdministration = puertoAdministration;
			this.logPath = logPath;
			this.logLock = logLock;
		}
		
		public void run() {
			administration(puertoAdministration, logPath, logLock);
		}
	}
}
