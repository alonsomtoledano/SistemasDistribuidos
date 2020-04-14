package serverCorba;

import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

import cameraRing.Log;

public class ServerCorba {
	
	public static void main(String[] args) {
	    //LOG VARIBLES
	    String logPath = "./src/cameraRing/serverCorba.log";
	    
	    //LOCKS
	    ReentrantLock logLock = new ReentrantLock();
		
	    //CODE
		logLock.lock();
		
		try {
			Process proc = Runtime.getRuntime().exec("./src/cameraRing/corbaMatriculasDetector/serverCorba.bat");
			
			try {
				Log.log("info", logPath, "Server Corba started");
			} finally {
				logLock.unlock();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while(true);
	}
}