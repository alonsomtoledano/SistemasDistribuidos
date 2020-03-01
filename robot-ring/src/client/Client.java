package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client {
	public static void main(String[] args) {
		String ipNode = "172.20.10.11";
        int port = 5004;
        
		Scanner u = new Scanner(System.in);
		Configuration configuration = new Configuration();
		
		try (Socket socket = new Socket(ipNode, port)) {
			while(true) {
				System.out.print("Robot ip: ");
				String ip = u.nextLine();
				System.out.print("Joint number (1 - 6): ");
				int jointNumber = u.nextInt();
				System.out.print("Joint rotation: ");
				float jointRotation = u.nextFloat();
				System.out.print("Joint translation: ");
				float jointTranslation = u.nextFloat();
				System.out.print("Stop (true, false): ");
				boolean stop = u.nextBoolean();
				System.out.print("\n");
				
				configuration.setIp(ip);
				configuration.setJointNumber(jointNumber);
				configuration.setJointRotation(jointRotation);
				configuration.setJointTranslation(jointTranslation);
				configuration.setStop(stop);
				
				ObjectOutputStream oout = new ObjectOutputStream (socket.getOutputStream());
				oout.writeObject(configuration);
			}
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("NODO CAIDO");
		}
	}
}
