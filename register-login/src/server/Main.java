package server;

import java.io.*;
import java.net.*;
import protocol.*;



public class Main {

    private ServerSocket s;
    private ObjectInputStream is;
    private ObjectOutputStream os;
    private Usuario usuario;
    private int numberofusers;

    public static void main(String[] args) {
        
        new Main();
    }

    //--------------------------------------------------------------------------

    
    public Main() {
        
        System.out.println("Arrancando el servidor...");
        this.init();

        System.out.println("Abriendo canal de comunicaciones...");
        try {
            this.s = new ServerSocket(3339);
            while( true ) {
                Socket sServicio = s.accept();
                System.out.println( "Aceptada conexion de " + sServicio.getInetAddress().toString() );
                procesaCliente(sServicio);
            }
        } catch (IOException ex) {
        }
    }

    //--------------------------------------------------------------------------

    public void init() {
        
        // Codigo de inicializacion ...
         
      
        
    }

    //--------------------------------------------------------------------------

    public void procesaCliente(Socket sServicio) {
        try {
            
            this.is = new ObjectInputStream(sServicio.getInputStream());
            this.os = new ObjectOutputStream(sServicio.getOutputStream());

            boolean end = false;

            Peticion p = (Peticion)this.is.readObject();
            while( !end )
            {
                if( p.getTipo().compareTo("PETICION_CONTROL")==0 ) {
                    PeticionControl pc = (PeticionControl)p;
                    if( pc.getSubtipo().compareTo("OP_LOGIN")==0 )
                        this.doLogin(pc);
                    if( pc.getSubtipo().compareTo("OP_LOGOUT")==0 ) {
                        this.doLogout();
                        end = true;
                    }
                    
                    if( pc.getSubtipo().compareTo("OP_REGISTER")==0 ) {
                        this.doRegister(pc);                        
                    }
                    
                    if( pc.getSubtipo().compareTo("OP_START")==0 ) {
                        this.doStart(pc);  
                        
                    }
                    //CREAR: Comando para solicitar numero de servidor
//                    if( pc.getSubtipo().compareTo("OP_SERVER_NUMBER_2_START")==0 ) {
//                        this.doSelectServerNumber(pc);  
//                        //REVISAR this.doLogout();
//                    }
                }
                else if(p.getTipo().compareTo("PETICION_DATOS") == 0) {
                    PeticionDatos pd = (PeticionDatos)p;
                    if( pd.getSubtipo().compareTo("FILEPART_REQUEST")==0 ) {
                        //FilePartRequest fpr = (FilePartRequest)pd; 
                    }
                }
                p = (Peticion)this.is.readObject();
            }
        } catch (ClassNotFoundException ex) {
        } catch (IOException ex) {
        } finally {
            try {
                os.close();
                is.close();
                sServicio.close();
            } catch (IOException ex) {
            }
        }
    }

    //--------------------------------------------------------------------------

    public void doLogin(PeticionControl pc) {
        
        String login = (String) pc.getArgs().get(0);
        String password = (String) pc.getArgs().get(1);
        try {
            Usuario userData = new Usuario();
            int error = Usuario.findUser(login, password, userData);
            if( error==Usuario.USER_OK ) {
                RespuestaControl rc = new RespuestaControl("OP_LOGIN_OK");
                this.os.writeObject(rc);
                System.out.println("Usuario " + userData.getLogin() + ": conectado");
                this.usuario = userData;
            }
            else if( error==Usuario.USER_BAD_PASSWORD ) {
                RespuestaControl rc = new RespuestaControl("OP_LOGIN_BAD_PASSWORD");
                this.os.writeObject(rc);
                System.out.println("Intento de acceso con contrase�a incorrecta");
                this.usuario = null;
            }
            else if( error==Usuario.USER_NO_LOGIN ) {
                RespuestaControl rc = new RespuestaControl("OP_LOGIN_NO_USER");
                this.os.writeObject(rc);
                System.out.println("Intento de acceso con login incorrecto");
                this.usuario = null;
            }
        } catch (Exception ex) {
        }
        
    }
    //--------------------------------------------------------------------------
    //CREAR: Metodo para registro de usuarios
    public void doRegister(PeticionControl pc) {
        
        String login = (String) pc.getArgs().get(0);
        String password = (String) pc.getArgs().get(1);
        try {
            Usuario userData = new Usuario();
            int error = Usuario.findUser(login);
            if( error==Usuario.USER_OK ) {
                RespuestaControl rc = new RespuestaControl("OP_REG_NOK");
                this.os.writeObject(rc);
                System.out.println("Usuario " + userData.getLogin() + " no registrado porque ya existe");
                this.usuario = userData;
            }else if( error==Usuario.USER_NO_LOGIN ) {
                RespuestaControl rc = new RespuestaControl("OP_REG_OK");
                this.os.writeObject(rc);
                System.out.println("Nuevo usuario, registrando usuario...");
                Usuario.writeUser(login, password);
                System.out.println("Usuario registrado satisfactoriamente");
                this.usuario = null;
            }
        } catch (Exception ex) {
        }
        
    }
    //--------------------------------------------------------------------------
    
    public void doStart(PeticionControl pc) {
        
        if(this.usuario != null){
            try {
                System.out.println("Se inicia  "+ this.usuario.getLogin());
                //MODIFICAR PARA EL BALANCEO DE CARGA
                RespuestaControl rc = new RespuestaControl("OP_START_OK");
                this.os.writeObject(rc);                
               
            } catch (Exception ex) {
            }
        }else{
            try {
                System.out.println("Usuario no registrado");
                RespuestaControl rc = new RespuestaControl("OP_START_NOK");
                this.os.writeObject(rc);
            }catch(Exception ex) {                
            }
        }        
    }
    //--------------------------------------------------------------------------

    public void doLogout() {
        if( this.usuario!=null ){
            System.out.println("Desconectado usuario " + this.usuario.getLogin());
            this.usuario = null;
        }
    }
}