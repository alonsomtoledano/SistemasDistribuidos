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

import clock.ProxyClock;
import log.Log;

public class Node {
	
	//PORTS
    static int puertoIzquierda = 6004;
    static int puertoDerecha = 6001;
    static int puertoIzquierda2 = 6008;
    static int puertoDerecha2 = 6005;
    static int puertoCentralServer = 6000;
    
    //IP
    static String ip = "localhost";
    static String ipIzquierda = "localhost";
    static String ipDerecha = "localhost";
    static String ipCentralServer = "localhost";
    
    //NODE VARIABLES
    static boolean masterNode = true; //IS THIS THE MASTER NODE
    static boolean inOut = true; //TRUE = IN, FALSE = OUT
    static boolean firstTime = true; // IS THE FIRST TIME THAT THE SYSTEM IS SET
    static String detectionCorbaPath = "\\localhost\\detectionIn"; //REMOTE IN/OUT FOLDER PATH
    static String matriculasCentralServerPath = "\\localhost\\matriculasCentralServer"; //REMOTE MATRICULAS FOLDER PATH
    
    //LOG VARIBLES
    static String logPath = "./src/cameraRing/logs/node.log";
    static String INFO = "info";
    static String ERROR = "error";
    static String className;
    static long time;
    
    //LOCKS
    static ReentrantLock logLock = new ReentrantLock();
    static ReentrantLock matriculasLock = new ReentrantLock();
    
    //THREADS
    static HandlerDerecha handlerDerecha = new HandlerDerecha();
	static HandlerIzquierda handlerIzquierda = new HandlerIzquierda();
	static HandlerClientCorba handlerClientCorba = new HandlerClientCorba();
	
	//SAVE MESSAGE STATE
	static Message auxMessage = new Message();
	
	public static void main(String[] args) {	
		//LOG DATA
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		
		//THREADS
    	new Thread(handlerDerecha).start();
    	new Thread(handlerIzquierda).start();
    	new Thread(handlerClientCorba).start();
	}
	
	//THREAD FUNCTIONS
    public static void derecha() {
    	String nodeInOut = inOut ? "IN" : "OUT";
    	boolean oneError = true;
        
    	List<List<String>> matriculasInLog = null;
    	List<List<String>> matriculasOutLog = null;

		time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "Right thread started", className, time);
		}finally {
			logLock.unlock();
		}
    	
    	while (true) {
    		try {
    			ServerSocket socketIzquierda = new ServerSocket(puertoIzquierda);
    			Socket sIzquierda;
    			
    			if(masterNode && firstTime) {
        			Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
        			ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
        			
    				Message message = new Message();
    				outputDerecha.writeObject(message);
    				
        			time = ProxyClock.getError();
        			logLock.lock();
        			try {
        				Log.log(INFO, logPath, "First message sent", className, time);
        			}finally {
        				logLock.unlock();
        			}
        			
        			Thread.sleep(2000);
        			System.out.println("FIRST MESSAGE SENT TO NEXT NODE\n");
        			
    			}
    			
    			if (masterNode && firstTime) {
    				socketIzquierda.close();
    				firstTime = false;
    			} else {
        			while ((sIzquierda = socketIzquierda.accept()) != null) {
        				
        				ObjectInputStream inputIzquierda = new ObjectInputStream(sIzquierda.getInputStream());

        				try {
        					Message message = (Message)inputIzquierda.readObject();
        					
        					time = ProxyClock.getError();
                			logLock.lock();
                			try {
                				Log.log(INFO, logPath, "Message received", className, time);
                			}finally {
                				logLock.unlock();
                			}
                			
                			Thread.sleep(2000);
                			System.out.println("MESSAGE RECEIVED");
        					System.out.println("matriculasInLog: " + message.getMatriculasInLog());
        					System.out.println("matriculasOutLog: " + message.getMatriculasOutLog() + "\n");
        					
        					logLock.lock();
        					matriculasLock.lock();
        					try {
        						matriculasInLog = message.getMatriculasInLog();
        						matriculasOutLog = message.getMatriculasOutLog();        						
        						
        						List<String> matricula;
        						String matriculaInfo = null;
        						
        						BufferedReader readerMatriculas  = new BufferedReader(new FileReader("./src/cameraRing/matriculas.txt"));
        						String lineMatriculas = readerMatriculas.readLine();
        						
        						while (lineMatriculas != null) {
        							
        							BufferedReader readerLogMatriculas  = new BufferedReader(new FileReader(logPath));
        							String lineLogMatriculas = readerLogMatriculas.readLine();
        							
        							while (lineLogMatriculas != null) {
        								if (lineLogMatriculas.contains(nodeInOut + ": " + lineMatriculas)) {
        									matriculaInfo = lineLogMatriculas;
        								}
        								lineLogMatriculas = readerLogMatriculas.readLine();
        							}
        							readerLogMatriculas.close();
        							
        							String messageMatricula = matriculaInfo.substring(9, 18);
        							String messageImage = matriculaInfo.substring(19, 33);
        							String messageHora = matriculaInfo.substring(40, matriculaInfo.length());
        							
        							matricula = new ArrayList<String>();
        							matricula.add(messageMatricula);
        							matricula.add(messageImage);
        					    	matricula.add(messageHora);
        					    	
        					    	if (inOut) {
        					    		matriculasInLog.add(matricula);
        					    	} else {
        					    		matriculasOutLog.add(matricula);
        					    	}
        					    	
        							lineMatriculas = readerMatriculas.readLine();
        						}
        						readerMatriculas.close();
        						
        						if (inOut) {
        							message.setMatriculasInLog(matriculasInLog);
        				    	} else {
        				    		message.setMatriculasOutLog(matriculasOutLog);
        				    	}
    							
    							time = ProxyClock.getError();
                				Log.log(INFO, logPath, "Message updated", className, time);
                				
                				Thread.sleep(2000);
                				System.out.println("MESSAGE UPDATED\n");
        						
        						if(masterNode) {        							
        							try {
        								Socket socketCentralServer = new Socket(ipCentralServer, puertoCentralServer);
            	    					ObjectOutputStream outputCentralServer = new ObjectOutputStream (socketCentralServer.getOutputStream());
            	    					outputCentralServer.writeObject(message);
            	    					
            	    					time = ProxyClock.getError();
                        				Log.log(INFO, logPath, "Message sent to server", className, time);
                        				
                        				Thread.sleep(2000);
                        				System.out.println("MESSAGE SENT TO SERVER\n");
                        				
    								} catch (Exception e) {
    								}
        							
        					    	matriculasInLog.clear();
        							matriculasOutLog.clear();
        					    	message.setMatriculasInLog(matriculasInLog);
        					    	message.setMatriculasOutLog(matriculasOutLog);
        							
        					    	time = ProxyClock.getError();
                    				Log.log(INFO, logPath, "Message cleared", className, time);
                    				
                    				Thread.sleep(2000);
                    				System.out.println("MESSAGE CLEARED\n");
        						}
        						
        						BufferedWriter bw = new BufferedWriter(new FileWriter("./src/cameraRing/matriculas.txt"));
        						bw.write("");
        						bw.close();
        						
        						time = ProxyClock.getError();
                				Log.log(INFO, logPath, "Matriculas.txt content deleted", className, time);
        						
        						auxMessage = message;
        						
        						Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
        						ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
        						outputDerecha.writeObject(message);
        						
        		    			oneError = true;
        						
        						time = ProxyClock.getError();
                				Log.log(INFO, logPath, "Message sent to next node", className, time);
                				
                				Thread.sleep(2000);
                				System.out.println("MESSAGE SENT TO NEXT NODE\n");
        						
        					} finally {
        						logLock.unlock();
        						matriculasLock.unlock();
        					}
    					} catch (ClassNotFoundException e) {
    						e.printStackTrace();
    					}
        				socketIzquierda.close();
        				sIzquierda.close();
        				firstTime = false;
        				break;
        			}
    			}

			} catch (IOException | InterruptedException e) {
				
				time = ProxyClock.getError();
    			logLock.lock();
    			try {
    				Log.log(ERROR, logPath, "Node fallen", className, time);
    			}finally {
    				logLock.unlock();
    			}
				
				boolean ringClosed = false;
				
				if (oneError) {
					oneError = false;

					try {					
						Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);
	        			ObjectOutputStream outputIzquierda = new ObjectOutputStream (socketIzquierda.getOutputStream());
						
	        			FallConfiguration fallConfiguration = new FallConfiguration(puertoDerecha, puertoDerecha2, ip);
	    				outputIzquierda.writeObject(fallConfiguration);
	    				
	    				
	    				time = ProxyClock.getError();
	        			logLock.lock();
	        			try {
	        				Log.log(ERROR, logPath, "FallingConfiguration sent to next node", className, time);
	        			}finally {
	        				logLock.unlock();
	        			}
	        			
	        			Thread.sleep(2000);
	        			System.out.println("Node Fallen");
        				System.out.println("FALLING CONFIGURATION SENT TO NEXT NODE\n");
						
						while (!ringClosed) {
							
							Thread.sleep(2000);
							System.out.println("TRY TO SEND MESSAGE\n");
							
							try {
								Socket socketDerechaAux = new Socket(ipDerecha, puertoDerecha);
	    						ObjectOutputStream outputDerechaAux = new ObjectOutputStream (socketDerechaAux.getOutputStream());
	    						outputDerechaAux.writeObject(auxMessage);
	    		    			
	    	    				time = ProxyClock.getError();
	    	        			logLock.lock();
	    	        			try {
	    	        				Log.log(ERROR, logPath, "Message sent to next node", className, time);
	    	        			}finally {
	    	        				logLock.unlock();
	    	        			}
	    	        			
	    	        			Thread.sleep(2000);
	            				System.out.println("MESSAGE SENT TO NEXT NODE\n");
	    						
	    						ringClosed = true;
							} catch (Exception e2) {
							}
						}
	    				
					} catch (Exception e1) {
					}
				}
			}
    	}
    }
    
    public static void izquierda() {
    	
		time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "Left thread started", className, time);
		}finally {
			logLock.unlock();
		}
		
    	while (true) {
    		try {
    			ServerSocket socketDerecha = new ServerSocket(puertoDerecha2);
    			Socket sDerecha;
    			
    			while((sDerecha = socketDerecha.accept()) != null) {
    				ObjectInputStream inputDerecha3 = new ObjectInputStream(sDerecha.getInputStream());
    				try {
    					FallConfiguration fallConfiguration = (FallConfiguration)inputDerecha3.readObject();
    					
    					time = ProxyClock.getError();
    					logLock.lock();
    					try {
    						Log.log(INFO, logPath, "FallConfiguration received", className, time);
    					}finally {
    						logLock.unlock();
    					}
    					
    					Thread.sleep(2000);
        				System.out.println("FALL CONFIGURATION RECEIVED\n");
    					
    					if (fallConfiguration.getCloseRing()) {
    						
    						if (!fallConfiguration.getMasterNode()) {
    							masterNode = true;
    							firstTime = false;
    						}
    						
    						ipDerecha = fallConfiguration.getIp();
        					
        					time = ProxyClock.getError();
        					logLock.lock();
        					try {
        						Log.log(INFO, logPath, "Final node configurated", className, time);
        					}finally {
        						logLock.unlock();
        					}
        					
        					Thread.sleep(2000);
        					System.out.println("FINAL NODE CONFIGURATED\n");
        					
        					socketDerecha.close();
        					sDerecha.close();
							
        					handlerIzquierda.interrupt();
							new Thread(handlerIzquierda).start();
    					} else {
    						if (masterNode) {
        						fallConfiguration.setMasterNode(true);
            					
            					time = ProxyClock.getError();
            					logLock.lock();
            					try {
            						Log.log(INFO, logPath, "FallConfiguration masterNode set true", className, time);
            					}finally {
            						logLock.unlock();
            					}
            					
            					Thread.sleep(2000);
            					System.out.println("MASTER NODE DETECTED\n");
        					}
        					
        					try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);) {
        						
        						ObjectOutputStream outputIzquierda = new ObjectOutputStream(socketIzquierda.getOutputStream());
                                outputIzquierda.writeObject(fallConfiguration);
            					
            					time = ProxyClock.getError();
            					logLock.lock();
            					try {
            						Log.log(INFO, logPath, "FallConfiguration sent to next node", className, time);
            					}finally {
            						logLock.unlock();
            					}
            					
            					Thread.sleep(2000);
                				System.out.println("FALLING CONFIGURATION SENT TO NEXT NODE\n");
            					
                        	}catch(Exception e) {
                        		
                        		ipIzquierda = fallConfiguration.getIp();
                        		puertoIzquierda2 = fallConfiguration.getPuertoDerecha2();
                        		puertoIzquierda = fallConfiguration.getPuertoDerecha();
            					
            					time = ProxyClock.getError();
            					logLock.lock();
            					try {
            						Log.log(INFO, logPath, "Node configurated", className, time);
            					}finally {
            						logLock.unlock();
            					}
            					
            					Thread.sleep(2000);
            					System.out.println("NODE CONFIGURATED\n");
    							
    							handlerDerecha.interrupt();
    							new Thread(handlerDerecha).start();
    							
            					try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);) {
            						
                					fallConfiguration.setCloseRing(true);
                					fallConfiguration.setIp(ip);
            						
            						ObjectOutputStream outputIzquierda = new ObjectOutputStream(socketIzquierda.getOutputStream());
                                    outputIzquierda.writeObject(fallConfiguration);
                					
                					time = ProxyClock.getError();
                					logLock.lock();
                					try {
                						Log.log(INFO, logPath, "FallConfiguration sent to last node", className, time);
                					}finally {
                						logLock.unlock();
                					}
                					
                					Thread.sleep(2000);
                    				System.out.println("FALLING CONFIGURATION SENT TO LAST NODE\n");
                					
								} catch (Exception e2) {
								}
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
    
    public static void clientCorba() {
    	detectionCorbaPath = "\\" + detectionCorbaPath + "\\";
    	matriculasCentralServerPath = "\\" + matriculasCentralServerPath + "\\";
    	boolean matriculaExist = false;

    	time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "Client Corba started", className, time);
		}finally {
			logLock.unlock();
		}
		
    	String folderRoute = inOut ? "./src/cameraRing/detectionIn/" : "./src/cameraRing/detectionOut/";
    	String logInOut = inOut ? "IN" : "OUT";
    	
    	File folder = new File(folderRoute);
    	Path path = Paths.get(folderRoute);
		
    	time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "Listening " + logInOut + " folder", className, time);
		}finally {
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
        		    			
        		    	    	time = ProxyClock.getError();
        		    			logLock.lock();
        		    			try {
        		    				Log.log(INFO, logPath, "Sending license plate to CorbaServer", className, time);
        		    			}finally {
        		    				logLock.unlock();
        		    			}
        		    			
        		    			Thread.sleep(2000);
	            				System.out.println("SENDING LICENSE PLATE TO CORRBA SERVER\n");
        		    			
        		    			matriculasLock.lock();
        		    			try {
            		    			try {
            		    				Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/clientCorba.bat " + detectionCorbaPath + " " + matriculasCentralServerPath);
            		    				
            		    				//READ
            		    				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

            		    				String line = null;
            		    				String matricula = null;
            		    				while ((line = stdInput.readLine()) != null) {
            		    					matricula = line;
            		    				}
            		    				
                		    	    	time = ProxyClock.getError();
                		    			logLock.lock();
                		    			try {
                		    				Log.log(INFO, logPath, logInOut + ": " + matricula + " TIME: " + time, className, time);
                		    			}finally {
                		    				logLock.unlock();
                		    			}
            		    				
                		    			Thread.sleep(2000);
        	            				System.out.println("RECEIVED LICENSE PLATE: " + matricula + " FROM CORBA SERVER\n");
                		    			
            		    				matriculasLock.lock();
            		    				try {
            		    					BufferedReader readerMatriculas  = new BufferedReader(new FileReader("./src/cameraRing/matriculas.txt"));
                    						String lineMatriculas = readerMatriculas.readLine();
                    						
                    				    	
                    						while (lineMatriculas != null) {                							
                								if (lineMatriculas.equals(matricula)) {
                									matriculaExist = true;
                									break;
                								}
                    					    	
                    							lineMatriculas = readerMatriculas.readLine();
                    						}
                    						readerMatriculas.close();
                    						
                    						if (!matriculaExist) {
                    							BufferedWriter bw = new BufferedWriter(new FileWriter("./src/cameraRing/matriculas.txt", true));
                        						bw.write(matricula + "\n");
                        						bw.close();
                    						}
                    						
                    						matriculaExist = false;
            		    				} finally {
            		    					matriculasLock.unlock();
            		    				}
            						} catch (Exception e) {
            							e.printStackTrace();
            						}
								} finally {
									matriculasLock.unlock();
								}
        		    		}
        		    	}
        			}
        		}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
	
	//THREAD HANDLERS
	private static class HandlerDerecha implements Runnable {
		@Override
		public void run() {
			derecha();
		}
		
		public void interrupt() {
			Thread.currentThread().interrupt();
		}
	}
	
	private static class HandlerIzquierda implements Runnable {
		@Override
		public void run() {			
			izquierda();
		}
		
		public void interrupt() {
			Thread.currentThread().interrupt();
		}
	}
	
	private static class HandlerClientCorba implements Runnable {
		@Override
		public void run() {
			clientCorba();
		}
	}
}