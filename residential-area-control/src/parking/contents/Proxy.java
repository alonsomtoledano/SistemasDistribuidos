package parking.contents;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import cameraRing.Message;
import clock.ProxyClock;
import log.Log;
import parking.service.ProxyServiceLogin;
import parking.service.ProxyServiceWriteData;

public class Proxy {
	//PORTS
	static int server1Port = 5000;
//	static int centralNodePort = 5900; //PUERTO DEL NODO CENTRAL///////////////////////////////////////////////////////////////////////////////
	static int centralNodeBackUpPort = 4999; //PUERTO DEL NODO CENTRAL BACKUP///////////////////////////////////////////////////////////////////////////////
	static int centralNodePort = 4007;
	//IP
	static String serverHost = "127.0.0.1";
	//LOG VARIABLES
	static String path = "./src/parking/logs/proxy.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	//LOKS
	static ReentrantLock logLock = new ReentrantLock();
	//CLOCK
	static ProxyClock clock = new ProxyClock();
	static long time;
	
	public static void main (String[] args) {
		//CODE
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		System.out.println("PROXY START.....");
		
		time = ProxyClock.getError();
		
		logLock.lock();
		try {
			Log.log(INFO, path, "PROXY START", className, time);
		}finally {
			logLock.unlock();
		}
		int portProxy = 5100;
		
		try {
			time = ProxyClock.getError();
			ServerSocket server = new ServerSocket(portProxy);
			while(true) {
				Socket serverAccept = server.accept();
				ServerHandler threadServerAccept = new ServerHandler(serverAccept);
				
				new Thread(threadServerAccept).start();
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
	
	private static class ServerHandler implements Runnable {
		private final Socket socketFromServer;
		
		public ServerHandler(Socket socketFromServer) {
			this.socketFromServer = socketFromServer;
		}

		@Override
		public void run() {
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "new thread PROXY start", className, time);
			}finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(socketFromServer.getInputStream());
				Message messageSeverCommand = (Message)ois.readObject();
				
				
				String option = messageSeverCommand.getData();
				
	
				if (option.equals("authentication")) {
					Message messageSeverPort = (Message)ois.readObject();
					int serverPort = messageSeverPort.getNumber();
					authentication(serverPort);	
				}
				else if (option.equals("newData")) {;
					newData();
				}
			}catch(IOException e) {
				
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
		//THREAD FUNCTIONS
		public void newData() {
			ProxyServiceWriteData pswd = new ProxyServiceWriteData();
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "PROXY received update from SERVER", className, time);
			}finally {
				logLock.unlock();
			}
			
			try {
				
				ObjectInputStream ois = new ObjectInputStream(socketFromServer.getInputStream());
	            
				Message messageDataBase = (Message)ois.readObject();
				Message messageData = (Message)ois.readObject();
				Message messageDate = (Message)ois.readObject();
				
				String dataBase = messageDataBase.getData();
				String data = messageData.getData();
				long date = messageDate.getLongNumber();
				
				if(dataBase.equals("inputLog")) {
					pswd.writeInputLog(data, date);
					
				}else {
					try {
						Socket socketToCentralNode = new Socket(serverHost, centralNodePort);
						ObjectOutputStream oos = new ObjectOutputStream(socketToCentralNode.getOutputStream());
						pswd.writeOutputLog(data, date);
						String commandToProxy = "newData";
			            Message newData = new Message(commandToProxy);
			            oos.writeObject(newData);
			            
			            ObjectOutputStream oos1 = new ObjectOutputStream(socketToCentralNode.getOutputStream());
			            Message messageDataToCentralNode = new Message(data);
			            Message messageDateToCentralNode = new Message(date);
			            
			            oos1.writeObject(messageDataToCentralNode);
			            oos1.writeObject(messageDateToCentralNode);
			            
			            socketToCentralNode.close();
						
					}catch(IOException e) {
						logLock.lock();
						try {
							Log.log(ERROR, path, Log.getStackTrace(e), className, time);
						}finally {
							logLock.unlock();
						}
						
						Socket socketToCentralNode = new Socket(serverHost, centralNodeBackUpPort);
						ObjectOutputStream oos = new ObjectOutputStream(socketToCentralNode.getOutputStream());
						pswd.writeOutputLog(data, date);
						String commandToProxy = "newData";
			            Message newData = new Message(commandToProxy);
			            oos.writeObject(newData);
			            
			            ObjectOutputStream oos1 = new ObjectOutputStream(socketToCentralNode.getOutputStream());
			            Message messageDataToCentralNode = new Message(data);
			            Message messageDateToCentralNode = new Message(date);
			            
			            oos1.writeObject(messageDataToCentralNode);
			            oos1.writeObject(messageDateToCentralNode);
			            
			            socketToCentralNode.close();
					}
		            
				}
	
				logLock.lock();
				try {
					Log.log(INFO, path, "PROXY writting the changes in the DATABASE", className, time);
				}finally {
					logLock.unlock();
				}
				logLock.lock();
				try {
					Log.log(INFO, path, "***************************************", className, time);
				}finally {
					logLock.unlock();
				}
				
				System.out.println("\n====================================");
	    		System.out.println("WRITING THE CHANGES IN THE DATABASE -> ");
	    		System.out.println("====================================");

				System.out.println("DATA: " + data);
				System.out.println("DATE: " + date);				
				
			}catch(IOException e) {
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, time);
				}finally {
					logLock.unlock();
				}
			}catch (ClassNotFoundException e) {
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, time);
				}finally {
					logLock.unlock();
				}
			}
		}
		public void authentication(int serverPort) {
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "PROXY verifying SERVER authentication", className, time);
			}finally {
				logLock.unlock();
			}
			boolean status = false;
			ProxyServiceLogin psl = new ProxyServiceLogin();
			try {
				ObjectInputStream ois = new ObjectInputStream(socketFromServer.getInputStream());
				Message messageIP = (Message)ois.readObject();
				Message messageMAC = (Message)ois.readObject();
				
				String ip = messageIP.getData();
				String mac = messageMAC.getData();

				//call to data base
				status = psl.dataBaseAuthentication(ip, mac, serverPort);
			
				Socket socketToServer = new Socket(serverHost, serverPort);
	            ObjectOutputStream oos = new ObjectOutputStream(socketToServer.getOutputStream());
				Message messageStatus = new Message(status);
				
				//sent status to server
				oos.writeObject(messageStatus);
				
				if(status) {
					System.out.println("\nPROXY LISTENING TO EVENTS.....");
				}else {
					System.out.println("THE SERVER COULD NOT BE AUTHENTICATED");
				}
				time = ProxyClock.getError();
				
				logLock.lock();
				try {
					Log.log(INFO, path, "PROXY sent status to SERVER", className, time);
				}finally {
					logLock.unlock();
				}
				
				socketToServer.close();
			}catch(IOException e) {
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
}