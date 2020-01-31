package TCP;

import java.io.*;
import java.net.*;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
 
        ServerSocket server = null;
        String host = "127.0.0.1";
        int maxServers = 2;
        int port = 32001;
    	for (int i = 0; i < maxServers; i++) {
    		try {
    			server = new ServerSocket(port + i);
                while (true) {
                    Socket client = server.accept();
                    System.out.println("NEW CLIENT CONNECTED TO SERVER");
                    
                    ServerHandler serverSock = new ServerHandler(client);

                    new Thread(serverSock).start();
                    
                    //break;
            	}
    		} catch (IOException e) {
                e.printStackTrace();
            }
    	}        
    }
    
    private static class ServerHandler implements Runnable {
    	
    	private final Socket serverSocket;
    	
    	 public ServerHandler(Socket socket) {
             this.serverSocket = socket;
         }
    	 
    	 @Override
    	 public void run() {

    		 try { 
				 DataInputStream in = new DataInputStream(serverSocket.getInputStream());
    			 String content;
    			 content = in.readUTF();
        		 System.out.println(content);
        		 //in.close();
    			 if(content.equals("CONNECTING")) {
    	    		 System.out.println("HOLA2");
    				 System.out.println("CLIENT MESSAGE: " + content);
    				 DataOutputStream out = new DataOutputStream(serverSocket.getOutputStream());
        			 out.writeUTF("CONNECTION DONE");
    				 
	    			 serverSocket.close();
				 }
    			 
    			 
    		 } catch (IOException e) {
    			 e.printStackTrace();
    		 }
            
             
    	 }
    }
}
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
                
//                PrintWriter out = null;
//                BufferedReader in = null;
//               
//                try {
//                    out = new PrintWriter(client.getOutputStream(), true);	//Capturamos el mensaje enviado en un hilo
//                    in = new BufferedReader(new InputStreamReader(client.getInputStream()));
//                    String line;
//                    while ((line = in.readLine()) != null) {
//                        System.out.printf("Sent from the client: %s\n", line);
//                        out.println(line);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                } finally {
//                    try {
//                        if (out != null) {
//                            out.close();
//                            
//                        }
//                        if (in != null)
//                            in.close();
//                        client.close();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                
//                break;
//                

//        } finally {
//            if (server != null) {
//                try {
//                    server.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
