package cameraRing;

import java.io.*;
import java.net.*;

public class Node {

	public static void main(String[] args) {
		//PORTS
	    int puertoIzquierda = 5002;
	    int puertoDerecha   = 5000;
	    int puertoIzquierda2 = 5005;
	    int puertoDerecha2   = 5003;
	    
	    //IP
	    String ipDerecha = "localhost";
	    String ipIzquierda = "localhost";
	    
	    //CONTROL VARIABLES
	    boolean masterNode = true;
	    int nodeNumber = 1;
		
	    
		HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoDerecha, ipDerecha, masterNode, nodeNumber);
    	new Thread(handlerDerecha).start();
    	
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda, nodeNumber);
    	new Thread(handlerIzquierda).start();
	}
	
	//THREAD FUNCTIONS
    public static void derecha(int puertoIzquierda, int puertoDerecha, String ipDerecha, boolean masterNode) {
    	boolean firstTime = true;
    	
    	while(true) {
    		try {
    			ServerSocket socketIzquierda = new ServerSocket(puertoIzquierda);
    			Socket sIzquierda;
    			
    			if(masterNode && firstTime) {
        			Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
        			ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
        			
    				System.out.println("ENVIANDO PRIMER MENSAJE");
    				Message message = new Message("Contenido");
    				
    				try {
						Thread.sleep(3000);
					} catch (InterruptedException e) {
					}
    				
    				outputDerecha.writeObject(message);
    				System.out.println("PRIMER MENSAJE ENVIADO");
    			}
    			
    			while((sIzquierda = socketIzquierda.accept()) != null) {
    				ObjectInputStream inputIzquierda = new ObjectInputStream(sIzquierda.getInputStream());
    				try {
    					Message message = (Message)inputIzquierda.readObject();
    					
    					Thread.sleep(3000);
    					
    					System.out.println("MENSAJE RECIBIDO: " + message.getContent());
    					
    					Thread.sleep(3000);
    					
    					System.out.println("ENVIANDO MENSAJE");
    					Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
    					ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
    					outputDerecha.writeObject(message);
    					
    					Thread.sleep(3000);
    					
    					System.out.println("MENSAJE ENVIADO");
					} catch (ClassNotFoundException | InterruptedException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
			}
    	}
    }
    
    public static void izquierda(int puertoIzquierda2, int puertoDerecha2, String ipIzquierda) {
    	
    }	
	
	//THREAD HANDLERS
	private static class HandlerDerecha implements Runnable {
	
		private int puertoIzquierda, puertoDerecha;
		private String ipDerecha;
		private boolean masterNode;
		private int nodeNumber;
	
		public HandlerDerecha(int puertoIzquierda, int puertoDerecha, String ipDerecha, boolean masterNode, int nodeNumber) {
			this.puertoIzquierda = puertoIzquierda;
			this.puertoDerecha = puertoDerecha;
			this.ipDerecha = ipDerecha;
			this.masterNode = masterNode;
			this.nodeNumber = nodeNumber;
		}
	
		@Override
		public void run() {
			System.out.println(nodeNumber + " Derecha");
			derecha(puertoIzquierda, puertoDerecha, ipDerecha, masterNode);
		}
	}
	
	private static class HandlerIzquierda implements Runnable {
		
		private int puertoIzquierda2, puertoDerecha2;
		private String ipIzquierda;
		private int nodeNumber;
	
		public HandlerIzquierda(int puertoIzquierda2, int puertoDerecha2, String ipIzquierda, int nodeNumber) {
			this.puertoIzquierda2 = puertoIzquierda2;
			this.puertoDerecha2 = puertoDerecha2;
			this.ipIzquierda = ipIzquierda;
			this.nodeNumber = nodeNumber;
		}
	
		@Override
		public void run() {
			System.out.println(nodeNumber + " Izquierda");
			izquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda);
		}
	}
}