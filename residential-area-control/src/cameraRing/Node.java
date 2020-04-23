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
    static String logPath = "./src/cameraRing/node.log";
    
    //LOCKS
    static ReentrantLock logLock = new ReentrantLock();
    static ReentrantLock matriculasLock = new ReentrantLock();
    
    static HandlerDerecha handlerDerecha = new HandlerDerecha();
	
	public static void main(String[] args) {		
	    //CODE
    	new Thread(handlerDerecha).start();
    	
    	HandlerIzquierda handlerIzquierda = new HandlerIzquierda();
    	new Thread(handlerIzquierda).start();
    	
    	HandlerClientCorba handlerClientCorba = new HandlerClientCorba();
    	new Thread(handlerClientCorba).start();
	}
	
	//THREAD FUNCTIONS
    public static void derecha() {
    	String nodeInOut = inOut ? "IN" : "OUT";
    	boolean oneError = true;
        boolean errorFlag = false;
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
    			System.out.println("Creando nuevo socket: " + puertoIzquierda);
    			ServerSocket socketIzquierda = new ServerSocket(puertoIzquierda);
    			Socket sIzquierda;
    			
    			System.out.println("Escuchando en el puerto izquierda: " + puertoIzquierda);
    			
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
    			
    			if (masterNode && firstTime) {
    				socketIzquierda.close();
    				firstTime = false;
    			} else {    					
        			while ((sIzquierda = socketIzquierda.accept()) != null) {
        				
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
        						Log.log("info", logPath, "Sending message to next node");
        						
        						Socket socketDerecha = new Socket(ipDerecha, puertoDerecha);
        						ObjectOutputStream outputDerecha = new ObjectOutputStream (socketDerecha.getOutputStream());
        						outputDerecha.writeObject(message);
        						
        		    			oneError = true;
        						
        						Thread.sleep(3000);
        						Log.log("info", logPath, "Message sent to next node");
        						
        					} finally {
        						logLock.unlock();
        						matriculasLock.unlock();
        					}
    					} catch (ClassNotFoundException | InterruptedException e) {
    						e.printStackTrace();
    					}
        				socketIzquierda.close();
        				sIzquierda.close();
        				firstTime = false;
        				break;
        			}
    			}

			} catch (IOException e) {
				
				boolean ringClosed = false;
				errorFlag = true;
				
				if (oneError) {
					oneError = false;

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
						
						while (!ringClosed) {
							System.out.println("try to send new object");
							try {
								Socket socketDerechaAux = new Socket(ipDerecha, puertoDerecha);
	    						ObjectOutputStream outputDerechaAux = new ObjectOutputStream (socketDerechaAux.getOutputStream());
	    						Message messageAux = new Message();
	    						outputDerechaAux.writeObject(messageAux);

	    		    			oneError = true;
	    		    			
	    		    			logLock.lock();
	    						try {
	    							Thread.sleep(3000);
	    							Log.log("info", logPath, "Message sent to next node");
	    						} finally {
	    							logLock.unlock();
	    						}
	    						
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
		logLock.lock();
		try {
			Log.log("info", logPath, "Left thread started");
		} finally {
			logLock.unlock();
		}
		
    	
    	while (true) {
    		try {
    			ServerSocket socketDerecha3 = new ServerSocket(puertoDerecha2);
    			Socket sDerecha3;
    			
    			while((sDerecha3 = socketDerecha3.accept()) != null) {
    				ObjectInputStream inputDerecha3 = new ObjectInputStream(sDerecha3.getInputStream());
    				try {
    					FallConfiguration fallConfiguration = (FallConfiguration)inputDerecha3.readObject();
    					
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
    						
    						logLock.lock();
        					try {
            					Thread.sleep(3000);
        						Log.log("info", logPath, "Final node configurated");
        					} finally {
        						logLock.unlock();
        					}
    						
    					} else {
    						System.out.println("Aqui no deberia entrar");
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
            					
            					System.out.println("ip: " + ip);
    							System.out.println("puertoIzquierda: " + puertoIzquierda);
    							
    							handlerDerecha.interrupt();/////////////////////////////////////////////////////////////////////////////////
    							new Thread(handlerDerecha).start();
    							
            					try(Socket socketIzquierda = new Socket(ipIzquierda, puertoIzquierda2);) {
            						
                					fallConfiguration.setCloseRing(true);
                					fallConfiguration.setIp(ip);
            						
            						ObjectOutputStream outputIzquierda = new ObjectOutputStream(socketIzquierda.getOutputStream());
                                    outputIzquierda.writeObject(fallConfiguration);
                                    
                                    logLock.lock();
                					try {
                    					Thread.sleep(3000);
                						Log.log("info", logPath, "Last FallConfiguration sent to next node");
                					} finally {
                						logLock.unlock();
                					}
                					
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
	}
	
	private static class HandlerClientCorba implements Runnable {
		@Override
		public void run() {
			clientCorba();
		}
	}
}