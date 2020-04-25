package log;

import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;

public class Main {
	
	static ReentrantLock logLock = new ReentrantLock();
	
	static String path = "./src/server1.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	static long time;
	
	public static void main(String[] args) {

		for (int i = 0; i < 20000; i++) {
			
			Class thisClass = new Object(){}.getClass();
			className = thisClass.getEnclosingClass().getSimpleName();
			time = ProxyClock.getError();
			
			logLock.lock();
			try {
				Log.log(INFO, path, "SERVER 1 START", className, time);
			}finally {
				logLock.unlock();
			}
		}

	}

}
