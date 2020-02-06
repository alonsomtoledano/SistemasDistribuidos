package Request;

import java.io.*;
import java.net.*;
import java.util.*;

public class Client {
	 
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 32000;
        try (Socket socket = new Socket(host, port)) {
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	
        	out.writeUTF("SQUARE");
        	String freePort = in.readUTF();
            System.out.println("PORT TO CONNECT: " + freePort);
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}