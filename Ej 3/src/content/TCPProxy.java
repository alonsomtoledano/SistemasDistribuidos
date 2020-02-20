package ejercicioClase.Proxy;

import java.io.*;
import java.net.*;

public class TCPProxy {
    public static void main(String[] args) {
    	int port = 32000;
        try {
        	ServerSocket server = new ServerSocket(port);
            while (true) {
                Socket serverAccept = server.accept();
                ClientHandler threadServerAccept = new ClientHandler(serverAccept);
 
                new Thread(threadServerAccept).start();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }
 
    private static class ClientHandler implements Runnable {
        private final Socket socketToClient;
 
        public ClientHandler(Socket socket) {
            this.socketToClient = socket;
        }
 
        @Override
        public void run() {
			try {
				DataInputStream in = new DataInputStream(socketToClient.getInputStream());
				DataOutputStream out = null;
	    		String encryptedMessage = in.readUTF();
	    		System.out.println("RECIBIDO DEL CLIENT: " + encryptedMessage);
	    		
	    		/////////////////////////
	    		// Socket para enviar mensaje encritado al servidor
	    		
	    		String serverHost = "127.0.0.1";
	            int serverPort = 32001;
	            String decryptedMessage = null;
	    		
	    		try (Socket socketToServer = new Socket(serverHost, serverPort)) {
            		out = new DataOutputStream(socketToServer.getOutputStream());
            		out.writeUTF(encryptedMessage);
            		System.out.println("ENVIADO AL SERVER: " + encryptedMessage);
            		
            		in = new DataInputStream(socketToServer.getInputStream());
                    decryptedMessage = in.readUTF();
                    System.out.println("RECIBIDO DEL SERVER: " + decryptedMessage);
            		socketToServer.close();
            		
                	out.close();
	            }catch(IOException e) {
	            	//Si no se ha podido conectar, aqui se tiene que gestionar
	            	e.printStackTrace();
	            }
	    		
	    		/////////////////////////
	    		
	    		out = new DataOutputStream(socketToClient.getOutputStream());
	            out.writeUTF(decryptedMessage);	
	            System.out.println("ENVIADO AL CLIENT: " + decryptedMessage);
			} catch (IOException e) {
				e.printStackTrace();
			}
        }
    }
}