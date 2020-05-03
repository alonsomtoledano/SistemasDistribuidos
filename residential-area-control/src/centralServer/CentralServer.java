package centralServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantLock;

import log.Log;
import cameraRing.Message;
import centralServer.services.database.AccessService;
import clock.ProxyClock;
import encryptation.Encryptation;

public class CentralServer {
	// PORTS
	static int puertoCameraRing = 6000;
	static int proxyRequestPort = 4005;

	// IP
	static String proxyRequestHost = "127.0.0.1";

	// NODE VARIABLES
	static boolean newLicensePlate = false;

	// LOG VARIBLES
	static String logPath = "./src/centralServer/logs/centralServer.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	static long time;

	// LOCKS
	static ReentrantLock logLock = new ReentrantLock();

	// THREADS
	static HandlerCameraRing handlerCameraRing = new HandlerCameraRing();
	static HandlerPublicAdministration handlerPublicAdministration = new HandlerPublicAdministration();

	public static void main(String[] args) {
		// LOG DATA
		Class thisClass = new Object() {
		}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();

		// THREADS
		new Thread(handlerCameraRing).start();
		new Thread(handlerPublicAdministration).start();
	}

	// THREAD FUNCTIONS
	public static void cameraRing() {
		time = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, logPath, "CameraRing thread started", className, time);
		} finally {
			logLock.unlock();
		}

		while (true) {
			try {
				ServerSocket socketCameraRing = new ServerSocket(puertoCameraRing);
				Socket sCameraRing;

				while ((sCameraRing = socketCameraRing.accept()) != null) {
					ObjectInputStream inputCameraRing = new ObjectInputStream(sCameraRing.getInputStream());

					time = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, logPath, "Message recieved", className, time);
					} finally {
						logLock.unlock();
					}

					try {
						Message message = (Message) inputCameraRing.readObject();

						Thread.sleep(2000);
						System.out.println("***********************************");
						System.out.println("MESSAGE RECEIVED");
						System.out.println("matriculasInLog: " + message.getMatriculasInLog());
						System.out.println("matriculasOutLog: " + message.getMatriculasOutLog());
						System.out.println("***********************************\n");

						if (!message.getMatriculasInLog().isEmpty()) {
							for (int i = 0; i < message.getMatriculasInLog().size(); i++) {
								addPlate(message.getMatriculasInLog().get(i).get(0),
										message.getMatriculasInLog().get(i).get(1),
										Long.parseLong(message.getMatriculasInLog().get(i).get(2)));
							}
							newLicensePlate = true;
						}

						if (!message.getMatriculasOutLog().isEmpty()) {
							for (int i = 0; i < message.getMatriculasOutLog().size(); i++) {
								vehicleExit(message.getMatriculasOutLog().get(i).get(0),
										Long.parseLong(message.getMatriculasOutLog().get(i).get(2)));

								if (checkSantioned(message.getMatriculasOutLog().get(i).get(0))) {
									addSanction(message.getMatriculasOutLog().get(i).get(0),
											message.getMatriculasOutLog().get(i).get(1),
											Long.parseLong(message.getMatriculasOutLog().get(i).get(2)));
								}
							}
							newLicensePlate = true;
						}

					} catch (ClassNotFoundException | InterruptedException e) {
						e.printStackTrace();
					}
				}
			} catch (IOException e) {
			}
		}
	}

	public static void publicAdministration() {
		int serverPort = 4007;
		try {

			ServerSocket proxyRequest = new ServerSocket(serverPort);
			while (true) {
				Socket proxyAccept = proxyRequest.accept();
				ServerHandler threadServerAccept = new ServerHandler(proxyAccept);

				new Thread(threadServerAccept).start();
			}

		} catch (IOException e) {
			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
				System.out.println("Exception. For more info visit " + logPath);
			} finally {
				logLock.unlock();
			}
		}
	}

	public static void addPlate(String plate, String image, long licensePlateTime) {
		System.out.println("New vehicle: " + plate + " at " + licensePlateTime);
		try (Socket socketToProxy = new Socket(proxyRequestHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message command = new Message("newAccess");
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message plateToSend = new Message(plate);
			Message timeToSend = new Message(licensePlateTime);
			Message imageToSend = new Message(image);
			out1.writeObject(plateToSend);
			out1.writeObject(timeToSend);
			out1.writeObject(imageToSend);

			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, logPath, "Data sent to PROXYREQUEST", className, time);
			} finally {
				logLock.unlock();
			}
			socketToProxy.close();
			out.close();
			out1.close();

		} catch (IOException e) {
			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
				System.out.println("Exception. For more info visit " + logPath);
			} finally {
				logLock.unlock();
			}
		}

	}

	public static void vehicleExit(String plate, long licensePlateTime) {
		AccessService psl = new AccessService();

		psl.vehicleExit(plate, licensePlateTime);
	}

	public static boolean checkSantioned(String plate) {
		AccessService psl = new AccessService();

		return psl.checkSantioned(plate);
	}

	public static void addSanction(String plate, String image, long date) {
		System.out.println("Sanction: " + plate);
		try (Socket socketToProxy = new Socket(proxyRequestHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message command = new Message("sanction");
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			Message plateToSend = new Message(plate);
			Message imageToSend = new Message(image);
			Message dateToSend = new Message(date);
			out1.writeObject(plateToSend);
			out1.writeObject(imageToSend);
			out1.writeObject(dateToSend);

			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, logPath, "Data sent to PROXYRESQUEST", className, time);
			} finally {
				logLock.unlock();
			}
			socketToProxy.close();
			out.close();
			out1.close();

		} catch (IOException e) {
			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
				System.out.println("Exception. For more info visit " + logPath);
			} finally {
				logLock.unlock();
			}
		}
	}

	// THREAD HANDLERS
	private static class HandlerCameraRing implements Runnable {
		@Override
		public void run() {
			cameraRing();
		}
	}

	private static class HandlerPublicAdministration implements Runnable {
		@Override
		public void run() {
			publicAdministration();
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

			time = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, logPath, "SERVER RUN", className, time);
			} finally {
				logLock.unlock();
			}
			try {
				ObjectInputStream in = new ObjectInputStream(socketToProxy.getInputStream());
				Message command = (Message) in.readObject();

				if (command.getContent().equals("newAccess")) {
					doAddPlate();
				}
				if (command.getContent().equals("newData")) {
					doLeaveParking();
				}

			} catch (IOException | ClassNotFoundException e) {
				time = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
					System.out.println("Exception. For more info visit " + logPath);
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
				Message licensePlateTime = (Message) in.readObject();
				Message image = (Message) in.readObject();

				time = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, logPath, "Received data from ProxyResponse", className, time);
				} finally {
					logLock.unlock();
				}
				AccessService psl = new AccessService();

				if (response.getContent().equals("yes")) {
					logLock.lock();
					try {
						Log.log(INFO, logPath, "Is resident", className, time);
					} finally {
						logLock.unlock();
					}
					System.out.println("Is resident");
					psl.addLicensePlate(plate.getContent(), image.getContent(), licensePlateTime.getLongNumber(),
							"Yes");
					System.out.println("Info added to database\n");
					logLock.lock();
					try {
						Log.log(INFO, logPath, "Added info to database", className, time);
					} finally {
						logLock.unlock();
					}
				} else {

					logLock.lock();
					try {
						Log.log(INFO, logPath, "Is not resident", className, time);
					} finally {
						logLock.unlock();
					}
					System.out.println("Is not resident");
					psl.addLicensePlate(plate.getContent(), image.getContent(), licensePlateTime.getLongNumber(), "No");
					System.out.println("Info added to database\n");
					logLock.lock();
					try {
						Log.log(INFO, logPath, "Added info to database", className, time);
					} finally {
						logLock.unlock();
					}
				}
				in.close();

			} catch (IOException | ClassNotFoundException e) {
				time = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
					System.out.println("Exception. For more info visit " + logPath);
				} finally {
					logLock.unlock();
				}

			}
		}

		public void doLeaveParking() {
			try {
				ObjectInputStream in = new ObjectInputStream(socketToProxy.getInputStream());

				time = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, logPath, "Received data from Proxy", className, time);
				} finally {
					logLock.unlock();
				}
				AccessService psl = new AccessService();

				Message messageDataFromProxy = (Message) in.readObject();
				Message messageDateFromProxy = (Message) in.readObject();

				String data = messageDataFromProxy.getData();
				long date = messageDateFromProxy.getLongNumber();

				Date d = new Date(date);

				System.out.println("Data from proxy: " + data);
				System.out.println("Date milis from proxy: " + date);
				System.out.println("Date: " + d);

				data = Encryptation.Encrypt(data);

				psl.leaveParking(data, date);

				logLock.lock();
				try {
					Log.log(INFO, logPath, "Added info to database", className, time);
				} finally {
					logLock.unlock();
				}
				in.close();
			} catch (IOException | ClassNotFoundException e) {
				time = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, logPath, Log.getStackTrace(e), className, time);
					System.out.println("Exception. For more info visit " + logPath);
				} finally {
					logLock.unlock();
				}
			}
		}
	}
}
