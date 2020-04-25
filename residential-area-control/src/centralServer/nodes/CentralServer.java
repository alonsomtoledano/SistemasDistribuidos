package centralServer.nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;

import centralServer.services.database.AccessService;
import clock.ProxyClock;
import log.Log;
import publicAdministration.Message;

public class CentralServer {

	static int proxyRequestPort = 4005;
	static String proxyRequestHost = "127.0.0.1";

	static String path = "./src/centralServer/logs/server.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	// LOCKS
	static ReentrantLock logLock = new ReentrantLock();
	// CLOCK
	static ProxyClock clock = new ProxyClock();
	static long timeClock;

	public static void main(String[] args) {
		System.out.println("Node initialized");
		Class thisClass = new Object() {
		}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();

		timeClock = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, path, "SERVER START", className, timeClock);
		} finally {
			logLock.unlock();
		}
		// isResident("3");
		addPlate("pop", 1000);

		int serverPort = 4007;
		try {
			ServerSocket proxyRequest = new ServerSocket(serverPort);
			while (true) {
				Socket proxyAccept = proxyRequest.accept();
				ServerHandler threadServerAccept = new ServerHandler(proxyAccept);

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

	public static void addPlate(String plate, long time) {
		System.out.println("New vehicle: " + plate + " at " + time);
		try (Socket socketToProxy = new Socket(proxyRequestHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message command = new Message("newAccess");
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message plateToSend = new Message(plate);
			Message timeToSend = new Message(time);
			out1.writeObject(plateToSend);
			out1.writeObject(timeToSend);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to PROXYREQUEST", className, timeClock);
			} finally {
				logLock.unlock();
			}
			socketToProxy.close();
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
		}

	}

	public static void addSanction(String plate) {
		System.out.println("Sanction: " + plate);
		try (Socket socketToProxy = new Socket(proxyRequestHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message command = new Message("sanction");
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message plateToSend = new Message(plate);
			out1.writeObject(plateToSend);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to PROXYRESQUEST", className, timeClock);
			} finally {
				logLock.unlock();
			}
			socketToProxy.close();
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
		}
	}

	private static class ServerHandler implements Runnable {
		private final Socket socketToProxy;

		public ServerHandler(Socket socket) {
			this.socketToProxy = socket;
		}

		@Override
		public void run() {
			System.out.println("New thread");

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "SERVER RUN", className, timeClock);
			} finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream in = new ObjectInputStream(socketToProxy.getInputStream());
				Message command = (Message) in.readObject();

				if (command.getContent().equals("newAccess")) {
					doAddPlate();
				}
				if (command.getContent().equals("leaveParking")) {
					doLeaveParking();
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
				ObjectInputStream in = new ObjectInputStream(socketToProxy.getInputStream());
				Message response = (Message) in.readObject();
				Message plate = (Message) in.readObject();
				Message time = (Message) in.readObject();

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from ProxyResponse", className, timeClock);
				} finally {
					logLock.unlock();
				}
				AccessService psl = new AccessService();

				if (response.getContent().equals("yes")) {
					logLock.lock();
					try {
						Log.log(INFO, path, "Is resident", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Is resident");
					psl.addLicensePlate(plate.getContent(), time.getLongNumber(), "Yes");
					System.out.println("Info added to database");
					logLock.lock();
					try {
						Log.log(INFO, path, "Added info to database", className, timeClock);
					} finally {
						logLock.unlock();
					}
				} else {

					logLock.lock();
					try {
						Log.log(INFO, path, "Is not resident", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Is not resident");
					psl.addLicensePlate(plate.getContent(), time.getLongNumber(), "No");
					System.out.println("Info added to database");
					logLock.lock();
					try {
						Log.log(INFO, path, "Added info to database", className, timeClock);
					} finally {
						logLock.unlock();
					}
				}
				in.close();

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

		public void doLeaveParking() {
			try {
				ObjectInputStream in = new ObjectInputStream(socketToProxy.getInputStream());
				Message plate = (Message) in.readObject();
				Message time = (Message) in.readObject();

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from Proxy", className, timeClock);
				} finally {
					logLock.unlock();
				}
				AccessService psl = new AccessService();
				psl.leaveParking(plate.getContent(), time.getLongNumber());
				
				logLock.lock();
				try {
					Log.log(INFO, path, "Added info to database", className, timeClock);
				} finally {
					logLock.unlock();
				}
				in.close();
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
