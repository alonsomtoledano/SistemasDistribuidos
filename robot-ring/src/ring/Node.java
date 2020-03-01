package ring;

import java.io.*;
import java.net.*;

import client.Configuration;
import ring.Robot;

public class Node {

    private static int puertoIzquierda = 5006;
    private static int puertoDerecha   = 5002;
    static String ipDerecha = "172.20.10.6";
    private static int puertoIzquierda2 = 5007;
    private static int puertoDerecha2   = 5003;
    static String ipIzquierda = "172.20.10.9";
    
    static String ip = "172.20.10.14";
    static int puertoClient = 5000;

    public static void main(String[] args) {
    	Robot robot = new Robot(ip);
    	
	    if(args[0].equals("1")) {
	    	puertoIzquierda = puertoClient;
	    }
	    
	    try {
	    	HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoDerecha, ipDerecha, robot);
	    	new Thread(handlerDerecha).start();
	    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda);
	    	new Thread(handlerIzquierda).start();
	    }catch(Exception e) {
	    	e.printStackTrace();
	    }
    }
    
    public static void derecha(String ip, Robot robot) {
    	 while( true ) {
             try {
                 ServerSocket socketIzquierda = new ServerSocket(puertoIzquierda);
                 Socket sIzquierda;
                 while( (sIzquierda=socketIzquierda.accept())!=null )
                 {
                     ObjectInputStream inputIzquierda = new ObjectInputStream(sIzquierda.getInputStream());
                     try {
						Configuration configuration = (Configuration)inputIzquierda.readObject();
						
	                     System.out.println("RECIBIDA LA CONFIGURACION");
                    	 if(configuration.getIp().equals(robot.getIp())) { //ES PARA ESTE ROBOT
                    		 System.out.println("LA CONFIGURACION ES PARA ESTE ROBOT");
    	                     try {
 								Thread.sleep(5000);
	 						} catch (InterruptedException e) {
	 						}
    	                     
                    		 //JOINT ROTATION
                    		 float newJointRotation[] = robot.getJointRotation();
                    		 newJointRotation[configuration.getJointNumber() - 1] += configuration.getJointRotation();
                    		 robot.setJointRotation(newJointRotation);
                    		 
                    		 //JOINT TRANSLATION
                    		 float newJointTranslation[] = robot.getJointTranslation();
                    		 newJointTranslation[configuration.getJointNumber() - 1] += configuration.getJointTranslation();
                    		 robot.setJointTranslation(newJointTranslation);
                    		 
                    		 //STOP
                    		 if(configuration.getStop()) {   	                         
    	                         Socket socketIzquierda1 = new Socket(ipIzquierda,puertoIzquierda2);
	            	             DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda1.getOutputStream() );
	            	             outputIzquierda.writeUTF("ERROR");
	            	             if( outputIzquierda!=null ) outputIzquierda.close();
	            	             if( socketIzquierda1!=null ) socketIzquierda1.close();
	            	             System.exit(0);
                    		 }
                    		 System.out.println("LA CONFIGURACION SE HA REALIZADO");
                    	 }
                    	 else {
                    		 System.out.println("LA CONFIGURACION NO ES PARA ESTE ROBOT, ENVIANDO CONFIGURACION...");
                    		 try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
							}
	                         Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
	                         ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
	                         outputDerecha.writeObject(configuration);
	                         if( inputIzquierda!=null ) inputIzquierda.close();
	                         if( sIzquierda!=null ) sIzquierda.close();
                    	 }
                    	 
                    	//SHOW ROBOT STATUS
                		 System.out.println("//////////_ROBOT STATUS_//////////");
                		 System.out.println("IP: " + robot.getIp());
                		 System.out.print("JOINT ROTATION: ");
                		 for (int i = 0; i < robot.getJointRotation().length; i++) {
                			 System.out.print("[" + robot.getJointRotation()[i] + "] ");
                		 }
                		 System.out.print("\nJOINT TRANSLATION: ");
                		 for (int i = 0; i < robot.getJointTranslation().length; i++) {
                			 System.out.print("[" + robot.getJointTranslation()[i] + "] ");
                		 }
                		 System.out.println("\n//////////////////////////////////");
                		 
					} catch (ClassNotFoundException e1) {
						e1.printStackTrace();
					}
                     
                 }
             }
             catch (IOException ex) {
            	 try {
      	           Socket socketIzquierda = new Socket(ipIzquierda,puertoIzquierda2);
      	           DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda.getOutputStream() );
      	           outputIzquierda.writeUTF("ERROR");
      	           if( outputIzquierda!=null ) outputIzquierda.close();
      	           if( socketIzquierda!=null ) socketIzquierda.close();
      	           System.exit(0);
      	       }
      	       catch(UnknownHostException ex1) {
      	       }
      	       catch(IOException ex1) {            
      	       }
             }
         }
    }
    
    public static void izquierda(String ip) {
   	 while( true ) {
   			try {
                ServerSocket socketDerecha = new ServerSocket(puertoDerecha2);
                Socket sDerecha;
                while( (sDerecha=socketDerecha.accept())!=null )
                {
                    DataInputStream inputDerecha = new DataInputStream( sDerecha.getInputStream() );
                    String mensaje = inputDerecha.readUTF();
                    if( mensaje.compareTo("ERROR")==0 ) {
                    	System.out.println("RECIBIDO ERROR");
                    	Thread.sleep(5000);
                    	System.out.println("ENVIANDO ERROR");
                        Socket socketIzquierda = new Socket(ipIzquierda,puertoIzquierda2);
                        DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda.getOutputStream());
                        outputIzquierda.writeUTF("ERROR");
                        if( outputIzquierda!=null ) outputIzquierda.close();
                        if( socketIzquierda!=null ) socketIzquierda.close();
                        if( inputDerecha!=null ) inputDerecha.close();
                        if( sDerecha!=null ) sDerecha.close();
                        if( socketDerecha!=null ) socketDerecha.close();
                        System.exit(0);
                    }
                }
            }
            catch (IOException ex) {
            } catch (InterruptedException e) {
				e.printStackTrace();
			} 
        }
   } 
    
	private static class HandlerDerecha implements Runnable {

		private int portIzq, portDer;
		private String ip;
		private Robot robot;

		public HandlerDerecha(int portIzq, int portDer, String ip, Robot robot) {
			this.portIzq = portIzq;
			this.portDer = portDer;
			this.ip = ip;
			this.robot = robot;
		}

		@Override
		public void run() {
			System.out.println("Derecha");
			derecha(ip, robot);
		}
	}
	
	private static class HandlerIzquierda implements Runnable {

		private int portIzq, portDer;
		private String ip;

		public HandlerIzquierda(int portIzq, int portDer, String ip) {
			this.portIzq = portIzq;
			this.portDer = portDer;
			this.ip = ip;
		}

		@Override
		public void run() {
			System.out.println("Izquierda");
			izquierda(ip);
		}
	}
}