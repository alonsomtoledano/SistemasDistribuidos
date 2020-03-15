import MatriculasApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;
import org.omg.PortableServer.*;
import org.omg.PortableServer.POA;

import java.util.Properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import java.io.*;

class MatriculasImpl extends MatriculasPOA {
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val; 
  }
  
  // implement generator() method
  public String matriculasGenerator() {
	List <String> listaMaticulas = new ArrayList<String>();
	
	int index = 5;
	
	for (int i=0; i<index; i++) {
		char [] chars = "BCDFGHJKLMNPQRSTVWXYZ".toCharArray();
		char [] numbers = "1234567890".toCharArray();
		int charsLength = chars.length;
		int numbersLenght = numbers.length;
		Random random = new Random();
		StringBuffer bufferChar = new StringBuffer();
		StringBuffer bufferNumber = new StringBuffer();
		for (int j=0; j<3; j++){
		   bufferChar.append(chars[random.nextInt(charsLength)]);
		}
		for (int j=0; j<4; j++) {
			bufferNumber.append(numbers[random.nextInt(numbersLenght)]);
		}
		String matricula = bufferNumber.toString() + bufferChar.toString();
		listaMaticulas.add(matricula);
	}
	return listaMaticulas.toString();
  }
  
  public String readMatriculas() {
	File archivo = null;
	FileReader fr = null;
	BufferedReader br = null;
	
	String listaMaticulas = "";
	try {
		// Apertura del fichero y creacion de BufferedReader para poder
		// hacer una lectura comoda (disponer del metodo readLine()).
		archivo = new File ("./matriculas.txt");
		fr = new FileReader (archivo);
		br = new BufferedReader(fr);
		// Lectura del fichero
		String linea;
		while((linea=br.readLine())!=null) {
			listaMaticulas = listaMaticulas + linea + ", ";
		}
	}
	catch(Exception e){
		e.printStackTrace();
	}finally{
		// En el finally cerramos el fichero, para asegurarnos
		// que se cierra tanto si todo va bien como si salta 
		// una excepcion.
		try{                    
			if( null != fr ){   
			   fr.close();     
			}
		}catch (Exception e2){ 
			e2.printStackTrace();
		}
	}
	return listaMaticulas;
  }
  
  public String findTxt() {
		File archivo = null;
		FileReader fr = null;
		BufferedReader br = null;
		
		String listaMaticulas = "";
		try {
			File f = new File("./");
			FilenameFilter filter = new FilenameFilter() {
				@Override
				public boolean accept(File f, String name) {
					// We want to find only .c files
					return name.endsWith(".txt");
				}
			};
			// Note that this time we are using a File class as an array,
			// instead of String
			File[] files = f.listFiles(filter);
			// Get the names of the files by using the .getName() method
			for (int i = 0; i < files.length; i++) {
				System.out.println(files[i].getName());
				archivo = new File (files[i].getName());
				fr = new FileReader (archivo);
				br = new BufferedReader(fr);
				 // Lectura del fichero
				String linea;
				while((linea=br.readLine())!=null){
					listaMaticulas = listaMaticulas + linea + ", ";
				}
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
		}finally{
		 // En el finally cerramos el fichero, para asegurarnos
		 // que se cierra tanto si todo va bien como si salta 
		 // una excepcion.
		 try{                    
			if( null != fr ){   
			   fr.close();     
			}                  
		 }catch (Exception e2){ 
			e2.printStackTrace();
		 }
		}
		return listaMaticulas;
  }
    
  // implement shutdown() method
  public void shutdown() {
    orb.shutdown(false);
  }
}


public class MatriculasServer {

  public static void main(String args[]) {
    try{
      // create and initialize the ORB
      ORB orb = ORB.init(args, null);

      // get reference to rootpoa & activate the POAManager
      POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
      rootpoa.the_POAManager().activate();

      // create servant and register it with the ORB
      MatriculasImpl matriculasImpl = new MatriculasImpl();
      matriculasImpl.setORB(orb); 

      // get object reference from the servant
      org.omg.CORBA.Object ref = rootpoa.servant_to_reference(matriculasImpl);
      Matriculas href = MatriculasHelper.narrow(ref);
          
      // get the root naming context
      // NameService invokes the name service
      org.omg.CORBA.Object objRef =
          orb.resolve_initial_references("NameService");
      // Use NamingContextExt which is part of the Interoperable
      // Naming Service (INS) specification.
      NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

      // bind the Object Reference in Naming
      String name = "Matriculas";
      NameComponent path[] = ncRef.to_name( name );
      ncRef.rebind(path, href);

      System.out.println("MatriculasServer ready and waiting ...");

      // wait for invocations from clients
      orb.run();
    } 
        
      catch (Exception e) {
        System.err.println("ERROR: " + e);
        e.printStackTrace(System.out);
      }
          
      System.out.println("MatriculasServer Exiting ...");
        
  }
}