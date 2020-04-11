package cameraRing;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {

	private final static Logger LOGGER = Logger.getLogger("log.class");

	public static void log(String type, String path, String message) {
		FileHandler fileHandler = null;
		try {
			fileHandler = new FileHandler(path, true);

			// simple format
			SimpleFormatter simpleFormatter = new SimpleFormatter();
			fileHandler.setFormatter(simpleFormatter);

			// write log to file
			LOGGER.addHandler(fileHandler);

			// level fileHandler
			fileHandler.setLevel(Level.ALL);

			// Write info
			if(type.equals("info")) {
				LOGGER.log(Level.INFO, message);
				//LOGGER.setUseParentHandlers(false);
			}
			else if(type.equals("error")) {
				LOGGER.log(Level.SEVERE, message);
				//LOGGER.setUseParentHandlers(false);
			}
			else if(type.equals("warning")) {
				LOGGER.log(Level.WARNING, message);
				//LOGGER.setUseParentHandlers(false);
			}
			

		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, "IO error");
		} catch (SecurityException ex) {
			LOGGER.log(Level.SEVERE, "Security error");
		}
		fileHandler.close();
	}

	public static String getStackTrace(Exception e) {
		StringWriter sWriter = new StringWriter();
		PrintWriter pWriter = new PrintWriter(sWriter);
		e.printStackTrace(pWriter);
		return sWriter.toString();
	}

}