package centralServer.nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import log.Log;
import cameraRing.Message;

public class ProxyResponse {
	static int centralServerPort = 4007;
	static int auxCentralServerPort = 4999;
	static String centralServerHost = "127.0.0.1";

	static String path = "./src/centralServer/logs/proxyResponse.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	// LOCKS
	static ReentrantLock logLock = new ReentrantLock();
	// CLOCK
	static ProxyClock clock = new ProxyClock();
	static long timeClock;

	public static void main(String[] args) {
		System.out.println("Proxy initialized");
		Class thisClass = new Object() {
		}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();

		timeClock = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, path, "PROXY START", className, timeClock);
		} finally {
			logLock.unlock();
		}

		int proxyResponsePort = 4006;
		String proxyResponseHost = "127.0.0.1";

		try {
			ServerSocket centralServer = new ServerSocket(proxyResponsePort);
			while (true) {
				Socket serverAccept = centralServer.accept();
				ProxyHandler threadServerAccept = new ProxyHandler(serverAccept);

				new Thread(threadServerAccept).start();
			}
		} catch (IOException e) {
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
				System.out.println("Exception. For more info visit " + path);
			} finally {
				logLock.unlock();
			}
		}
	}

	private static class ProxyHandler implements Runnable {
		private final Socket socketFromServer;

		public ProxyHandler(Socket socket) {
			this.socketFromServer = socket;
		}

		public void run() {
			System.out.println("New thread");
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "PROXY RUN", className, timeClock);
			} finally {
				logLock.unlock();
			}

			try {
				ObjectInputStream in = new ObjectInputStream(socketFromServer.getInputStream());
				Message command = (Message) in.readObject();

				if (command.getContent().equals("newAccess")) {
					doAddPlate();
				}

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}
		}

		public void doAddPlate() {
			try {
				ObjectInputStream in = new ObjectInputStream(socketFromServer.getInputStream());
				Message response = (Message) in.readObject();
				Message plate = (Message) in.readObject();
				Message time = (Message) in.readObject();
				Message image = (Message) in.readObject();
				System.out.println("Add plate: " + plate.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received plate from administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				try {
					Socket socketToCentralServer = new Socket(centralServerHost, centralServerPort);
					ObjectOutputStream out = new ObjectOutputStream(socketToCentralServer.getOutputStream());
					Message command = new Message("newAccess");
					out.writeObject(command);

					ObjectOutputStream out1 = new ObjectOutputStream(socketToCentralServer.getOutputStream());
					out1.writeObject(response);
					out1.writeObject(plate);
					out1.writeObject(time);
					out1.writeObject(image);
					logLock.lock();
					try {
						Log.log(INFO, path, "Data sent to central server", className, timeClock);
					} finally {
						logLock.unlock();
					}
					socketToCentralServer.close();
					in.close();
					out.close();
					out1.close();
				} catch (IOException e) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
						System.out.println("Exception. For more info visit " + path);
					} finally {
						logLock.unlock();
					}
					Socket socketToCentralServer = new Socket(centralServerHost, auxCentralServerPort);
					ObjectOutputStream out = new ObjectOutputStream(socketToCentralServer.getOutputStream());
					Message command = new Message("newAccess");
					out.writeObject(command);

					ObjectOutputStream out1 = new ObjectOutputStream(socketToCentralServer.getOutputStream());
					out1.writeObject(response);
					out1.writeObject(plate);
					out1.writeObject(time);
					out1.writeObject(time);
					logLock.lock();
					try {
						Log.log(INFO, path, "Data sent to central server", className, timeClock);
					} finally {
						logLock.unlock();
					}
					socketToCentralServer.close();
					in.close();
					out.close();
					out1.close();
				}

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}

		}

	}
}
