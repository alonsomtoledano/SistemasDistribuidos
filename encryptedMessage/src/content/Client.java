package content;

import java.io.*;
import java.net.*;
import java.util.*;

import encryptation.Encryptation;

public class Client {
	 
    public static void main(String[] args) {
        String proxyHost = "127.0.0.1";
        int proxyPort = 32000;
        
        Scanner u = new Scanner(System.in);
        
        System.out.print("MESSAGE TO SEND: ");
        String message =  u.nextLine();
        String encryptedMessage = Encryptation.Encrypt(message);
        
        try (Socket socketToProxy = new Socket(proxyHost, proxyPort)) {
            DataOutputStream out = new DataOutputStream(socketToProxy.getOutputStream());
            out.writeUTF(encryptedMessage);
            System.out.println("ENVIADO AL PROXY: " + encryptedMessage);
            
            DataInputStream in = new DataInputStream(socketToProxy.getInputStream());
            String decryptedMessage = in.readUTF();
            System.out.println("RECIBIDO DEL PROXY: " + decryptedMessage);
        } catch (IOException e) {
        	e.printStackTrace();
        }        
    }
}