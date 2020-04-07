package cameraRing;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;

public class Node {

	public static void main(String[] args) {
		//PORTS
	    int puertoIzquierda = 6003;
	    int puertoDerecha = 6001;
	    int puertoIzquierda2 = 6006;
	    int puertoDerecha2 = 6004;
	    int puertoCentralServer = 6000;
	    
	    //IP
	    String ipIzquierda = "localhost";
	    String ipDerecha = "localhost";
	    String ipCentralServer = "localhost";
	    
	    //CONTROL VARIABLES
	    boolean masterNode = true;
	    int nodeNumber = 1;
	    boolean inOut = true; //TRUE = IN, FALSE = OUT
		
		HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoDerecha, puertoCentralServer, ipDerecha, ipCentralServer, masterNode, nodeNumber);
    	new Thread(handlerDerecha).start();
    	
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda, nodeNumber);
    	new Thread(handlerIzquierda).start();
    	
    	if (masterNode) {
        	HandlerServerCorba handlerServerCorba = new HandlerServerCorba();
        	new Thread(handlerServerCorba).start();
    	}
    	
    	HandlerClientCorba handlerClientCorba = new HandlerClientCorba(inOut);
    	new Thread(handlerClientCorba).start();
	}
	
	//THREAD FUNCTIONS
    public static void derecha(int puertoIzquierda, int puertoDerecha, int puertoCentralServer, String ipDerecha, String ipCentralServer, boolean masterNode) {
    	boolean firstTime = true;
    	
    	while (true) {
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
    					
    					if(masterNode) {
    						Socket socketCentralServer = new Socket(ipCentralServer, puertoCentralServer);
        					ObjectOutputStream outputCentralServer = new ObjectOutputStream (socketCentralServer.getOutputStream());
        					outputCentralServer.writeObject(message);
        					System.out.println("MENSAJE ENVIADO AL SERVIDOR");
    					}
    					
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
    
    public static void serverCorba() {
		try {
			Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/serverCorba.bat");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void clientCorba(boolean inOut) {
    	String folderRoute = inOut ? "./src/cameraRing/detectionIn/" : "./src/cameraRing/detectionOut/";
    	
    	File folder = new File(folderRoute);
    	Path path = Paths.get(folderRoute);
    	
    	while (true) {
        	try {
        		WatchService watcher = path.getFileSystem().newWatchService();
        		path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        		WatchKey watchKey = watcher.take();
        		List<WatchEvent<?>> events = watchKey.pollEvents();
        		for (WatchEvent event : events) {
        			if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
        				
        				String[] list = folder.list();
        				
        		    	for (int i = 0; i < list.length; i++) {
        		    		if (list[i].endsWith(".jpg")) {        	
        		    			
        		    			try {
        		    				Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/clientCorba.bat");
        		    				
        		    				//LEER
        		    				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        		    				
        		    				String s = null;
        		    				while ((s = stdInput.readLine()) != null) {
        		    				    System.out.println(s);
        		    				}
        						} catch (Exception e) {
        						}
        		    		}
        		    	}
        		    	
        			}
        		}
    		} catch (Exception e) {
    		}
    	}
    	
//		try {
//			Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/clientCorba.bat");
//			
//			//LEER
//			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
//			
//			String s = null;
//			while ((s = stdInput.readLine()) != null) {
//			    System.out.println(s);
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
    }
    
    public static void matriculasDetector(boolean inOut) {
    	String folderRoute = inOut ? "./src/cameraRing/detectionIn/" : "./src/cameraRing/detectionOut/";
    	
    	File folder = new File(folderRoute);
    	Path path = Paths.get(folderRoute);
    	
    	while (true) {
        	try {
        		WatchService watcher = path.getFileSystem().newWatchService();
        		path.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
        		WatchKey watchKey = watcher.take();
        		List<WatchEvent<?>> events = watchKey.pollEvents();
        		for (WatchEvent event : events) {
        			if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
        				
        				String[] list = folder.list();
        				
        		    	for (int i = 0; i < list.length; i++) {
        		    		if (list[i].endsWith(".jpg")) {
        		    			String imageRoute = folderRoute + list[i];
        		    			
        		    			try {
        							String cmd = "python ./src/cameraRing/matriculasDetector.py " + imageRoute + " " + inOut;
        							Runtime.getRuntime().exec(cmd);
        						} catch (Exception e) {
        						}
        		    		}
        		    	}
        		    	
        			}
        		}
    		} catch (Exception e) {
    		}
    	}
    }
	
	//THREAD HANDLERS
	private static class HandlerDerecha implements Runnable {
	
		private int puertoIzquierda, puertoDerecha, puertoCentralServer;
		private String ipDerecha, ipCentralServer;
		private boolean masterNode;
		private int nodeNumber;
	
		public HandlerDerecha(int puertoIzquierda, int puertoDerecha, int puertoCentralServer, String ipDerecha, String ipCentralServer, boolean masterNode, int nodeNumber) {
			this.puertoIzquierda = puertoIzquierda;
			this.puertoDerecha = puertoDerecha;
			this.puertoCentralServer = puertoCentralServer;
			this.ipDerecha = ipDerecha;
			this.ipCentralServer = ipCentralServer;
			this.masterNode = masterNode;
			this.nodeNumber = nodeNumber;
		}
	
		@Override
		public void run() {
			System.out.println(nodeNumber + " Derecha");
			derecha(puertoIzquierda, puertoDerecha, puertoCentralServer, ipDerecha, ipCentralServer, masterNode);
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
	
	private static class HandlerServerCorba implements Runnable {		
		@Override
		public void run() {
			System.out.println("Server Corba");
			serverCorba();
		}
	}
	
	private static class HandlerClientCorba implements Runnable {
		
		private boolean inOut;
		
		public HandlerClientCorba(boolean inOut) {
			this.inOut = inOut;
		}
		
		@Override
		public void run() {
			System.out.println("Client Corba");
			clientCorba(inOut);
		}
	}
}