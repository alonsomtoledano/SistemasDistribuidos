package publicAdministration.nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import encryptation.Encryptation;
import log.Log;
import cameraRing.Message;
import publicAdministration.database.CitizensService;

public class AdministrationServer {

	static int centralProxyRequest = 4005;
	static int centralProxyResponse = 4006;
	static String centralProxyResponseHost = "127.0.0.1";
	static String centralProxyRequestHost = "127.0.0.1";

	static int adminProxyRequest = 4009;
	static int adminProxyResponse = 4010;
	static String adminProxyResponseHost = "127.0.0.1";
	static String adminProxyRequestHost = "127.0.0.1";

	static String path = "./src/publicAdministration/logs/server.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	// LOCKS
	static ReentrantLock logLock = new ReentrantLock();
	// CLOCK
	static ProxyClock clock = new ProxyClock();
	static long timeClock;

	public static void main(String[] args) {
		System.out.println("Server initialized");
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
		int serverPortAdmin = 4003; // Para comunicación con administración
		int serverPortCentral = 4011; // Para comunicación con el server central
		ServerAdminHandler threadServerAdmin = new ServerAdminHandler(serverPortAdmin);
		new Thread(threadServerAdmin).start();

		ServerCentralHandler threadServerCentral = new ServerCentralHandler(serverPortCentral);
		new Thread(threadServerCentral).start();

	}

	private static class ServerAdminHandler implements Runnable {
		private int serverPortAdmin;

		public ServerAdminHandler(int serverPortAdmin) {
			this.serverPortAdmin = serverPortAdmin;
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
			while (true) {
				try {
					ServerSocket proxyAdmin = new ServerSocket(serverPortAdmin);
					Socket proxyAdminAccept;
					while ((proxyAdminAccept = proxyAdmin.accept()) != null) {
						ObjectInputStream in = new ObjectInputStream(proxyAdminAccept.getInputStream());
						Message clientCommand = (Message) in.readObject();

						// Administration
						if (clientCommand.getContent().equals("addResident")) {
							Message dni = (Message) in.readObject();
							Message name = (Message) in.readObject();
							Message surname = (Message) in.readObject();
							Message plate = (Message) in.readObject();
							Message port = (Message) in.readObject();
							doAddResident(dni.getContent(), name.getContent(), surname.getContent(), plate.getContent(),
									port.getNumber());
						} else if (clientCommand.getContent().equals("deleteResident")) {
							Message dni = (Message) in.readObject();
							Message port = (Message) in.readObject();
							doDeleteResident(dni.getContent(), port.getNumber());
						}

						else if (clientCommand.getContent().equals("findSanctioned")) {
							Message plate = (Message) in.readObject();
							Message port = (Message) in.readObject();
							doFindSanctioned(plate.getContent(), port.getNumber());
						}

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

		public void doAddResident(String dni, String name, String surname, String plate, int port) {

			try {

				Socket socketToProxyResponse = new Socket(adminProxyResponseHost, adminProxyResponse);

				ObjectOutputStream out = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				Message comm = new Message("addResident");
				out.writeObject(comm);

				ObjectOutputStream out1 = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				CitizensService psl = new CitizensService();
				Message portMsg = new Message(port);
				if (!psl.addResident(dni, plate, name, surname)) {
					System.out.println("Resident");
					Message ok = new Message("ok");
					out1.writeObject(ok);
					out1.writeObject(portMsg);
				} else {
					System.out.println("Could not add resident");
					Message no = new Message("notOk");
					out1.writeObject(no);
					out1.writeObject(portMsg);
				}

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Data base accessed and sent response to proxy", className, timeClock);

				} finally {
					logLock.unlock();
				}
				socketToProxyResponse.close();
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

		public void doDeleteResident(String dni, int port) {
			try {
				Socket socketToProxyResponse = new Socket(adminProxyResponseHost, adminProxyResponse);
				ObjectOutputStream out = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				Message comm = new Message("deleteResident");
				out.writeObject(comm);
				ObjectOutputStream out1 = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				CitizensService psl = new CitizensService();
				Message portMsg = new Message(port);
				if (psl.deleteResident(dni)) {
					System.out.println("Resident deleted");
					Message ok = new Message("ok");
					out1.writeObject(ok);
					out1.writeObject(portMsg);
				} else {
					System.out.println("Could not delete resident");
					Message no = new Message("notOk");
					out1.writeObject(no);
					out1.writeObject(portMsg);
				}

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Data base accessed and sent response to proxy", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToProxyResponse.close();
				out.close();
				out1.close();

			} catch (Exception e) {
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

		public void doFindSanctioned(String plate, int port) {
			System.out.println("Find Sanctioned");
			List<String> found = new ArrayList<String>();
			CitizensService psl = new CitizensService();
			found = psl.findSanctioned(plate);
			String arr[] = new String[found.size()];

			try {
				// Get and decrypt the license plate
				for (int i = 0; i < found.size(); i++) {
					int j = found.get(i).indexOf(' ');
					String word = found.get(i).substring(0, j);
					String rest = found.get(i).substring(j);
					String decryptedMessage = Encryptation.Decrypt(word);
					arr[i] = decryptedMessage + rest;
				}

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received and decrypted license plate", className, timeClock);
				} finally {
					logLock.unlock();
				}
				Socket socketToProxyResponse = new Socket(adminProxyResponseHost, adminProxyResponse);

				ObjectOutputStream out = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				Message comm = new Message("findSanctioned");
				out.writeObject(comm);
				Message portMsg = new Message(port);
				ObjectOutputStream out1 = new ObjectOutputStream(socketToProxyResponse.getOutputStream());
				if (arr.length != 0) {
					Message result = new Message(arr);
					out1.writeObject(result);
					out1.writeObject(portMsg);
				} else {
					String aux[] = null;
					Message result = new Message(aux);
					out1.writeObject(result);
					out1.writeObject(portMsg);
				}

				logLock.lock();
				try {
					Log.log(INFO, path, "Data base accessed and sent response to proxy", className, timeClock);
				} finally {
					logLock.unlock();
				}
				socketToProxyResponse.close();
				out.close();
				out1.close();

			} catch (Exception e) {
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

	private static class ServerCentralHandler implements Runnable {
		private int serverPortCentral;

		public ServerCentralHandler(int serverPortCentral) {
			this.serverPortCentral = serverPortCentral;
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

			while (true) {
				try {
					ServerSocket proxyCentral = new ServerSocket(serverPortCentral);
					Socket proxyCentralAccept;
					while ((proxyCentralAccept = proxyCentral.accept()) != null) {
						ObjectInputStream in = new ObjectInputStream(proxyCentralAccept.getInputStream());
						Message clientCommand = (Message) in.readObject();
						if (clientCommand.getContent().equals("newAccess")) {

							Message plate = (Message) in.readObject();
							Message time = (Message) in.readObject();
							Message image = (Message) in.readObject();
							doAddPlate(plate.getContent(), image.getContent(), time.getLongNumber());
						} else if (clientCommand.getContent().equals("sanction")) {
							Message plate = (Message) in.readObject();
							Message image = (Message) in.readObject();
							Message date = (Message) in.readObject();
							doAddSanction(plate.getContent(), image.getContent(), date.getLongNumber());
						}
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

		public void doAddPlate(String plate, String image, long time) {
			CitizensService psl = new CitizensService();
			System.out.println("Add plate: " + plate);
			try {
				Message plateMsg = new Message(plate);
				Socket socketToproxyResponse = new Socket(centralProxyResponseHost, centralProxyResponse);
				ObjectOutputStream out = new ObjectOutputStream(socketToproxyResponse.getOutputStream());
				Message command = new Message("newAccess");
				out.writeObject(command);
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Command sent to proxy response", className, timeClock);
				} finally {
					logLock.unlock();
				}

				Message timeMsg = new Message(time);
				Message imageMsg = new Message(image);
				ObjectOutputStream out1 = new ObjectOutputStream(socketToproxyResponse.getOutputStream());
				if (psl.findResident(plate)) {
					Message result = new Message("yes");

					logLock.lock();
					try {
						Log.log(INFO, path, "Is resident", className, timeClock);
					} finally {
						logLock.unlock();
					}
					out1.writeObject(result);
					out1.writeObject(plateMsg);
					out1.writeObject(timeMsg);
					out1.writeObject(imageMsg);
				} else {
					Message result = new Message("no");
					logLock.lock();
					try {
						Log.log(INFO, path, "Is not resident", className, timeClock);
					} finally {
						logLock.unlock();
					}
					out1.writeObject(result);
					out1.writeObject(plateMsg);
					out1.writeObject(timeMsg);
					out1.writeObject(imageMsg);
				}
				logLock.lock();
				try {
					Log.log(INFO, path, "Data base accessed and sent response to proxy response", className, timeClock);
				} finally {
					logLock.unlock();
				}

				socketToproxyResponse.close();
				out.close();
				out1.close();

			} catch (Exception e) {
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

		public void doAddSanction(String plate, String image, long date) {
			System.out.println("Add Sanction: " + plate);
			CitizensService psl = new CitizensService();
			psl.addSanction(plate, image, date);
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data base accessed and added sanction", className, timeClock);
			} finally {
				logLock.unlock();
			}
		}

	}

}