package TCP;

public class Main {
	public static void main(String[] args) {
		TCPServer server = new TCPServer();
		TCPProxy proxy = new TCPProxy();
		TCPClient client = new TCPClient();
		
		server.main(args);
		proxy.main(args);
		client.main(args);
	}
	
}
