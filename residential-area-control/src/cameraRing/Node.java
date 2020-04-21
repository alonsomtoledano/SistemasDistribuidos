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
	    String ip = "localhost";
	    String ipIzquierda = "localhost";
	    String ipDerecha = "localhost";
	    String ipCentralServer = "localhost";
	    
	    //NODE VARIABLES
	    boolean masterNode = true; //IS THIS THE MASTER NODE
	    boolean inOut = true; //TRUE = IN, FALSE = OUT
	    boolean firstTime = true; // IS THE FIRST TIME THAT THE SYSTEM IS SET
	    String detectionCorbaPath = "\\localhost\\detectionIn"; //REMOTE IN/OUT FOLDER PATH
	    String matriculasCentralServerPath = "\\localhost\\matriculasCentralServer"; //REMOTE MATRICULAS FOLDER PATH
	    
	    //LOG VARIBLES
	    String logPath = "./src/cameraRing/node.log";
	    
	    //LOCKS
	    ReentrantLock logLock = new ReentrantLock();
	    ReentrantLock matriculasLock = new ReentrantLock();
		
	    //CODE
		HandlerDerecha handlerDerecha = new HandlerDerecha(puertoIzquierda, puertoIzquierda2, puertoDerecha, puertoDerecha2, puertoCentralServer, ip, ipIzquierda, ipDerecha,
															ipCentralServer, masterNode, inOut, firstTime, logPath, logLock, matriculasLock);
    	new Thread(handlerDerecha).start();
    	
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda(puertoIzquierda, puertoIzquierda2, puertoDerecha2, ip, ipIzquierda, ipDerecha, masterNode, firstTime, logPath, logLock);
    	new Thread(handlerIzquierda).start();
    	
    	HandlerClientCorba handlerClientCorba = new HandlerClientCorba(inOut, detectionCorbaPath, matriculasCentralServerPath, logPath, logLock, matriculasLock);
    	new Thread(handlerClientCorba).start();
	}
	
	//THREAD FUNCTIONS
    public static void derecha(int puertoIzquierda, int puertoIzquierda2, int puertoDerecha, int puertoDerecha2, int puertoCentralServer, String ip, String ipIzquierda, String ipDerecha, String ipCentralServer,
    							boolean masterNode, boolean inOut, boolean firstTime, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
    	String nodeInOut = inOut ? "IN" : "OUT";
    	List<List<String>> matriculasInLog = null;
    	List<List<String>> matriculasOutLog = null;
    	
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
    					
    					System.out.println("matriculasInLog: " + message.getMatriculasInLog());
    					System.out.println("matriculasOutLog: " + message.getMatriculasOutLog());
    					
    					logLock.lock();
    					matriculasLock.lock();
    					try {
    						if (inOut) {
    							matriculasInLog = message.getMatriculasInLog();
    						} else {
    							matriculasOutLog = message.getMatriculasOutLog();
    						}
    						
    						
    						List<String> matricula;
    						int lineLogMatriculasCounter = 0;
    						int lineLogHoraCounter = 0;
    						String messageHora = null ;
    						
    						BufferedReader readerMatriculas  = new BufferedReader(new FileReader("./src/cameraRing/matriculas.txt"));
    						String lineMatriculas = readerMatriculas.readLine();
    						
    						while (lineMatriculas != null) {
    							lineLogMatriculasCounter = 0;
    							lineLogHoraCounter = 0;
    							
    							BufferedReader readerLogMatriculas  = new BufferedReader(new FileReader("./src/cameraRing/node.log"));
    							BufferedReader readerLogHora  = new BufferedReader(new FileReader("./src/cameraRing/node.log"));
    							String lineLogMatriculas = readerLogMatriculas.readLine();
    							String lineLogHora = readerLogHora.readLine();
    							
    							while (lineLogMatriculas != null) {
    								if (lineLogMatriculas.contains(nodeInOut + ": " + lineMatriculas)) {
    									while (lineLogHora != null) {
    										if (lineLogHoraCounter == lineLogMatriculasCounter - 1) {
    											messageHora = lineLogHora;
    											break;
    										}
    										lineLogHoraCounter++;
    										lineLogHora = readerLogHora.readLine();
    									}
    								}
    								lineLogMatriculasCounter++;
    								lineLogMatriculas = readerLogMatriculas.readLine();
    							}
    							readerLogMatriculas.close();
    							readerLogHora.close();
    							
    							messageHora = messageHora.substring(13, messageHora.length() - 19);
    							
    							matricula = new ArrayList<String>();
    							matricula.add(lineMatriculas);
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
    						
    						Thread.sleep(3000);
							Log.log("info", logPath, "Message updated");
    						
    						
    						if(masterNode) {
    	    					Thread.sleep(3000);
    							Log.log("info", logPath, "Message sending to server");
    							
    							try {
    								Socket socketCentralServer = new Socket(ipCentralServer, puertoCentralServer);
        	    					ObjectOutputStream outputCentralServer = new ObjectOutputStream (socketCentralServer.getOutputStream());
        	    					outputCentralServer.writeObject(message);
        	    					
        	    					Thread.sleep(3000);
        							Log.log("info", logPath, "Message sent to server");
								} catch (Exception e) {
								}
    							
    					    	matriculasInLog.clear();
    					    	message.setMatriculasInLog(matriculasInLog);
    							
    	    					Thread.sleep(3000);
    							Log.log("info", logPath, "Message cleared");
    						}
    						
    						BufferedWriter bw = new BufferedWriter(new FileWriter("./src/cameraRing/matriculas.txt"));
    						bw.write("");
    						bw.close();
    						
    						Thread.sleep(3000);
    						Log.log("info", logPath, "Matriculas.txt content deleted");
    						
    						Thread.sleep(3000);
    						Log.log("info", logPath, "Sending message");
    						
    						Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
    						ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
    						outputDerecha.writeObject(message);
    						
    						Thread.sleep(3000);
    						Log.log("info", logPath, "Message sent to next node");
    						
    					} finally {
    						logLock.unlock();
    						matriculasLock.unlock();
    					}
					} catch (ClassNotFoundException | InterruptedException e) {
						e.printStackTrace();
					}
    			}
			} catch (IOException e) {
				try {
					Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);
        			ObjectOutputStream outputIzquierda = new ObjectOutputStream (socketIzquierda.getOutputStream());
    				
					logLock.lock();
					try {
						Thread.sleep(3000);
						Log.log("info", logPath, "Sending FallingConfiguration");
					} finally {
						logLock.unlock();
					}
					
        			FallConfiguration fallConfiguration = new FallConfiguration(puertoDerecha, puertoDerecha2, ip);
    				outputIzquierda.writeObject(fallConfiguration);
    				
    				logLock.lock();
					try {
						Thread.sleep(3000);
						Log.log("info", logPath, "FallingConfiguration sent to next node");
					} finally {
						logLock.unlock();
					}
    				
				} catch (Exception e1) {
				}
			}
    	}
    }
    
    public static void izquierda(int puertoIzquierda, int puertoIzquierda2, int puertoDerecha2, String ip, String ipIzquierda, String ipDerecha, boolean masterNode,
    								boolean firstTime, String logPath, ReentrantLock logLock) {
		logLock.lock();
		try {
			Log.log("info", logPath, "Left thread started");
		} finally {
			logLock.unlock();
		}
		
    	
    	while (true) {
    		try {
    			ServerSocket socketDerecha = new ServerSocket(puertoDerecha2);
    			Socket sDerecha;
    			
    			while((sDerecha = socketDerecha.accept()) != null) {
    				ObjectInputStream inputDerecha = new ObjectInputStream(sDerecha.getInputStream());
    				try {
    					FallConfiguration fallConfiguration = (FallConfiguration)inputDerecha.readObject();
    					
    					logLock.lock();
    					try {
        					Thread.sleep(3000);
    						Log.log("info", logPath, "FallConfiguration recieved");
    					} finally {
    						logLock.unlock();
    					}
    					
    					if (fallConfiguration.getCloseRing()) {
    						
    						if (fallConfiguration.getMasterNode()) {
    							masterNode = true;
    							firstTime = false;
    						}
    						
    						ipDerecha = fallConfiguration.getIp();
    						
    					} else {
    						
    						if (masterNode) {
        						fallConfiguration.setMasterNode(true);
        						
        						logLock.lock();
            					try {
                					Thread.sleep(3000);
            						Log.log("info", logPath, "FallConfiguration masterNode set true");
            					} finally {
            						logLock.unlock();
            					}
        					}
        					
        					logLock.lock();
        					try {
            					Thread.sleep(3000);
        						Log.log("info", logPath, "Sending FallConfiguration");
        					} finally {
        						logLock.unlock();
        					}
        					
        					try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);) {
        						
        						ObjectOutputStream outputIzquierda = new ObjectOutputStream(socketIzquierda.getOutputStream());
                                outputIzquierda.writeObject(fallConfiguration);
                                
                                logLock.lock();
            					try {
                					Thread.sleep(3000);
            						Log.log("info", logPath, "FallConfiguration sent to next node");
            					} finally {
            						logLock.unlock();
            					}
            					
                        	}catch(Exception e) {
                        		
                        		ipIzquierda = fallConfiguration.getIp();
                        		puertoIzquierda2 = fallConfiguration.getPuertoDerecha2();
                        		puertoIzquierda = fallConfiguration.getPuertoDerecha();
                        		
                        		logLock.lock();
            					try {
                					Thread.sleep(3000);
            						Log.log("info", logPath, "Node configurated");
            					} finally {
            						logLock.unlock();
            					}
            					
            					fallConfiguration.setCloseRing(true);
            					fallConfiguration.setIp(ip);
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
    
    public static void clientCorba(boolean inOut, String detectionCorbaPath, String matriculasCentralServerPath, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
    	detectionCorbaPath = "\\" + detectionCorbaPath + "\\";
    	matriculasCentralServerPath = "\\" + matriculasCentralServerPath + "\\";
    	boolean matriculaExist = false;

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
            		    				Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/clientCorba.bat " + detectionCorbaPath + " " + matriculasCentralServerPath);
            		    				
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
	
		private int puertoIzquierda, puertoIzquierda2, puertoDerecha, puertoDerecha2, puertoCentralServer;
		private String ip, ipIzquierda, ipDerecha, ipCentralServer, logPath;
		private boolean masterNode, inOut, firstTime;
		private ReentrantLock logLock, matriculasLock;
	
		public HandlerDerecha(int puertoIzquierda, int puertoIzquierda2, int puertoDerecha, int puertoDerecha2, int puertoCentralServer, String ip, String ipIzquierda, String ipDerecha,
								String ipCentralServer, boolean masterNode, boolean inOut, boolean firstTime, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
			this.puertoIzquierda = puertoIzquierda;
			this.puertoIzquierda2 = puertoIzquierda2;
			this.puertoDerecha = puertoDerecha;
			this.puertoDerecha2 = puertoDerecha2;
			this.puertoCentralServer = puertoCentralServer;
			this.ip = ip;
			this.ipIzquierda = ipIzquierda;
			this.ipDerecha = ipDerecha;
			this.ipCentralServer = ipCentralServer;
			this.masterNode = masterNode;
			this.inOut = inOut;
			this.firstTime = firstTime;
			this.logPath = logPath;
			this.logLock = logLock;
			this.matriculasLock = matriculasLock;
		}
	
		@Override
		public void run() {
			derecha(puertoIzquierda, puertoIzquierda2, puertoDerecha, puertoDerecha2, puertoCentralServer, ip, ipIzquierda, ipDerecha, ipCentralServer,
					masterNode, inOut, firstTime, logPath, logLock, matriculasLock);
		}
	}
	
	private static class HandlerIzquierda implements Runnable {
		
		private int puertoIzquierda, puertoIzquierda2, puertoDerecha2;
		private String ip, ipIzquierda, ipDerecha, logPath;
		private boolean masterNode, firstTime;
		private ReentrantLock logLock;
	
		public HandlerIzquierda(int puertoIzquierda, int puertoIzquierda2, int puertoDerecha2, String ip, String ipIzquierda, String ipDerecha, boolean masterNode, boolean firstTime,
								String logPath, ReentrantLock logLock) {
			this.puertoIzquierda = puertoIzquierda;
			this.puertoIzquierda2 = puertoIzquierda2;
			this.puertoDerecha2 = puertoDerecha2;
			this.ip = ip;
			this.ipIzquierda = ipIzquierda;
			this.ipDerecha = ipDerecha;
			this.masterNode = masterNode;
			this.firstTime = firstTime;
			this.logPath = logPath;
			this.logLock = logLock;
		}
	
		@Override
		public void run() {			
			izquierda(puertoIzquierda, puertoIzquierda2, puertoDerecha2, ip, ipIzquierda, ipDerecha, masterNode, firstTime, logPath, logLock);
		}
	}
	
	private static class HandlerClientCorba implements Runnable {
		
		private boolean inOut;
		private String detectionCorbaPath, matriculasCentralServerPath, logPath;
		private ReentrantLock logLock, matriculasLock;
		
		public HandlerClientCorba(boolean inOut, String detectionCorbaPath, String matriculasCentralServerPath, String logPath, ReentrantLock logLock, ReentrantLock matriculasLock) {
			this.inOut = inOut;
			this.detectionCorbaPath = detectionCorbaPath;
			this.matriculasCentralServerPath = matriculasCentralServerPath;
			this.logPath = logPath;
			this.logLock = logLock;
			this.matriculasLock = matriculasLock;
		}
		
		@Override
		public void run() {
			clientCorba(inOut, detectionCorbaPath, matriculasCentralServerPath, logPath, logLock, matriculasLock);
		}
	}
}