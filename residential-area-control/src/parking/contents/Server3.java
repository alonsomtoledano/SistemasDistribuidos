package parking.contents;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import log.Log;

import java.nio.file.WatchEvent.Kind;

public class Server3 {
	//PORTS
	static int proxyPort = 5100;
	static int server3Port = 5002;
	//IP
	static String serverHost = "127.0.0.1";
	//LOG VARIABLES
	static String path = "./src/parking/logs/server3.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	//LOKS
	static ReentrantLock logLock = new ReentrantLock();
	//CLOCK
	static ProxyClock clock = new ProxyClock();
	static long time;
	static long timeUpdate;
	
	public static void main(String[] args) {
		//CODE
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		time = ProxyClock.getError();
		System.out.println("SERVER 3 START.....");
		
		logLock.lock();
		try {
			Log.log(INFO, path, "SERVER 3 START", className, time);
		}finally {
			logLock.unlock();
		}
		
		authentication();
	
		try {
			ServerSocket server = new ServerSocket(server3Port);
			while(true) {
				Socket serverAccept = server.accept();
				ProxyHandler threadProxyAccept = new ProxyHandler(serverAccept);
				
				new Thread(threadProxyAccept).start();	
			}	
		}catch(IOException e) {
			
			logLock.lock();
			try {
				Log.log(ERROR, path, Log.getStackTrace(e), className, time);
			}finally {
				logLock.unlock();
			}
		}
	}
	
	private static class ProxyHandler implements Runnable {
		private final Socket socketFromProxy;
		
		public ProxyHandler(Socket socketFromProxy) {
			this.socketFromProxy = socketFromProxy;
		}
		
		@Override
		public void run() {
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "new thread SERVER 3 start", className, time);
			}finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(socketFromProxy.getInputStream());
				Message messageStatus = (Message)ois.readObject();
				boolean status = messageStatus.isStatus();
				if(status) {
					time = ProxyClock.getError();
					
					logLock.lock();
					try {
						Log.log(INFO, path, "SERVER 3 correct authenticaction", className, time);
					}finally {
						logLock.unlock();
					}
					inputLogHandler();
				}else {
					
					logLock.lock();
					try {
						Log.log(INFO, path, "authentication error on the SERVER 3", className, time);
					}finally {
						logLock.unlock();
					}
					System.out.println("authentication error on the SERVER 3");
					
					logLock.lock();
					try {
						Log.log(INFO, path, "SERVER 3 stopped", className, time);
					}finally {
						logLock.unlock();
					}
					System.exit(0);	
				}
			}catch (IOException e) {
				
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, time);
				}finally {
					logLock.unlock();
				}
			} catch (ClassNotFoundException e) {
				
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, time);
				}finally {
					logLock.unlock();
				}
			} 
		}
	}
	//FUNCTIONS
	public static void authentication() {
		time = ProxyClock.getError();
		
		logLock.lock();
		try {
			Log.log(INFO, path, "SERVER 3 authentication sent to PROXY", className, time);
		}finally {
			logLock.unlock();
		}
		try {
			Socket socketToProxy = new Socket(serverHost, proxyPort);
			ObjectOutputStream oos = new ObjectOutputStream(socketToProxy.getOutputStream());
            String authentication = "authentication";
            Message messageAuthentication = new Message(authentication);
            Message messagePort = new Message(server3Port);
            oos.writeObject(messageAuthentication);
            oos.writeObject(messagePort);
            
            ObjectOutputStream oos1 = new ObjectOutputStream(socketToProxy.getOutputStream());
            Message messageIP = new Message(serverHost); 
            String mac = getMAC();
            Message messageMAC = new Message(mac);
             
            oos1.writeObject(messageIP);
            oos1.writeObject(messageMAC);
            
            socketToProxy.close();
		}catch(IOException e){
			
			logLock.lock();
			try {
				Log.log(ERROR, path, Log.getStackTrace(e), className, time);
			}finally {
				logLock.unlock();
			}
		}
	}
	
	public static String getMAC() {
		StringBuilder sb = new StringBuilder();
		NetworkInterface networkInterface; 
   	 	time = ProxyClock.getError();
   	 try {
   		 networkInterface = NetworkInterface.getByInetAddress(InetAddress.getLocalHost());
   		 byte[] mac = networkInterface.getHardwareAddress();

   		 for (int i = 0; i < mac.length; i++) {
   			 sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : "")); 	 
   		 } 
   	 } catch (Exception e) {
   		
   		 logLock.lock();
   		 try {
	   		Log.log(ERROR, path, Log.getStackTrace(e), className, time); 
		}finally {
			logLock.unlock();
		}
   	 }
   	 return ""+sb.toString();
    }
	
	public static void inputLogHandler() {
		time = ProxyClock.getError();
		
		logLock.lock();
		try {
			Log.log(INFO, path, "SERVER 3 listening to events", className, time);
		}finally {
			logLock.unlock();
		}
	
		try (WatchService ws = FileSystems.getDefault().newWatchService()) {
            // directory to be monitored  
            Path dirToWatch = Paths.get("./src/parking/SERVERS/SERVER3");

            // events we wish to be notified of
            dirToWatch.register(ws, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);
            System.out.println("\nSERVER 3 LISTENING TO CHANGES IN THE FILES.....");

            while (true) {
                // get the key
                WatchKey key = ws.take();
                // processes the events that have occurred
                for (WatchEvent<?> event : key.pollEvents()) {
                    Kind<?> eventKind = event.kind();
                    if (eventKind == OVERFLOW) {
                        System.out.println("Event overflow occurred");
                        continue;
                    }
                    // get information about the event that happened
                    WatchEvent<Path> currEvent = (WatchEvent<Path>) event;
                    Path dirEntry = currEvent.context();
                    
                    timeUpdate = ProxyClock.getError();
                    
                    System.out.println("\nCHANGE PRODUCED IN THE FILE: " + dirEntry);
                    
                    logLock.lock();
            		try {
                        Log.log(INFO, path, "SERVER 3 file update", className, timeUpdate);
            		}finally {
            			logLock.unlock();
            		}
            		logLock.lock();
            		try {
                        Log.log(INFO, path, "SERVER 3 request getTime to PROXYCLOCK", className, timeUpdate);
            		}finally {
            			logLock.unlock();
            		}
            		logLock.lock();
            		try {
        	            Log.log(INFO, path, "SERVER 3 received adjustTime from PROXYCLOCK", className, timeUpdate);
            		}finally {
            			logLock.unlock();
            		}
                    
                    sendData(dirEntry);
                    time = ProxyClock.getError();
                    
                    logLock.lock();
            		try {
                        Log.log(INFO, path, "SERVER 3 send data to PROXY", className, time);
            		}finally {
            			logLock.unlock();
            		}
            		logLock.lock();
            		try {
                        Log.log(INFO, path, "***************************************", className, time);
            		}finally {
            			logLock.unlock();
            		}
                }
                // reset the key
                boolean isKeyValid = key.reset();
                if (!isKeyValid) {
                    break;
                }
            }
        } catch (IOException | InterruptedException e) {
        	
        	logLock.lock();
    		try {
            	Log.log(ERROR, path, Log.getStackTrace(e), className, time);
    		}finally {
    			logLock.unlock();
    		}
        }
	}
	
	public static void sendData(Path nameFile) {
		//update files
        String auxNameFile = nameFile.toString();
        
        File writeInputs= null;
        File inputLog = null;
        File backupInputLog = null;
        File writeOutputs = null;
        File outputLog = null;
        File backupOutputLog = null;
        
        if (auxNameFile.equals("writeInputs.txt")) {
        	writeInputs = new File("./src/parking/SERVERS/SERVER3/writeInputs.txt" );
        	inputLog = new File("./src/parking/filesOutput/SERVER3/inputLog.txt");
        	backupInputLog = new File("./src/parking/filesBackup/SERVER3/backupInputLog.txt");
        	String dataBaseInputLog = "inputLog";
	    	
        	try {
	    		//socket connecting to the proxy
	            Socket socketToProxy = new Socket(serverHost, proxyPort);
	            //object being sent to the proxy
	            ObjectOutputStream oos = new ObjectOutputStream(socketToProxy.getOutputStream());
	 		
	 			InputStream in = new FileInputStream(writeInputs);
	 	        OutputStream out = new FileOutputStream(inputLog);
	 	        OutputStream outBackupInputLog = new FileOutputStream(backupInputLog);
	 	        
	 	        byte[] buf = new byte[1024];
	 	        int len;
	 	        
	 	        //write two files
	 	        while ((len = in.read(buf)) > 0) {
	 	        	out.write(buf, 0, len);
	 	        	outBackupInputLog.write(buf, 0, len);
	 	        }
	            String data = data(auxNameFile);
	
	            String commandToProxy = "newData";
	            Message newData = new Message(commandToProxy);
	            oos.writeObject(newData);
	
	            ObjectOutputStream oos1 = new ObjectOutputStream(socketToProxy.getOutputStream());

	            //System.out.println(date);
	            Message messageDataBase = new Message(dataBaseInputLog);
	            Message messageData = new Message(data);
	            Message messageDate = new Message(timeUpdate);
	            
	            oos1.writeObject(messageDataBase);
	            oos1.writeObject(messageData);
	            oos1.writeObject(messageDate);

	            System.out.println("\n===========================");
	            System.out.println("LAST CHANGE SENT TO PROXY -> ");
	            System.out.println("DATA: " + data);
	            System.out.println("DATE: " + timeUpdate);
	            
	            //Date d = new Date(timeUpdate);
	            //System.out.println("FECHA ACTUALIZADA: " + d);
	 	        
	 	        //Close files
	 	        in.close();
	 	        out.close();
	 	        outBackupInputLog.close();
	 	        
	 	        socketToProxy.close();
	    	}catch(IOException e) {
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(ERROR, path, Log.getStackTrace(e), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	 		}
        }else {
        	writeOutputs = new File("./src/parking/SERVERS/SERVER3/writeOutputs.txt");
        	outputLog = new File("./src/parking/filesOutput/SERVER3/outputLog.txt");
            backupOutputLog = new File("./src/parking/filesBackup/SERVER3/backupOutputLog.txt");
            String dataBaseOutputLog = "outputLog";
            
            try {
	    		//socket connecting to the proxy
	            Socket socketToProxy = new Socket(serverHost, proxyPort);
	            //object being sent to the proxy
	            ObjectOutputStream oos = new ObjectOutputStream(socketToProxy.getOutputStream());
	 		
	 			InputStream in = new FileInputStream(writeOutputs);
	 	        OutputStream out = new FileOutputStream(outputLog);
	 	        OutputStream outBackupInputLog = new FileOutputStream(backupOutputLog);
	 	        
	 	        byte[] buf = new byte[1024];
	 	        int len;
	 	        
	 	        //write two files
	 	        while ((len = in.read(buf)) > 0) {
	 	        	out.write(buf, 0, len); 
	 	        	outBackupInputLog.write(buf, 0, len); 
	 	        }
	            String data = data(auxNameFile);
	
	            String commandToProxy = "newData";
	            Message newData = new Message(commandToProxy);
	            oos.writeObject(newData);
	
	            ObjectOutputStream oos1 = new ObjectOutputStream(socketToProxy.getOutputStream());

	            Message messageDataBase = new Message(dataBaseOutputLog);
	            Message messageData = new Message(data);
	            Message messageDate = new Message(timeUpdate);
	            
	            oos1.writeObject(messageDataBase);
	            oos1.writeObject(messageData);
	            oos1.writeObject(messageDate);
	          
	            System.out.println("\n===========================");
	            System.out.println("LAST CHANGE SENT TO PROXY -> ");
	            System.out.println("DATA: " + data);
	            System.out.println("DATE: " + timeUpdate);
	 	        
	 	        //Close files and sockets
	 	        in.close();
	 	        out.close();
	 	        outBackupInputLog.close();
	 	        socketToProxy.close();
	 	        
	    	}catch(IOException e) {
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(ERROR, path, Log.getStackTrace(e), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	 		}
        }  
	}
	
	public static String data(String nameFile) {
		String data = null;
		File file = null;
		if(nameFile.equals("writeInputs.txt")) {
			file = new File("./src/parking/filesOutput/SERVER3/inputLog.txt");
	    	data = new String();
	    	try {
	    		InputStreamReader streamReader =
	    		new InputStreamReader(new FileInputStream(file));
	    	  
	    		BufferedReader br = new BufferedReader(streamReader);
	    		System.out.println("\n===========================");
	    		System.out.println("READING FILE: " + file.getName());
	 
	    		while (br.ready()) {
	    			data = br.readLine();
	    		}
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(INFO, path, "SERVER 3 write data to file " + file.getName(), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	    		System.out.println("===========================");
	    	  
	    		br.close();
	    	  
	    	}catch(IOException e){
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(ERROR, path, Log.getStackTrace(e), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	    		
	    		file = new File("./src/parking/filesBackup/SERVER3/backupInputLog.txt");
	    		try {
	    			InputStreamReader streamReader =
		    	    new InputStreamReader(new FileInputStream(file));
		    	    	  
		    	    BufferedReader br = new BufferedReader(streamReader);
		    	    System.out.println("\n===========================");
		    	    System.out.println("READING FILE: " + file.getName());
		    	 
		    		while (br.ready()) {
		    			data = br.readLine();
		    		}
		    		time = ProxyClock.getError();
		    		
		    		logLock.lock();
		    		try {
			    		Log.log(INFO, path, "SERVER 3 write data to file " + file.getName(), className, time);
		    		}finally {
		    			logLock.unlock();
		    		}
		    		System.out.println("===========================");
		    	  
		    		br.close();
	    		}catch(IOException ex) {
	    			time = ProxyClock.getError();
	    			
	    			logLock.lock();
		    		try {
		    			Log.log(ERROR, path, Log.getStackTrace(ex), className, time);
		    		}finally {
		    			logLock.unlock();
		    		}
	    		}
	    	}
			
		}else {
			file = new File("./src/parking/filesOutput/SERVER3/outputLog.txt");
	    	data = new String();
	    	try {
	    		InputStreamReader streamReader =
	    		new InputStreamReader(new FileInputStream(file));
	    	  
	    		BufferedReader br = new BufferedReader(streamReader);
	    		System.out.println("\n===========================");
	    		System.out.println("READING FILE: " + file.getName());
	 
	    		while (br.ready()) {
	    			data = br.readLine();
	    		}
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(INFO, path, "SERVER 3 write data to file" + file.getName(), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	    		System.out.println("===========================");
	    	  
	    		br.close();
	    	  
	    	}catch(IOException e){
	    		time = ProxyClock.getError();
	    		
	    		logLock.lock();
	    		try {
		    		Log.log(ERROR, path, Log.getStackTrace(e), className, time);
	    		}finally {
	    			logLock.unlock();
	    		}
	    		
	    		file = new File("./src/parking/filesBackup/SERVER3/backupOutputLog.txt");
	    		try {
	    			InputStreamReader streamReader =
		    	    new InputStreamReader(new FileInputStream(file));
		    	    	  
		    	    BufferedReader br = new BufferedReader(streamReader);
		    	    System.out.println("\n===========================");
		    	    System.out.println("READING FILE: " + file.getName());
		    	 
		    		while (br.ready()) {
		    			data = br.readLine();
		    		}
		    		time = ProxyClock.getError();
		    		
		    		logLock.lock();
		    		try {
			    		Log.log(INFO, path, "SERVER 3 write data to file " + file.getName(), className, time);
		    		}finally {
		    			logLock.unlock();
		    		}
		    		System.out.println("===========================");
		    	  
		    		br.close();
	    		}catch(IOException ex) {
	    			time = ProxyClock.getError();
	    			
	    			logLock.lock();
		    		try {
		    			Log.log(ERROR, path, Log.getStackTrace(ex), className, time);
		    		}finally {
		    			logLock.unlock();
		    		}
	    		}
	    	}
		}
		return data;	
    }
}