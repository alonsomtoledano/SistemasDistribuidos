package cameraRing;

import java.io.*;
import java.net.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

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
	    
	    //NODE VARIABLES
	    boolean masterNode = true; //IS THIS THE MASTER NODE
	    boolean inOut = true; //TRUE = IN, FALSE = OUT
	    boolean firstTime = true; // IS THE FIRST TIME THAT THE SYSTEM IS SET
	    
	    //LOG VARIBLES
	    String logPath = "./src/cameraRing/node.log";
	    
	    //LOCKS
	    ReentrantLock logLock = new ReentrantLock();
	    ReentrantLock matriculasLock = new ReentrantLock();
		
	    //CODE
		HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoDerecha, puertoCentralServer, ipDerecha, ipCentralServer, masterNode, firstTime, logPath, logLock, matriculasLock);
    	new Thread(handlerDerecha).start();
    	
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda, logPath, logLock);
    	new Thread(handlerIzquierda).start();
    	
    	if (masterNode) {
        	HandlerServerCorba handlerServerCorba = new HandlerServerCorba(logPath, logLock);
        	new Thread(handlerServerCorba).start();
    	}
    	
    	HandlerClientCorba handlerClientCorba = new HandlerClientCorba(inOut, logPath, logLock, matriculasLock);
    	new Thread(handlerClientCorba).start();
    	/*
    	//Se crea un nuevo mensaje y se hace el get de la lista
    	Message mensaje = new Message();
    	List<List<String>> matriculasInLog = mensaje.getMatriculasInLog();
    	
    	//Se crea la primera pareja de matricula - hora
    	List<String> matricula1 = new ArrayList<String>();
    	matricula1.add("matricula1");
    	matricula1.add("hora1");
    	
    	//Se crea la segunda pareja de matricula - hora
    	List<String> matricula2 = new ArrayList<String>();
    	matricula2.add("matricula2");
    	matricula2.add("hora2");
    	
    	//Se a�aden las parejas
    	matriculasInLog.add(matricula1);
    	matriculasInLog.add(matricula2);
    	
    	//Se hace el set
    	mensaje.setMatriculasInLog(matriculasInLog);
    	
    	//Se muestra por pantalla toda la lista
    	System.out.println(mensaje.getMatriculasInLog());
    	
    	//Se muestra por pantalla cada valor
    	System.out.println(mensaje.getMatriculasInLog().get(0));
    	System.out.println(mensaje.getMatriculasInLog().get(1));
    	System.out.println(mensaje.getMatriculasInLog().get(0).get(0));
    	System.out.println(mensaje.getMatriculasInLog().get(0).get(1));
    	System.out.println(mensaje.getMatriculasInLog().get(1).get(0));
    	System.out.println(mensaje.getMatriculasInLog().get(1).get(1));
    	
    	//Se hace el get de la lista, se vac�a, se hace el set y se muestra por pantalla
    	matriculasInLog = mensaje.getMatriculasInLog();
    	matriculasInLog.clear();
    	mensaje.setMatriculasInLog(matriculasInLog);
    	System.out.println(mensaje.getMatriculasInLog());*/
	}
	
	//THREAD FUNCTIONS
    public static void derecha(int puertoIzquierda, int puertoDerecha, int puertoCentralServer, String ipDerecha, String ipCentralServer, boolean masterNode,
    							boolean firstTime, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
    	logLock.lock();
		try {
			Log.log("info", logPath, "Right thread started");
		} finally {
			logLock.unlock();
		}
    	
    	while (true) {
    		try {
    			ServerSocket socketIzquierda = new ServerSocket(puertoIzquierda);
    			Socket sIzquierda;
    			
    			if(masterNode && firstTime) {
        			Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
        			ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
        			
        			logLock.lock();
        			try {
            			try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
        				Log.log("info", logPath, "Making fist message");
        			} finally {
        				logLock.unlock();
        			}
        			
    				Message message = new Message();
    				outputDerecha.writeObject(message);
    				
    				logLock.lock();
    				try {
    					try {
							Thread.sleep(3000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
    					Log.log("info", logPath, "First message sent");
    				} finally {
    					logLock.unlock();
    				}
    			}
    			
    			while((sIzquierda = socketIzquierda.accept()) != null) {
    				ObjectInputStream inputIzquierda = new ObjectInputStream(sIzquierda.getInputStream());
    				try {
    					Message message = (Message)inputIzquierda.readObject();
    					
    					logLock.lock();
    					try {
        					Thread.sleep(3000);
    						Log.log("info", logPath, "Message recieved");
    					} finally {
    						logLock.unlock();
    					}
    					
    					List<List<String>> matriculasInLog = message.getMatriculasInLog();
    					
    					//System.out.println("MENSAJE RECIBIDO: " + message.getContent());
    					
    					logLock.lock();
    					try {
        					Thread.sleep(3000);
    						Log.log("info", logPath, "Sending message");
    					} finally {
    						logLock.unlock();
    					}
    					
    					Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
    					ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
    					outputDerecha.writeObject(message);
    					
    					logLock.lock();
    					try {
        					Thread.sleep(3000);
    						Log.log("info", logPath, "Message sent to next node");
    					} finally {
    						logLock.unlock();
    					}
    					
    					if(masterNode) {
    						
    						logLock.lock();
        					try {
            					Thread.sleep(3000);
        						Log.log("info", logPath, "Message sending to server");
        					} finally {
        						logLock.unlock();
        					}
    						
    						Socket socketCentralServer = new Socket(ipCentralServer, puertoCentralServer);
        					ObjectOutputStream outputCentralServer = new ObjectOutputStream (socketCentralServer.getOutputStream());
        					outputCentralServer.writeObject(message);

        					logLock.lock();
        					try {
            					Thread.sleep(3000);
        						Log.log("info", logPath, "Message sent to server");
        					} finally {
        						logLock.unlock();
        					}
        					
    					}
					} catch (ClassNotFoundException | InterruptedException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
			}
    	}
    }
    
    public static void izquierda(int puertoIzquierda2, int puertoDerecha2, String ipIzquierda, String logPath, ReentrantLock logLock) {
		logLock.lock();
		try {
			Log.log("info", logPath, "Left thread started");
		} finally {
			logLock.unlock();
		}
    }
    
    public static void serverCorba(String logPath, ReentrantLock logLock) {
		logLock.lock();
		try {
			Log.log("info", logPath, "Server Corba started");
		} finally {
			logLock.unlock();
		}
		
		try {
			Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/serverCorba.bat");
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public static void clientCorba(boolean inOut, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
		logLock.lock();
		try {
			Log.log("info", logPath, "Client Corba started");
		} finally {
			logLock.unlock();
		}
		
    	String folderRoute = inOut ? "./src/cameraRing/detectionIn/" : "./src/cameraRing/detectionOut/";
    	String logInOut = inOut ? "IN" : "OUT";
    	
    	File folder = new File(folderRoute);
    	Path path = Paths.get(folderRoute);
    	
		logLock.lock();
		try {
			Log.log("info", logPath, "Listening " + logInOut + " folder");
		} finally {
			logLock.unlock();
		}
    	
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
        		    			
        		    			logLock.lock();
        		    			try {
        		    				Log.log("info", logPath, "Sending matricula to CorbaServer");
        		    			} finally {
        		    				logLock.unlock();
        		    			}
        		    			
        		    			matriculasLock.lock();
        		    			try {
            		    			try {
            		    				Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/clientCorba.bat " + inOut);
            		    				
            		    				//READ
            		    				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            		    				String line = null;
            		    				String matricula = null;
            		    				while ((line = stdInput.readLine()) != null) {
            		    					matricula = line;
            		    				}

            		    				logLock.lock();
            		    				try {
            		    					Log.log("info", logPath, logInOut + ": " + matricula);
            		    				} finally {
            		    					logLock.unlock();
            		    				}
            		    				
            						} catch (Exception e) {
            						}
								} finally {
									matriculasLock.unlock();
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
		private String ipDerecha, ipCentralServer, logPath;
		private boolean masterNode, firstTime;
		private ReentrantLock logLock, matriculasLock;
	
		public HandlerDerecha(int puertoIzquierda, int puertoDerecha, int puertoCentralServer, String ipDerecha, String ipCentralServer,
								boolean masterNode, boolean firstTime, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
			this.puertoIzquierda = puertoIzquierda;
			this.puertoDerecha = puertoDerecha;
			this.puertoCentralServer = puertoCentralServer;
			this.ipDerecha = ipDerecha;
			this.ipCentralServer = ipCentralServer;
			this.masterNode = masterNode;
			this.firstTime = firstTime;
			this.logPath = logPath;
			this.logLock = logLock;
			this.matriculasLock = matriculasLock;
		}
	
		@Override
		public void run() {
			derecha(puertoIzquierda, puertoDerecha, puertoCentralServer, ipDerecha, ipCentralServer, masterNode, firstTime, logPath, logLock, matriculasLock);
		}
	}
	
	private static class HandlerIzquierda implements Runnable {
		
		private int puertoIzquierda2, puertoDerecha2;
		private String ipIzquierda, logPath;
		private ReentrantLock logLock;
	
		public HandlerIzquierda(int puertoIzquierda2, int puertoDerecha2, String ipIzquierda, String logPath, ReentrantLock logLock) {
			this.puertoIzquierda2 = puertoIzquierda2;
			this.puertoDerecha2 = puertoDerecha2;
			this.ipIzquierda = ipIzquierda;
			this.logPath = logPath;
			this.logLock = logLock;
		}
	
		@Override
		public void run() {			
			izquierda(puertoIzquierda2, puertoDerecha2, ipIzquierda, logPath, logLock);
		}
	}
	
	private static class HandlerServerCorba implements Runnable {
		
		private String logPath;
		private ReentrantLock logLock;
		
		public HandlerServerCorba(String logPath, ReentrantLock logLock) {
			this.logPath = logPath;
			this.logLock = logLock;
		}
		
		@Override
		public void run() {
			serverCorba(logPath, logLock);
		}
	}
	
	private static class HandlerClientCorba implements Runnable {
		
		private boolean inOut;
		private String logPath;
		private ReentrantLock logLock, matriculasLock;
		
		public HandlerClientCorba(boolean inOut, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
			this.inOut = inOut;
			this.logPath = logPath;
			this.logLock = logLock;
			this.matriculasLock = matriculasLock;
		}
		
		@Override
		public void run() {
			clientCorba(inOut, logPath, logLock, matriculasLock);
		}
	}
}