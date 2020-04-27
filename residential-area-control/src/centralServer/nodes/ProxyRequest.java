package centralServer.nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import encryptation.Encryptation;
import log.Log;
import cameraRing.Message;

public class ProxyRequest {

	static int adminServerPort = 4011;
	static String adminServerHost = "127.0.0.1";

	static String path = "./src/centralServer/logs/proxyRequest.log";
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

		int proxyRequestPort = 4005;

		try {
			ServerSocket adminServer = new ServerSocket(proxyRequestPort);
			while (true) {
				Socket serverAccept = adminServer.accept();
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
				Message serverCommand = (Message) in.readObject();

				if (serverCommand.getContent().equals("newAccess")) {
					doAddPlate();
				} else if (serverCommand.getContent().equals("sanction")) {
					doAddSanction();
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

			} catch (ClassNotFoundException e) {
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
				Message plate = (Message) in.readObject();
				Message time = (Message) in.readObject();
				Message image = (Message) in.readObject();
				System.out.println("Add plate: " + plate.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received plate from central server", className, timeClock);
				} finally {
					logLock.unlock();
				}

				String encryptedPlate = Encryptation.Encrypt(plate.getContent());

				logLock.lock();
				try {
					Log.log(INFO, path, "Plate encrypted", className, timeClock);
				} finally {
					logLock.unlock();
				}

				Message encryptedPlateMessage = new Message(encryptedPlate);
				Socket socketToAdminServer = new Socket(adminServerHost, adminServerPort);
				ObjectOutputStream out = new ObjectOutputStream(socketToAdminServer.getOutputStream());
				Message command = new Message("newAccess");
				out.writeObject(command);
				out.writeObject(encryptedPlateMessage);
				out.writeObject(time);
				out.writeObject(image);

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToAdminServer.close();
				in.close();
				out.close();
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

		public void doAddSanction() {
			try {
				ObjectInputStream in = new ObjectInputStream(socketFromServer.getInputStream());
				Message plate = (Message) in.readObject();
				Message image = (Message) in.readObject();
				Message date = (Message) in.readObject();
				System.out.println("Add sanction: " + plate.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received plate from central server", className, timeClock);
				} finally {
					logLock.unlock();
				}

				String encryptedPlate = Encryptation.Encrypt(plate.getContent());

				logLock.lock();
				try {
					Log.log(INFO, path, "Plate encrypted", className, timeClock);
				} finally {
					logLock.unlock();
				}

				Message encryptedPlateMessage = new Message(encryptedPlate);
				Socket socketToAdminServer = new Socket(adminServerHost, adminServerPort);
				ObjectOutputStream out = new ObjectOutputStream(socketToAdminServer.getOutputStream());
				Message command = new Message("sanction");
				out.writeObject(command);
				out.writeObject(encryptedPlateMessage);
				out.writeObject(image);
				out.writeObject(date);

				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToAdminServer.close();
				in.close();
				out.close();
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
