package parking.contents;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import log.Log;

public class CentralNodeBackUp {
	//PORTS
	static int proxyPort = 5100;
	//LOG VARIABLES
	static String serverHost = "127.0.0.1";
	static String path = "./src/parking/logs/nodeCentralBackUp.log";
	static String INFO = "info"; 
	static String ERROR = "error";
	static String className;
	//LOKS
	static ReentrantLock logLock = new ReentrantLock();
	//CLOCK
	static ProxyClock clock = new ProxyClock();
	static long time;
		
	public static void main(String[] args) {
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		
		time = ProxyClock.getError();
		
		logLock.lock();
		try {
			Log.log(INFO, path, "NODE CENTRAL start", className, time);
		}finally {
			logLock.unlock();
		}
		
		int centralNodePort = 5901;
		
		try {
			time = ProxyClock.getError();
			ServerSocket cNode = new ServerSocket(centralNodePort);
			while(true) {
				Socket centralNodeAccept = cNode.accept();
				NodeCentralHandler threadProxyAccept = new NodeCentralHandler(centralNodeAccept);
				
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
	
	private static class NodeCentralHandler implements Runnable {
		private final Socket socketFromProxy;
		
		public NodeCentralHandler(Socket socketFromProxy) {
			this.socketFromProxy = socketFromProxy;
		}
		@Override
		public void run() {
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "new thread CENTRAL NODE BACK UP start",className , time);
			}finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(socketFromProxy.getInputStream());
				Message severCommand = (Message)ois.readObject();
				
				String option = severCommand.getData();
	
				if (option.equals("newData")) {
					dateFromParkin();	
				}
				
			}catch (IOException e) {
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
		
		public void dateFromParkin() {
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "received new data from PARKING",className , time);
			}finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream ois = new ObjectInputStream(socketFromProxy.getInputStream());
				Message messageDataFromProxy = (Message)ois.readObject();
				Message messageDateFromProxy = (Message)ois.readObject();
				
				String data = messageDataFromProxy.getData();
				long date = messageDateFromProxy.getLongNumber();
				
				Date d = new Date(date);
				
				System.out.println("Data from proxy: " + data);
				System.out.println("Date milis from proxy: " + date);
				System.out.println("Date: " + d);
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
	}
}
