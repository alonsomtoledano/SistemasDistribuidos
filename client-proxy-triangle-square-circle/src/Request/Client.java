package Request;

import java.io.*;
import java.net.*;
import java.util.*;

import Froms.Circle;
import Froms.Square;
import Froms.Triangle;

public class Client {
	 
    public static void main(String[] args) {
    	
    	String form = menu();
		
        String host = "127.0.0.1";
        int port = 32000;
        try (Socket socket = new Socket(host, port)) {
        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	
        	out.writeUTF(form);
        	
        	int freePort = Integer.parseInt(in.readUTF());
            System.out.println("PORT TO CONNECT: " + freePort);
            
            try (Socket socket2 = new Socket(host, freePort)) {
            	DataInputStream in2 = new DataInputStream(socket2.getInputStream());
            	DataOutputStream out2 = new DataOutputStream(socket2.getOutputStream());
            	
            	out2.writeUTF(form);
            	String area = in2.readUTF();
                System.out.println("Area: " + area);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static String menu() {
    	int option;
    	Scanner u = new Scanner(System.in);
    	
		while (true) {
			System.out.println("<1> Triangle Area\n<2> Square Area\n<3> Circle Area");
			option = u.nextInt();
			
			if (option == 1) {
				return createTriangle();
			}
			else if (option == 2) {
				return createSquare();
			}
			else if (option == 3) {
				return createCircle();
			}
			else {System.out.println("Enter a valid option");}
		}
		
    }
    
    public static String createTriangle() {
    	Scanner u = new Scanner(System.in);
    	float base;
    	float height;
    	
    	System.out.print("Base: ");
		base = u.nextFloat();
		System.out.print("Height: ");
		height = u.nextFloat();
		
    	Triangle triangle = new Triangle(base, height);

		return "TRIANGLE";
    }
    
    public static String createSquare() {
    	Scanner u = new Scanner(System.in);
    	float base;
    	float height;
    	
    	System.out.print("Base: ");
		base = u.nextFloat();
		System.out.print("Height: ");
		height = u.nextFloat();

    	Square square = new Square(base, height);
    	System.out.println("Created");
    	
    	return "SQUARE";
    }
    
    public static String createCircle() {
    	Scanner u = new Scanner(System.in);
    	float radio;
    	
    	System.out.print("Radio: ");
		radio = u.nextFloat();
    	
    	Circle circle = new Circle(radio);
    	
    	return "CIRCLE";
    }
}