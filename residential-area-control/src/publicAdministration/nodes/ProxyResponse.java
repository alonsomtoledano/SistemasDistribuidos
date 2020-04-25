package publicAdministration.nodes;

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
	static String clientHost = "127.0.0.1";
	static String path = "./src/publicAdministration/logs/proxyResponse.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String WARNING = "warning";
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
		int proxyResponsePort = 4010;

		try {
			ServerSocket client = new ServerSocket(proxyResponsePort);
			while (true) {
				Socket clientAccept = client.accept();
				ClientHandler threadServerAccept = new ClientHandler(clientAccept);

				new Thread(threadServerAccept).start();
			}
		} catch (IOException e) {
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, Log.getStackTrace(e), className, timeClock);
				System.out.println("Exception. For more info visit " + path);
			} finally {
				logLock.unlock();
			}
		}
	}

	private static class ClientHandler implements Runnable {
		private final Socket socketFromServer;

		public ClientHandler(Socket socket) {
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
				Message clientCommand = (Message) in.readObject();

				if (clientCommand.getContent().equals("addResident")) {
					doAddResident();

				} else if (clientCommand.getContent().equals("deleteResident")) {
					doDeleteResident();
				} else if (clientCommand.getContent().equals("findSanctioned")) {
					doFindSanctioned();
				}

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}

		}

		public void doAddResident() {
			System.out.println("Add Resident");
			try {
				// From Server
				ObjectInputStream in1 = new ObjectInputStream(socketFromServer.getInputStream());
				Message opt = (Message) in1.readObject();
				Message port = (Message) in1.readObject();
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}

				// To Client
				Message com = new Message("addResident");
				Socket socketToCLient = new Socket(clientHost, port.getNumber());
				ObjectOutputStream out2 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out2.writeObject(com);
				ObjectOutputStream out3 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out3.writeObject(opt);
				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to client", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToCLient.close();
				in1.close();
				out2.close();
				out3.close();

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}
		}

		public void doDeleteResident() {
			System.out.println("Delete Resident");
			String proxyHost = "127.0.0.1";
			try {
				// From Server
				ObjectInputStream in1 = new ObjectInputStream(socketFromServer.getInputStream());
				Message opt = (Message) in1.readObject();
				Message port = (Message) in1.readObject();
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}

				// To Client
				Message com = new Message("deleteResident");
				Socket socketToCLient = new Socket(proxyHost, port.getNumber());
				ObjectOutputStream out2 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out2.writeObject(com);
				ObjectOutputStream out3 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out3.writeObject(opt);

				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to client", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToCLient.close();
				in1.close();
				out2.close();
				out3.close();

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}

		}

		public void doFindSanctioned() {
			System.out.println("Find Sanctioned");
			String proxyHost = "127.0.0.1";
			try {
				// From Server
				ObjectInputStream in1 = new ObjectInputStream(socketFromServer.getInputStream());
				Message result = (Message) in1.readObject();
				Message port = (Message) in1.readObject();
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from administration server", className, timeClock);
				} finally {
					logLock.unlock();
				}

				// To Client
				Message com = new Message("findSanctioned");
				Socket socketToCLient = new Socket(proxyHost, port.getNumber());
				ObjectOutputStream out2 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out2.writeObject(com);
				ObjectOutputStream out3 = new ObjectOutputStream(socketToCLient.getOutputStream());
				out3.writeObject(result);

				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to client", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToCLient.close();
				in1.close();
				out2.close();
				out3.close();

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}

		}

	}

}