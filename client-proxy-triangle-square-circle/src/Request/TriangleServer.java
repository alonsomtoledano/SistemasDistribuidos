package Request;

import java.io.*;
import java.net.*;

import Froms.Triangle;

import java.io.*;
import java.net.*;

public class TriangleServer {
    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 32001;
        
    	ServerHandler serverSock = new ServerHandler(port);
        new Thread(serverSock).start();
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
                     System.out.println("Hola1");
        			 ObjectInputStream is = new ObjectInputStream(client.getInputStream());
        			 
        			 System.out.println("Hola2");
        			 

        			 Triangle triangle = (Triangle)is.readObject();
        			 is.close();
        			 
        			 float area = (triangle.getBase() * triangle.getHeight())/2;
        			 
        			 System.out.println(area);
    				 
	    			 client.close();
    			 }

    		 } catch (IOException e) {
    			 e.printStackTrace();
    		 } catch (ClassNotFoundException e) {
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
