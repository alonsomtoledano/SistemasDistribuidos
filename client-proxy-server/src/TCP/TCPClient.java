package TCP;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
	 
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 32000;
        try (Socket socket = new Socket(host, port)) {
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	String freePort = in.readUTF();
            System.out.println("FREE PORT TO CONNECT: " + freePort);
            
            if(!freePort.equals("NO PORTS AVAIABLE")) {
            	int newPort = Integer.parseInt(freePort);
            	
                try (Socket socketToServer = new Socket(host, newPort)) {
    	            DataOutputStream out = new DataOutputStream(socketToServer.getOutputStream());
    	            out.writeUTF("CONNECTING");
    	            
    	            in = new DataInputStream(socketToServer.getInputStream());
    	            String serverMessage = in.readUTF();
    	            System.out.println("SERVER MESSAGE: " + serverMessage);
                } catch (IOException e) {
                	e.printStackTrace();
                }
            }
        while(true) {
        	
        }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}