package content;

import java.io.*;
import java.net.*;
import java.util.*;

public class TCPClient {
	 
    public static void main(String[] args) {
        String proxyHost = "127.0.0.1";
        int proxyPort = 32000;
        String encryptedMessage = "HOLA";
        
        try (Socket socketToProxy = new Socket(proxyHost, proxyPort)) {
            DataOutputStream out = new DataOutputStream(socketToProxy.getOutputStream());
            out.writeUTF("HOLA");
            System.out.println("ENVIADO AL PROXY: " + encryptedMessage);
            
            DataInputStream in = new DataInputStream(socketToProxy.getInputStream());
            String decryptedMessage = in.readUTF();
            System.out.println("RECIBIDO DEL PROXY: " + decryptedMessage);
        } catch (IOException e) {
        	e.printStackTrace();
        }        
    }
}