package content;

import java.io.*;
import java.net.*;

import encryptation.Encryptation;

public class Server {
    public static void main(String[] args) {
    	int port = 32001;
        try {
        	ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket serverAccept = server.accept();
                ServerHandler threadServerAccept = new ServerHandler(serverAccept);
 
                new Thread(threadServerAccept).start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static class ServerHandler implements Runnable {   	 
    	 private final Socket socketToProxy;
    	 
         public ServerHandler(Socket socket) {
             this.socketToProxy = socket;
         }
    	 
    	 @Override
    	 public void run() {
    		 try {
    			DataInputStream in = new DataInputStream(socketToProxy.getInputStream());
 				DataOutputStream out = null;
 	    		String encryptedMessage = in.readUTF();
 	    		System.out.println("RECIBIDO DEL PROXY: " + encryptedMessage);
 	    		
 	    		String decryptedMessage = Encryptation.Decrypt(encryptedMessage);
 	    		out = new DataOutputStream(socketToProxy.getOutputStream());
 	            out.writeUTF(decryptedMessage);
 	            
 	            System.out.println("ENVIADO AL PROXY: " + decryptedMessage);
	            }catch(IOException e) {
	            	e.printStackTrace();
	            } catch (Exception e) {
					e.printStackTrace();
				}
    	 }
    }
}