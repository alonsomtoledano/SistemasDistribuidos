package publicAdministration.nodes;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.locks.ReentrantLock;

import clock.ProxyClock;
import log.Log;
import cameraRing.Message;

public class Client1 {

	static int proxyLoginPort = 4008;
	static int proxyRequestPort = 4009;
	static int proxyResponsetPort = 4010;
	static int clientPort = 4000;
	static String path = "./src/publicAdministration/logs/client1.log";
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
		Class thisClass = new Object() {
		}.getClass();
		className = thisClass.getEnclosingClass().getSimpleName();

		timeClock = ProxyClock.getError();
		logLock.lock();
		try {
			Log.log(INFO, path, "CLIENT START", className, timeClock);
		} finally {
			logLock.unlock();
		}
		login();
		//signUp();
		try {

			ServerSocket proxy = new ServerSocket(clientPort);
			while (true) {
				Socket proxyAccept = proxy.accept();
				ProxyHandler threadServerAccept = new ProxyHandler(proxyAccept);

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

	public static void menu() {
		Scanner sc = new Scanner(System.in);
		System.out.println("-------------------------------");
		System.out.println("Enter a command or '-h' to see help");
		System.out.print(">");

		String option = sc.nextLine();
		if (option.equals("-h")) {
			System.out.println("-------------------------------");
			System.out.println("Possible options include:");
			System.out.println("signUp");
			System.out.println("logout");
			System.out.println("addResident");
			System.out.println("deleteResident");
			System.out.println("findSanctioned");
			menu();
		}

		else if (option.equals("signUp")) {
			signUp();
		}

		else if (option.equals("logout")) {
			logout();
		}

		else if (option.equals("addResident")) {
			addResident();
		}

		else if (option.equals("deleteResident")) {
			deleteResident();
		}

		else if (option.equals("findSanctioned")) {
			findSanctioned();
		}

		else if (option.equals("exit")) {
			System.out.println("Disconnected");
		} else {
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(ERROR, path, "Commamnd not found", className, timeClock);
			} finally {
				logLock.unlock();
			}
			System.out.println("Commamnd not found \nWrite -h for help\n");
			menu();
		}
	}

	public static void signUp() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";
		Scanner sc = new Scanner(System.in);

		System.out.print("DNI: ");
		Message dni = new Message(sc.nextLine());

		System.out.print("USER: ");
		Message userName = new Message(sc.nextLine());

		System.out.print("PASSWORD: ");
		Message pwd = new Message(sc.nextLine());

		Message command = new Message("signUp");

		Message port = new Message(clientPort);
		System.out.println(port.getNumber());

		try (Socket socketToProxy = new Socket(clientHost, proxyLoginPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);
			out1.writeObject(userName);
			out1.writeObject(pwd);
			out1.writeObject(dni);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	public static void login() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";
		Scanner sc = new Scanner(System.in);

		System.out.print("USER: ");
		Message userName = new Message(sc.nextLine());

		System.out.print("PASSWORD: ");
		Message pwd = new Message(sc.nextLine());

		Message command = new Message("login");
		Message port = new Message(clientPort);

		try (Socket socketToProxy = new Socket(clientHost, proxyLoginPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);
			out1.writeObject(userName);
			out1.writeObject(pwd);
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	public static void logout() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";
		Message command = new Message("logout");
		Message port = new Message(clientPort);

		try (Socket socketToProxy = new Socket(clientHost, proxyLoginPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	public static void addResident() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";

		Scanner sc = new Scanner(System.in);

		System.out.print("DNI: ");
		Message dni = new Message(sc.nextLine());

		System.out.print("NAME: ");
		Message name = new Message(sc.nextLine());

		System.out.print("SURNAME: ");
		Message surname = new Message(sc.nextLine());

		System.out.print("LICENSE PLATE: ");
		Message plate = new Message(sc.nextLine());

		Message command = new Message("addResident");
		Message port = new Message(clientPort);

		try (Socket socketToProxy = new Socket(clientHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);
			out1.writeObject(dni);
			out1.writeObject(name);
			out1.writeObject(surname);
			out1.writeObject(plate);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	public static void deleteResident() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";

		Scanner sc = new Scanner(System.in);

		System.out.print("DNI: ");
		Message dni = new Message(sc.nextLine());

		Message command = new Message("deleteResident");
		Message port = new Message(clientPort);

		try (Socket socketToProxy = new Socket(clientHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);
			out1.writeObject(dni);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	public static void findSanctioned() {
		System.out.println("-------------------------------");
		String clientHost = "127.0.0.1";

		Scanner sc = new Scanner(System.in);

		System.out.print("LICENSE PLATE: ");
		Message plate = new Message(sc.nextLine());

		Message command = new Message("findSanctioned");
		Message port = new Message(clientPort);

		try (Socket socketToProxy = new Socket(clientHost, proxyRequestPort)) {
			ObjectOutputStream out = new ObjectOutputStream(socketToProxy.getOutputStream());
			out.writeObject(command);

			ObjectOutputStream out1 = new ObjectOutputStream(socketToProxy.getOutputStream());
			out1.writeObject(port);
			out1.writeObject(plate);

			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "Data sent to proxy", className, timeClock);

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

	private static class ProxyHandler implements Runnable {
		private final Socket socket;

		public ProxyHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "CLIENT RUN", className, timeClock);

			} finally {
				logLock.unlock();
			}

			try {

				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message proxyCommand = (Message) in.readObject();

				if (proxyCommand.getContent().equals("signUp")) {
					doSignUp();
				}

				else if (proxyCommand.getContent().equals("login")) {
					doLogin();
				}

				else if (proxyCommand.getContent().equals("logout")) {
					doLogout();
				}

				else if (proxyCommand.getContent().equals("addResident")) {
					doAddResident();
				} else if (proxyCommand.getContent().equals("deleteResident")) {
					doDeleteResident();
				} else if (proxyCommand.getContent().equals("findSanctioned")) {
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

		public void doSignUp() {
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message command = (Message) in.readObject();
				System.out.println("-------------------------------");
				if (command.getContent().equals("ok")) {

					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "Sign up correct", className, timeClock);

					} finally {
						logLock.unlock();
					}
					System.out.println("SIGN UP CORRECT");
					menu();
				} else {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "Sign up incorrect: DNI already in use", className, timeClock);

					} finally {
						logLock.unlock();
					}
					System.out.println("SIGN UP INCORRECT: DNI already in use");
					menu();
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

		public void doLogin() {
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				Message command = (Message) in.readObject();
				System.out.println("-------------------------------");
				if (command.getContent().equals("password")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "Incorrect password", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Incorrect password");
					login();
				} else if (command.getContent().equals("username")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "Incorrect username", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Incorrect username");
					login();
				} else if (command.getContent().equals("status")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "User already logged", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("User already logged");
					login();
				} else {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "User logged in", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("WELCOME");
					menu();
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

		public void doLogout() {
			timeClock = ProxyClock.getError();
			logLock.lock();
			try {
				Log.log(INFO, path, "User logged out", className, timeClock);
			} finally {
				logLock.unlock();
			}
			System.out.println("BYE");
			login();
		}

		public void doAddResident() {
			System.out.println("Add Resident Run");

			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message command = (Message) in.readObject();
				System.out.println("-------------------------------");
				if (command.getContent().equals("ok")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "Resident added", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Resident added");
					menu();
				} else if (command.getContent().equals("notOk")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "License plate already registered", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("License plate already registered");
					menu();
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

		public void doDeleteResident() {
			System.out.println("Delete Resident Run");
			try {
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message command = (Message) in.readObject();
				System.out.println("-------------------------------");
				if (command.getContent().equals("ok")) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "Resident deleted", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Resident deleted");
					menu();
				} else if (command.getContent().equals("notOk")) {

					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "Incorrect DNI", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("Incorrect DNI");
					menu();
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

		public void doFindSanctioned() {
			System.out.println("Find Sanctioned Run");
			ObjectInputStream in;
			try {
				in = new ObjectInputStream(socket.getInputStream());
				Message command = (Message) in.readObject();
				if (command.getArr() != null) {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(INFO, path, "Results", className, timeClock);
					} finally {
						logLock.unlock();
					}

					System.out.println("-------------------------------");
					System.out.println("RESULTS:");
					for (int i = 0; i < command.getArr().length; i++) {
						System.out.println(command.getArr()[i]);
					}
					menu();
				} else {
					timeClock = ProxyClock.getError();
					logLock.lock();
					try {
						Log.log(WARNING, path, "License plate not found", className, timeClock);
					} finally {
						logLock.unlock();
					}
					System.out.println("-------------------------------");
					System.out.println("License plate not found");
					menu();
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