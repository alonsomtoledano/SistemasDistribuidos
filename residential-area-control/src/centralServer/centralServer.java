package centralServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import cameraRing.Message;

public class centralServer {

	public static void main(String[] args) {
		//PORTS
	    int puertoCameraRing = 6000;
	    
	    HandlerCameraRing handlerCameraRing = new HandlerCameraRing(puertoCameraRing);
    	new Thread(handlerCameraRing).start();
	}
	
	//THREAD FUNCTIONS
    public static void cameraRing(int puertoCameraRing) {
    	while(true) {
    		try {
    			ServerSocket socketCameraRing = new ServerSocket(puertoCameraRing);
    			Socket sCameraRing;
    			    			
    			while((sCameraRing = socketCameraRing.accept()) != null) {
    				ObjectInputStream inputCameraRing = new ObjectInputStream(sCameraRing.getInputStream());
    				try {
    					Message message = (Message)inputCameraRing.readObject();    					
    					System.out.println("MENSAJE RECIBIDO DEL CAMERA RING: " + message.getContent());
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
		
		public HandlerCameraRing(int puertoCameraRing) {
			this.puertoCameraRing = puertoCameraRing;
		}
		
		public void run() {
			System.out.println("Server");
			cameraRing(puertoCameraRing);
		}
	}
}
