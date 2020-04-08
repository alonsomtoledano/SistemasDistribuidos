package MatriculasApp;


/**
* MatriculasApp/MatriculasPOA.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from MatriculasApp.idl
* mi�rcoles 8 de abril de 2020 18H53' CEST
*/

public abstract class MatriculasPOA extends org.omg.PortableServer.Servant
 implements MatriculasApp.MatriculasOperations, org.omg.CORBA.portable.InvokeHandler
{

  // Constructors

  private static java.util.Hashtable _methods = new java.util.Hashtable ();
  static
  {
    _methods.put ("matriculasDetectorIn", new java.lang.Integer (0));
    _methods.put ("shutdown", new java.lang.Integer (1));
  }

  public org.omg.CORBA.portable.OutputStream _invoke (String $method,
                                org.omg.CORBA.portable.InputStream in,
                                org.omg.CORBA.portable.ResponseHandler $rh)
  {
    org.omg.CORBA.portable.OutputStream out = null;
    java.lang.Integer __method = (java.lang.Integer)_methods.get ($method);
    if (__method == null)
      throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

    switch (__method.intValue ())
    {
       case 0:  // MatriculasApp/Matriculas/matriculasDetectorIn
       {
         String $result = null;
         $result = this.matriculasDetectorIn ();
         out = $rh.createReply();
         out.write_string ($result);
         break;
       }

       case 1:  // MatriculasApp/Matriculas/shutdown
       {
         this.shutdown ();
         out = $rh.createReply();
         break;
       }

       default:
         throw new org.omg.CORBA.BAD_OPERATION (0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
    }

    return out;
  } // _invoke

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:MatriculasApp/Matriculas:1.0"};

  public String[] _all_interfaces (org.omg.PortableServer.POA poa, byte[] objectId)
  {
    return (String[])__ids.clone ();
  }

  public Matriculas _this() 
  {
    return MatriculasHelper.narrow(
    super._this_object());
  }

  public Matriculas _this(org.omg.CORBA.ORB orb) 
  {
    return MatriculasHelper.narrow(
    super._this_object(orb));
  }


} // class MatriculasPOA
