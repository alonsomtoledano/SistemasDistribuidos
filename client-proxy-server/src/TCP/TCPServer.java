package TCP;

import java.io.*;
import java.net.*;

import java.io.*;
import java.net.*;

public class TCPServer {
    public static void main(String[] args) {
 
        ServerSocket server = null;
        ServerSocket server2 = null;
        String host = "127.0.0.1";
        try {
        	
            server = new ServerSocket(32001);
            server.setReuseAddress(false);
            

            //server2 = new ServerSocket(32002);
            //server2.setReuseAddress(false);

            while (true) {
            	
                Socket client = server.accept();
                System.out.println("NEW CLIENT CONNECTED TO SERVER PORT: 32001");
                DataInputStream in = new DataInputStream(client.getInputStream());
                String content = in.readUTF();
                
                DataOutputStream out = new DataOutputStream(client.getOutputStream());
                out.writeUTF("CONNECTION DONE");
                
                if(content.equals("CONECTING")) {
                	System.out.println("CLIENT MESSAGE: " + content);
                	
                	//in.close();
                }
                client.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
