import MatriculasApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;
import org.omg.CORBA.*;

public class MatriculasClient
{
  static Matriculas matriculasImpl;

  public static void main(String args[]) {
      try {
		ORB orb = ORB.init(args, null);

        org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");

        NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
 
        String name = "Matriculas";
        matriculasImpl = MatriculasHelper.narrow(ncRef.resolve_str(name));

        System.out.println("Obtained a handle on server object: " + matriculasImpl);
        System.out.println(matriculasImpl.matriculasDetector());
        matriculasImpl.shutdown();

        } catch (Exception e) {
          System.out.println("ERROR : " + e) ;
          e.printStackTrace(System.out);
        }
    }
}