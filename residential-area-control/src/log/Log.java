package log;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Instant;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.SimpleFormatter;

public class Log {
	
	
	public static void log(String type, String path, String message, String nameClass, long time) {
		//CODE
		try {
			LogRecord logRecord = new LogRecord( Level.parse("800"), message); 
		
			FileHandler fileHandler = new FileHandler(path, true);
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			Instant instant = Instant.ofEpochMilli(time);
			logRecord.setInstant(instant);
			fileHandler.setFormatter(simpleFormatter);
				
			logRecord.setSourceClassName(nameClass + ".java"); 
			
			if(type.equals("info")) {
				logRecord.setLevel(Level.INFO);	
			}
			else if(type.equals("error")) {
				logRecord.setLevel(Level.SEVERE);
			}
			else if(type.equals("warning")) {
				logRecord.setLevel(Level.WARNING);
			}
			
			logRecord.setLevel(Level.INFO);
			fileHandler.publish(logRecord);
			
			fileHandler.close();
		}catch(IOException e) {
			e.printStackTrace();
		}
	}
	//FUNCTION PRINTSTACKTRACE
	public static String getStackTrace(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}
}