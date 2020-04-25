package serverCorba;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

import log.Log;
import clock.ProxyClock;

public class ServerCorba {
	
	public static void main(String[] args) {
	    //LOG VARIBLES
	    String logPath = "./src/cameraRing/logs/serverCorba.log";
	    String INFO = "info";
	    String ERROR = "error";
	    String className;
	    long time;
	    
	    //LOCKS
	    ReentrantLock logLock = new ReentrantLock();
		
	    //LOG DATA
		Class thisClass = new Object(){}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();
		
		//CODE
		try {
			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, logPath, "Server Corba started", className, time);
			}finally {
				logLock.unlock();
			}
			
			Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/serverCorba.bat");
			
			System.out.println("SERVER CORBA LISTENING...");
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true);
	}
}