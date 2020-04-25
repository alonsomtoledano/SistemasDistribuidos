package clock;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class ProxyClock {
	//TIME
	static long adjustedTime = 0;
	//LOG VARIABLES
//	static String path = "./src/parking/logs/proxyClock.log";
	static String INFO = "info";
	static String ERROR = "error";
	static String className;
	
	//FUNCTIONS
    public static long getError() {
    	//Class thisClass = new Object(){}.getClass();
		//className = thisClass.getEnclosingClass().getSimpleName();
    	//Log.log(INFO, path, "PROXYCLOCK received time from SERVER");
    	List<Long> list = new ArrayList<Long>();
    	ClockService stSQL = new ClockService();
    	setError();
    	list = stSQL.getAverage();
    	
    	//System.out.println("\nERROR LIST: " + list);
    	long result = 0;
    	for(int i= 0; i< list.size(); i++) {
    		result = result + list.get(i);
    	}
    	long average = Math.abs(result/list.size());
    	//System.out.println("\nAVERAGE OF ERROR: " + average);
    	
    	long standardDeviation = 0;
    	
    	for(int i=0; i<list.size(); i++) {
    		standardDeviation += Math.pow(list.get(i) - average, 2);
    	}
    	standardDeviation = (long) Math.sqrt(standardDeviation / (long) list.size());
    	
    	//System.out.println("STANDARD DEVIATION: " + standardDeviation);
    	
    	List<Long> atypicalData = new ArrayList<Long>();
    	List<Long> typicalData = new ArrayList<Long>();
    	
    	for (int i=0; i<list.size(); i++) {
    		if((Math.abs(list.get(i) - average)) > standardDeviation) {
    			atypicalData.add(list.get(i));
    		}else {
    			typicalData.add(list.get(i));
    		}
    	}
    	for(int i=0; i<typicalData.size(); i++) {
    		average += typicalData.get(i);
    	}
    	
    	average = average /typicalData.size();
    	
    	//System.out.println("ATYPICAL DATA: " + atypicalData);
    	//System.out.println("AVERAGE FINAL: " + average);
    	
    	long getTime = adjustedTime + average;
    	//Log.log(INFO, path, "PROXYCLOCK sends adjustTime to SERVER");
        //Log.log(INFO, path, "***************************************");

    	
    	return getTime;
    }
    static void setError() {
    	ClockService stSQL = new ClockService();
    	try {
	    	int serverPort = 5400;
	    	String serverHost = "127.0.0.1";
	    	Socket socket = new Socket(serverHost, serverPort);

	    	ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
	        Random random = new Random();
	
	        //New random client time
	        int millisInDay = 24 * 60 * 60 * 1000;
	        Time clientFullTime = new Time((long) random.nextInt(millisInDay));
	        String clientHr = (clientFullTime.getHours() < 9) ? '0' + Integer.toString(clientFullTime.getHours()) : Integer.toString(clientFullTime.getHours());
	        String clientMin = (clientFullTime.getMinutes() < 9) ? '0' + Integer.toString(clientFullTime.getMinutes()) : Integer.toString(clientFullTime.getMinutes());
	
	        String clientTime = clientHr + ':' + clientMin;
	
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
	        Calendar c = Calendar.getInstance();
	        c.set(Calendar.HOUR_OF_DAY, sdf.parse(clientTime).getHours());
	        long clientTimeLong = c.getTimeInMillis();
	        
	        Message messageClientTimeLong = new Message(clientTimeLong);
	        oos.writeObject(messageClientTimeLong);
	        //Log.log(INFO, path, "PROXYCLOCK request time to SERVER SYNCHRONIZATION");
	        
	        //System.out.println("\nWAITING CLOCK SYNCHRONIZATION.....");
	        
	        //Reading the time from server, and the gap
	        ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
	    	Message messageServerTime = (Message)ois.readObject();
	    	Message messageGapServer = (Message)ois.readObject();
	        
	    	//Log.log(INFO, path, "PROXYCLOCK received time from SERVER SYNCHRONIZATION");
	    	
	        long serverTime = messageServerTime.getLongNumber();
	        int gapServer = messageGapServer.getNumber();
	        adjustedTime = adjustTime(serverTime, gapServer);
	        
	        //System.out.println("\n============================================");
	        //System.out.println("CLIENT TIME: " + clientFullTime);
	        //System.out.println("CLIENT TIME MILISECONDS: " + millisInDay);
	        
	        long error = (serverTime - adjustedTime);
	        stSQL.setError(serverTime, adjustedTime, error);
	        
	        //System.out.println("SERVER TIME: " + serverTime);
	        //System.out.println("ADJUSTED TIME: " + adjustedTime);
	        //System.out.println("ERROR : " + error);
	        
	        socket.close();
        
    	}catch(IOException e) {
    		e.printStackTrace();
    	} catch (ParseException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
    private static long adjustTime(long sTime, int gap) {
        Random r = new Random();
        int t0 = r.nextInt(2000);
        int t1 = r.nextInt(3000);
        int d = (t1 - t0 - (gap * 1000)) / 2;
        return (sTime + d);
    }
}