package publicAdministration.nodes;

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

	static int adminServerPort = 4003;
	static String adminServerHost = "127.0.0.1";

	static String path = "./src/publicAdministration/logs/proxyRequest.log";
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
		int proxyRequestPort = 4009;
		try {
			ServerSocket client = new ServerSocket(proxyRequestPort);
			while (true) {
				Socket clientAccept = client.accept();
				ClientHandler threadServerAccept = new ClientHandler(clientAccept);

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

	private static class ClientHandler implements Runnable {
		private final Socket socketFromClient;

		public ClientHandler(Socket socket) {
			this.socketFromClient = socket;
		}

		public void run() {
			System.out.println("New thread");
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "PROXY START", className, timeClock);
			} finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
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
					Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}

		}

		public void doAddResident() {
			try {
				// From Client
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
				Message port = (Message) in.readObject();
				Message dni = (Message) in.readObject();
				Message name = (Message) in.readObject();
				Message surname = (Message) in.readObject();
				Message plate = (Message) in.readObject();

				System.out.println("Add resident. Plate: " + plate.getContent());

				String encryptedDni = Encryptation.Encrypt(dni.getContent());
				String encryptedPlate = Encryptation.Encrypt(plate.getContent());

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client and DNI and license plate encrypted", className,
							timeClock);
				} finally {
					logLock.unlock();
				}

				Message dniMsg = new Message(encryptedDni);
				Message plateMsg = new Message(encryptedPlate);

				// To Server
				Socket socketToServer = new Socket(adminServerHost, adminServerPort);

				ObjectOutputStream out = new ObjectOutputStream(socketToServer.getOutputStream());
				Message com = new Message("addResident");
				out.writeObject(com);

				out.writeObject(dniMsg);
				out.writeObject(name);
				out.writeObject(surname);
				out.writeObject(plateMsg);
				out.writeObject(port);
				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToServer.close();
				in.close();
				out.close();

			} catch (IOException | ClassNotFoundException e) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(e), className, timeClock);
					System.out.println("Data sent to server" + path);
				} finally {
					logLock.unlock();
				}
			}
		}

		public void doDeleteResident() {

			String proxyHost = "127.0.0.1";
			try {
				// From Client
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
				Message port = (Message) in.readObject();
				Message dni = (Message) in.readObject();
				System.out.println("Delete resident: " + dni.getContent());
				String encryptedDni = Encryptation.Encrypt(dni.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client and DNI encrypted", className, timeClock);
				} finally {
					logLock.unlock();
				}

				Message dniMsg = new Message(encryptedDni);

				// To Server
				Socket socketToServer = new Socket(adminServerHost, adminServerPort);

				ObjectOutputStream out = new ObjectOutputStream(socketToServer.getOutputStream());
				Message com = new Message("deleteResident");
				out.writeObject(com);
				out.writeObject(dniMsg);
				out.writeObject(port);
				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToServer.close();
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

		public void doFindSanctioned() {

			String proxyHost = "127.0.0.1";
			try {
				// From Client
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
				Message port = (Message) in.readObject();
				Message plate = (Message) in.readObject();

				String encryptedPlate = Encryptation.Encrypt(plate.getContent());

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client and license plate encrypted", className, timeClock);
				} finally {
					logLock.unlock();
				}

				Message plateMsg = new Message(encryptedPlate);

				// To Server
				Socket socketToServer = new Socket(adminServerHost, adminServerPort);

				ObjectOutputStream out = new ObjectOutputStream(socketToServer.getOutputStream());
				Message com = new Message("findSanctioned");
				out.writeObject(com);
				out.writeObject(plateMsg);
				out.writeObject(port);

				logLock.lock();
				try {
					Log.log(INFO, path, "Data sent to server", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToServer.close();
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