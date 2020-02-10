package Request;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class Proxy {
	
	static Hashtable portList=new Hashtable();
	
    public static void main(String[] args) {
 
        ServerSocket server = null;
        try {
            server = new ServerSocket(32000);
            server.setReuseAddress(false);
            
            while (true) {
                Socket client = server.accept();
//                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
 
                new Thread(clientSock).start();

                //break;
            }
        }catch (IOException e) {
            e.printStackTrace();
        } /*finally {
            if (server != null) {
                try {
                    server.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }*/
    }
 
    private static class ClientHandler implements Runnable {
 
        private final Socket proxySocket;
 
        public ClientHandler(Socket socket) {
            this.proxySocket = socket;
        }
 
        @Override
        public void run() {
        	portList.put("Triangle", 32001);
            portList.put("Square", 32002);
            portList.put("Circle", 32003);

            Enumeration clave = portList.keys();          
            Enumeration valor = portList.elements();
            
            System.out.println("\n______HASH TABLE______");
            while (clave.hasMoreElements()) {
              System.out.print("SERVER: " + clave.nextElement());
              System.out.println(" PORT: " + valor.nextElement());
            }
            
            try {
            	ObjectInputStream is = new ObjectInputStream(proxySocket.getInputStream());
	        	DataOutputStream out = new DataOutputStream(proxySocket.getOutputStream());
	        	
	        	String clientObject = is.readUTF();
	        	
	        	System.out.println(clientObject.getClass().getName());
	        	
	        	clave = portList.keys(); 
	            valor = portList.elements();
	            
	            while (clave.hasMoreElements()) {
	            	Object claveValue = clave.nextElement();
	            	if(clientObject.getClass().getName().equals(claveValue.toString())) {
	            		out.writeUTF(portList.get(claveValue).toString());
	            	}
	              }
			} catch (IOException e) {
				e.printStackTrace();
			}
            
            
            
            /*finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                    if ((in != null ) );
                        in.close();
                    	clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }*/
        }
    }
}