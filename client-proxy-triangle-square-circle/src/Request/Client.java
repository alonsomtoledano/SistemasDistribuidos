package Request;

import java.io.*;
import java.net.*;
import java.util.*;

import Froms.Circle;
import Froms.Square;
import Froms.Triangle;

public class Client {
	 
    public static void main(String[] args) {
    	
    	int option;
    	Triangle triangle = null;
    	Square square = null;
    	Circle circle = null;
    	
    	String portProxy;
    	Scanner u = new Scanner(System.in);
    	
		while (true) {
			System.out.println("<1> Triangle Area\n<2> Square Area\n<3> Circle Area");
			option = u.nextInt();
			
			if (option == 1) {
				portProxy = "TRIANGLE";
				triangle = createTriangle();
				break;
			}
			else if (option == 2) {
				portProxy = "SQUARE";
				square =  createSquare();
				break;
			}
			else if (option == 3) {
				portProxy = "CIRCLE";
				circle =  createCircle();
				break;
			}
			else {
				System.out.println("Enter a valid option");
			}
		}
		
        String host = "127.0.0.1";
        int port = 32000;
        try (Socket socket = new Socket(host, port)) {

        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	DataOutputStream out = new DataOutputStream(socket.getOutputStream());
        	
        	out.writeUTF(portProxy);
        	
        	int freePort = Integer.parseInt(in.readUTF());
            System.out.println("PORT TO CONNECT: " + freePort);
          
            try (Socket socket2 = new Socket(host, freePort)) {
            	
            	ObjectOutputStream os = new ObjectOutputStream (socket2.getOutputStream());
            	DataInputStream in2 = new DataInputStream(socket2.getInputStream());
            	
            	if(triangle != null) {
            		os.writeObject(triangle);
                	String area = in2.readUTF();
                	System.out.println("Area: " + area);
            	}
            	else if (square != null) {
            		os.writeObject(square);
                	String area = in2.readUTF();
                	System.out.println("Area: " + area);
            	}
            	else if (circle != null) {
            		os.writeObject(circle);
                	String area = in2.readUTF();
                	System.out.println("Area: " + area);
            	}

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static Triangle createTriangle() {
    	Scanner u = new Scanner(System.in);
    	float base;
    	float height;
    	
    	System.out.print("Base: ");
		base = u.nextFloat();
		System.out.print("Height: ");
		height = u.nextFloat();
		
    	Triangle triangle = new Triangle(base, height);

		return triangle;
    }
    
    public static Square createSquare() {
    	Scanner u = new Scanner(System.in);
    	float base;
    	float height;
    	
    	System.out.print("Base: ");
		base = u.nextFloat();
		System.out.print("Height: ");
		height = u.nextFloat();

    	Square square = new Square(base, height);
    	
    	return square;
    }
    
    public static Circle createCircle() {
    	Scanner u = new Scanner(System.in);
    	float radio;
    	
    	System.out.print("Radio: ");
		radio = u.nextFloat();
    	
    	Circle circle = new Circle(radio);
    	
    	return circle;
    }
}