import MatriculasApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class MatriculasClient
{
  static Matriculas matriculasImpl;

  public static void main(String args[]) {
//	  String orbArgs[] = {args[1], args[2], args[3], args[4]};
//	  String inOutArgs = args[0];
//	  boolean inOut;
//	  
//	  if(inOutArgs.equals("true")) {
//		  inOut = true;
//	  } else {
//		  inOut = false;
//	  }
	  
      try {
		ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
 
        String name = "Matriculas";
        matriculasImpl = MatriculasHelper.narrow(ncRef.resolve_str(name));

        System.out.println(matriculasImpl.matriculasDetectorOut());
        matriculasImpl.shutdown();

        } catch (Exception e) {
          System.out.println("ERROR : " + e) ;
          e.printStackTrace(System.out);
        }
    }
}