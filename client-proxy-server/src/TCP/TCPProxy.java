package TCP;

import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.Hashtable;

public class TCPProxy {
	
	static Hashtable portList=new Hashtable();
	
    public static void main(String[] args) {
 
        ServerSocket server = null;
        try {
            server = new ServerSocket(32000);
            server.setReuseAddress(false);
            
            while (true) {
                Socket client = server.accept();
                System.out.println("New client connected " + client.getInetAddress().getHostAddress());
                ClientHandler clientSock = new ClientHandler(client);
 
                new Thread(clientSock).start();
                
                
            }
        }catch (IOException e) {
        	System.out.println("Catch 1");
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
 
        private final Socket pollingSocket;
 
        public ClientHandler(Socket socket) {
            this.pollingSocket = socket;
        }
 
        @Override
        public void run() {
//            PrintWriter out = null;
//            BufferedReader in = null;
            
            DataInputStream in;           
            DataOutputStream out;
                      
            
            String host = "127.0.0.1";
            int port = 32001;
            //Socket pollingSocket = null;
            
            
            for(int i= 0; i<3; i++) {   
            	try (Socket pollingSocket = new Socket(host, port)) {
            		out = new DataOutputStream(pollingSocket.getOutputStream());
            		out.writeUTF("POLLING");
                	portList.put(port, true);
                	pollingSocket.close();
	            }catch(IOException e) {
	            	portList.put(port, false);
	            }
            	port++;
            }

            //Enumeration enumeration = portList.elements();
            Enumeration clave = portList.keys();          
            Enumeration valor = portList.elements();
            Object valorValue;
            Object claveValue;
            
            System.out.println("\n______HASH TABLE______");
            while (clave.hasMoreElements()) {
              System.out.print("PORT: " + clave.nextElement());
              System.out.println(" AVIABLE: " + valor.nextElement());
            }
            
            clave = portList.keys(); 
            valor = portList.elements();
            
            
            boolean someFree = false;
            
            while (valor.hasMoreElements()) {
            	valorValue = valor.nextElement();
            	claveValue = clave.nextElement();
            	
            	
            	if(valorValue.equals(true)) {
            		try {
            			someFree = true;
            			System.out.println("\nSENDING FREE PORT TO CLIENT");
            			out = new DataOutputStream(pollingSocket.getOutputStream());
            			String data = claveValue.toString();

            			out.writeUTF(data);
            			
            			try {
            				pollingSocket.close();
            			} catch (IOException e) {
            				e.printStackTrace();
            			}
            			
            			break;
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}
            }
            
            if (!someFree) {
            	try {
            		out = new DataOutputStream(pollingSocket.getOutputStream());
					out.writeUTF("NO PORTS AVAIABLE");
				} catch (IOException e) {
					e.printStackTrace();
				}
            }
            
//            try {
//            	 
//                for(int i= 0; i<10; i++) {
//                	System.out.println(i);
//                	
//                	Socket socket = new Socket(host, port);
//                	
//                	System.out.println(port);
//                	
//                	out = new PrintWriter(socket.getOutputStream(), true);
//                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//                    
//                    //out.flush(); 
//                }  
//                
//            } catch(BindException e) {
//            	e.printStackTrace();
//            }catch(Exception e) {
//            	System.out.print("ERROR AL CONECTAR CON EL SERVIDOR, PUERTO OCUPADO");
//            }
            
            
            
            
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