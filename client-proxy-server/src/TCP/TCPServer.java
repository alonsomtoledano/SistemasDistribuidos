package TCP;

import java.io.*;
import java.net.*;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int maxServers = 2;
        int port = 32001;
        
        for (int i = 0; i < maxServers; i++) {
        	ServerHandler serverSock = new ServerHandler(port + i);
            new Thread(serverSock).start();
        }
    }
    
    private static class ServerHandler implements Runnable {
    	
    	//private final Socket serverSocket;
    	private final int port;
    	
    	 public ServerHandler(int port) {
             this.port = port;
         }
    	 
    	 @Override
    	 public void run() {
    		 ServerSocket server = null;
			try {
				server = new ServerSocket(port);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
    		 try {  
    			 while(true) {
    	    	     
                     Socket client = server.accept();
                     System.out.println("NEW CLIENT CONNECTED TO SERVER");
                     
        			 
    				 DataInputStream in = new DataInputStream(client.getInputStream());
        			 String content;
        			 content = in.readUTF();
            		 System.out.println(content);
            		 //in.close();
        			 if(content.equals("CONNECTING")) {
        	    		 System.out.println("HOLA2");
        				 System.out.println("CLIENT MESSAGE: " + content);
        				 DataOutputStream out = new DataOutputStream(client.getOutputStream());
            			 out.writeUTF("CONNECTION DONE");
        				 
    	    			 client.close();
    				 }
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
