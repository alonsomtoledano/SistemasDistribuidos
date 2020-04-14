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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

class MatriculasImpl extends MatriculasPOA {
  private ORB orb;

  public void setORB(ORB orb_val) {
    orb = orb_val; 
  }
  
  public String matriculasDetector(String folderPath) {
	String matricula = null;
	String imagePath = "hola";
	File folder = new File(folderPath);

  	try {  				
		String[] list = folder.list();
		
    	for (int i = 0; i < list.length; i++) {
    		if (list[i].endsWith(".jpg")) {
    			imagePath = folderPath + list[i];
    			
    			try {
    				Process proc = Runtime.getRuntime().exec("python ../matriculasDetector.py " + imagePath);
					
					//READ
    				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));

    				String line = null;
    				while ((line = stdInput.readLine()) != null) {
    					matricula = line;
    				}    				
				} catch (Exception e) {
				}
    		}
    	}
	} catch (Exception e) {
	}
  	//return matricula;
  	return imagePath;
  }
    
  public void shutdown() {
    orb.shutdown(false);
  }
}


public class MatriculasServer {

  public static void main(String args[]) {
	  while (true) {
	    try{
	        ORB orb = ORB.init(args, null);

	        POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
	        rootpoa.the_POAManager().activate();

	        MatriculasImpl matriculasImpl = new MatriculasImpl();
	        matriculasImpl.setORB(orb); 

	        org.omg.CORBA.Object ref = rootpoa.servant_to_reference(matriculasImpl);
	        Matriculas href = MatriculasHelper.narrow(ref);
	            
	        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

	        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

	        String name = "Matriculas";
	        NameComponent path[] = ncRef.to_name( name );
	        ncRef.rebind(path, href);

	        System.out.println("MatriculasServer ready and waiting ...");
	        
	        orb.run();
	        
	      }
	          
	        catch (Exception e) {
	          System.err.println("ERROR: " + e);
	          e.printStackTrace(System.out);
	        }
	            
	        System.out.println("MatriculasServer Exiting ...");
	  }
  }
}