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
    static String ipClient = "172.20.10.14";
    
    static boolean masterNode = false;
    static int auxPuertoIzquierda = puertoIzquierda;
    static boolean done = false;
    static boolean allDone = false;

    public static void main(String[] args) {
    	Robot robot = new Robot(ip);
    	
	    if(args[0].equals("1")) {
	    	masterNode = true;
	    	puertoIzquierda = puertoClient;
	    }
	    
    	HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoDerecha, ipDerecha, robot);
    	new Thread(handlerDerecha).start();
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda);
    	new Thread(handlerIzquierda).start();
    }
    
    public static void derecha(String ip, Robot robot) {
    	int a = 1;
    	if(masterNode) a = 2;
    	for(int k = 0; k < a; k++) {
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
	                     
	                     Thread.sleep(3000);
	                     
	                     String configurationRobot[][] = configuration.getConfigurationRobot();
	                     boolean doneConfiguration[] = configuration.getDone();
	                     if(done) {
	                    	 for(int i = 0; i < configurationRobot.length; i++) {
	                          	 if(configurationRobot[i][0].equals(robot.getIp())) {
	                          		 doneConfiguration[i] = true;
	                          		 configuration.setDone(doneConfiguration);
	                          		 break;
	                          	 }
	                    	 }
	                     }
	                     
	                     for (int l = 0; l < configuration.getDone().length; l++) {
	                    	 if (configuration.getDone()[l] == true) {
	                    		 allDone = true;
	                    	 } else {
	                    		 allDone = false;
	                    		 break;
	                    	 }
	                     }
	                     
	                     if(!allDone) {
	                    	 System.out.println("ENVIANDO CONFIGURACION");
	                    	 Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
	                         ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
	                         outputDerecha.writeObject(configuration);
	                         
			                 if(!done) {    
		                         //REALIZAR LA CONFIGURACION
		                         HandlerOperation handlerOperation = new HandlerOperation(configuration, robot);
		                     	 new Thread(handlerOperation).start();
		                     }
	                     } else {
	                    	 System.out.println("ALL DONE");
	                    	 Socket socketIzquierda2 = new Socket(ipIzquierda,puertoIzquierda2);
	            	           DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda2.getOutputStream() );
	            	           outputIzquierda.writeUTF("ALL DONE");
	            	           if( outputIzquierda!=null ) outputIzquierda.close();
	            	           if( socketIzquierda!=null ) socketIzquierda.close();
	            	           System.exit(0);
	                     }
                    	 
					} catch (ClassNotFoundException | InterruptedException e1) {
					}  
                    if(masterNode) {
                    	break;
                    }
                 }
             }
             catch (IOException ex) {
            	 try {
      	           Socket socketIzquierda = new Socket(ipIzquierda,puertoIzquierda2);
      	           System.out.println("ERROR\nENVIANDO ERROR");
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
             if(masterNode) {
                 puertoIzquierda=auxPuertoIzquierda;
                 break;
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
                    	try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);){
                    		DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda.getOutputStream());
                            outputIzquierda.writeUTF("ERROR");
                            System.exit(0);
                    	}catch(Exception e) {
	           				 System.exit(0);
	           			}
                    } else if (mensaje.compareTo("ALL DONE")==0) {
                    	System.out.println("ALL DONE");
                    	Thread.sleep(5000);
                    	try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);){
                    		DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda.getOutputStream());
                            outputIzquierda.writeUTF("ALL DONE");
                            System.exit(0);
                    	}catch(Exception e) {
	           				 System.exit(0);
	           			}
                    }
                }
            }
            catch (IOException ex) {
            } catch (InterruptedException e) {
				e.printStackTrace();
			} 
        }
    }
    
    public static void operation(Configuration configuration, Robot robot) {
		String configurationRobot[][] = configuration.getConfigurationRobot();
		
        for(int i = 0; i < configurationRobot.length; i++) {
          	 if(configurationRobot[i][0].equals(robot.getIp())) {
           		 System.out.println("REALIZANDO LA CONFIGURACION");
           		 
           		 //JOINT ROTATION
           		 float newRotation[] = robot.getJointRotation();
           		 newRotation[Integer.parseInt(configurationRobot[i][1]) - 1] += Float.parseFloat(configurationRobot[i][2]);
           		 robot.setJointRotation(newRotation);
           		 
           		 //JOINT TRANSLATION
           		 float newTranslation[] = robot.getJointTranslation();
           		 newTranslation[Integer.parseInt(configurationRobot[i][1]) - 1] += Float.parseFloat(configurationRobot[i][3]);
           		 robot.setJointTranslation(newTranslation);
           		 
           		 if(configuration.getIpToStop().equals(robot.getIp())) {
           			 System.out.println("SHUTTING DOWN");
           			 try(Socket socketIzquierda1 = new Socket(ipIzquierda, puertoIzquierda2);){
           				 DataOutputStream outputIzquierda = new DataOutputStream( socketIzquierda1.getOutputStream() );
           	             outputIzquierda.writeUTF("ERROR");
           	             System.exit(0);
           			 }catch(Exception e) {
           				 System.exit(0);
           			 }
           		 }
           		 
           		 try {
    				Thread.sleep(5000);
    			} catch (InterruptedException e) {
    				e.printStackTrace();
    			}
           		 
           		 done = true;
           		 System.out.println("LA CONFIGURACION SE HA REALIZADO");
           		 
                try {
    				Thread.sleep(5000);
    			} catch (InterruptedException e1) {
    				e1.printStackTrace();
    			}
	       		 //SHOW ROBOT STATUS
	       		 System.out.println("////////////////////___ROBOT STATUS___////////////////////");
	       		 System.out.println("IP: " + robot.getIp());
	       		 System.out.print("JOINT ROTATION: ");
	       		 for (int j = 0; j < robot.getJointRotation().length; j++) {
	       			 System.out.print("[" + robot.getJointRotation()[j] + "] ");
	       		 }
	       		 System.out.print("\nJOINT TRANSLATION: ");
	       		 for (int j = 0; j < robot.getJointTranslation().length; j++) {
	       			 System.out.print("[" + robot.getJointTranslation()[j] + "] ");
	       		 }
	       		 System.out.println("\n////////////////////////////////////////////////////////");
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
	
	private static class HandlerOperation implements Runnable {
		
		private Configuration configuration;
		private Robot robot;
		
		public HandlerOperation(Configuration configuration, Robot robot) {
			this.configuration = configuration;
			this.robot = robot;
		}
		
		@Override
		public void run() {
			operation(configuration, robot);
		}
	}
}