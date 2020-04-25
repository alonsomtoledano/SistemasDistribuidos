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
import publicAdministration.database.UsersService;

public class ProxyLogin {
	static String clientHost = "127.0.0.1";
	static String path = "./src/publicAdministration/logs/proxyLogin.log";
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

		int proxyLoginPort = 4008;

		try {
			ServerSocket client = new ServerSocket(proxyLoginPort);
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
		private String dniUser;

		public ClientHandler(Socket socket) {
			this.socketFromClient = socket;
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
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
				Message clientCommand = (Message) in.readObject();

				if (clientCommand.getContent().equals("signUp")) {
					doSignUp();
				} else if (clientCommand.getContent().equals("login")) {
					dniUser = doLogin();
				} else if (clientCommand.getContent().equals("logout")) {
					doLogout(dniUser);

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

		public void doSignUp() {
			try {

				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());
				Message port = (Message) in.readObject();
				Message user = (Message) in.readObject();
				Message password = (Message) in.readObject();
				Message dni = (Message) in.readObject();

				String encryptedPassword = Encryptation.Encrypt(password.getContent());
				String encryptedDni = Encryptation.Encrypt(dni.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client and password and DNI encrypted", className,
							timeClock);

				} finally {
					logLock.unlock();
				}

				UsersService psl = new UsersService();
				Socket socketToCLient = new Socket(clientHost, port.getNumber());
				ObjectOutputStream out = new ObjectOutputStream(socketToCLient.getOutputStream());
				Message command = new Message("signUp");
				out.writeObject(command);

				ObjectOutputStream out1 = new ObjectOutputStream(socketToCLient.getOutputStream());

				if (!psl.signUpUser(user.getContent(), encryptedPassword, encryptedDni)) {
					System.out.println("Sign up correct");
					Message ok = new Message("ok");
					out1.writeObject(ok);
				} else {
					Message no = new Message("DNI already in use");
					out1.writeObject(no);
				}

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Data base accessed and sent response to client", className, timeClock);

				} finally {
					logLock.unlock();
				}

				socketToCLient.close();
				in.close();
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

		public String doLogin() {
			String dni = null;
			boolean loginOk = false;
			try {
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());

				Message port = (Message) in.readObject();
				Message user = (Message) in.readObject();
				Message password = (Message) in.readObject();

				String encryptedPassword = Encryptation.Encrypt(password.getContent());
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client and password encrypted", className, timeClock);

				} finally {
					logLock.unlock();
				}
				Socket socketToCLient = new Socket(clientHost, port.getNumber());
				ObjectOutputStream out = new ObjectOutputStream(socketToCLient.getOutputStream());
				Message com = new Message("login");
				out.writeObject(com);

				ObjectOutputStream out1 = new ObjectOutputStream(socketToCLient.getOutputStream());
				UsersService psl = new UsersService();
				dni = psl.loginUser(user.getContent(), encryptedPassword);

				if (dni.equals("1")) {
					System.out.println("Incorrect password");
					Message pwd = new Message("password");
					out1.writeObject(pwd);
				} else if (dni.equals("2")) {
					System.out.println("Incorrect username");
					Message username = new Message("username");
					out1.writeObject(username);
				} else {
					loginOk = true;
					if (psl.getStatus(dni).equals("0")) {
						psl.changeStatus(dni, "1");
						System.out.println("User connected: " + user.getContent());
						Message ok = new Message("ok");
						out1.writeObject(ok);
					} else if (psl.getStatus(dni).equals("1")) {
						System.out.println("User aleady logged");
						Message status = new Message("status");
						out1.writeObject(status);
					}

					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "Data base accessed and sent response to client", className, timeClock);

					} finally {
						logLock.unlock();
					}
				}

				socketToCLient.close();
				in.close();
				out.close();
				out1.close();

			} catch (Exception ex) {
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(ERROR, path, Log.getStackTrace(ex), className, timeClock);
					System.out.println("Exception. For more info visit " + path);
				} finally {
					logLock.unlock();
				}
			}
			return dni;
		}

		public void doLogout(String dni) {
			UsersService psl = new UsersService();
			psl.changeStatus(dni, "0");
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Status canged", className, timeClock);

			} finally {
				logLock.unlock();
			}

			try {
				ObjectInputStream in = new ObjectInputStream(socketFromClient.getInputStream());

				Message port = (Message) in.readObject();
				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Received data from client", className, timeClock);

				} finally {
					logLock.unlock();
				}

				Socket socketToCLient = new Socket(clientHost, port.getNumber());
				ObjectOutputStream out = new ObjectOutputStream(socketToCLient.getOutputStream());
				Message com = new Message("logout");
				out.writeObject(com);

				timeClock = ProxyClock.getError();
				logLock.lock();
				try {
					Log.log(INFO, path, "Command sent to client", className, timeClock);

				} finally {
					logLock.unlock();
				}
				socketToCLient.close();
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