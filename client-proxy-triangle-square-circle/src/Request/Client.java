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
    	
    	Scanner u = new Scanner(System.in);
    	
		while (true) {
			System.out.println("<1> Triangle Area\n<2> Square Area\n<3> Circle Area");
			option = u.nextInt();
			
			if (option == 1) {
				triangle = createTriangle();
				break;
			}
			else if (option == 2) {
				square =  createSquare();
				break;
			}
			else if (option == 3) {
				circle =  createCircle();
				break;
			}
			else {
				System.out.println("Enter a valid option");
			}
		}
		
        String host = "127.0.0.1";
        int port = 32000;
        int freePort = 0;
        
        try (Socket socket = new Socket(host, port)) {

        	DataInputStream in = new DataInputStream(socket.getInputStream());
        	ObjectOutputStream oout = new ObjectOutputStream (socket.getOutputStream());
        	
        	if(triangle != null) {
        		oout.writeObject(triangle);
            	freePort = Integer.parseInt(in.readUTF());
                System.out.println("PORT TO CONNECT: " + freePort);
        	}
        	else if (square != null) {
        		oout.writeObject(square);
        		freePort = Integer.parseInt(in.readUTF());
                System.out.println("PORT TO CONNECT: " + freePort);
        	}
        	else if (circle != null) {
        		oout.writeObject(circle);
        		freePort = Integer.parseInt(in.readUTF());
                System.out.println("PORT TO CONNECT: " + freePort);
        	}
          
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