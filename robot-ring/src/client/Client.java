package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String ipNode = "10.45.0.20";
        int port = 5000;
        int maxNodes = 4;
        
        String configurationRobot[][] = new String[maxNodes][4];
        String ipToStop = null;
        
		Scanner u = new Scanner(System.in);
		Configuration configuration = new Configuration(maxNodes);
		
		try (Socket socket = new Socket(ipNode, port)) {
			for(int i = 0; i < maxNodes; i++) {
				System.out.print("Robot ip: ");
				configurationRobot[i][0] = u.nextLine();
				
				System.out.print("Joint number (1-6): ");
				configurationRobot[i][1] = u.nextLine();
				
				System.out.print("Joint rotation: ");
				configurationRobot[i][2] = u.nextLine();
				
				System.out.print("Joint translation: ");
				configurationRobot[i][3] = u.nextLine();
				
				System.out.print("\n");
			}
			
			System.out.print("Robot ip to stop: ");
			ipToStop = u.nextLine();
			
			configuration.setConfigurationRobot(configurationRobot);
			configuration.setIpToStop(ipToStop);
			
			ObjectOutputStream oout = new ObjectOutputStream (socket.getOutputStream());
			oout.writeObject(configuration);
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("NODE FALLEN");
		}
	}
}
