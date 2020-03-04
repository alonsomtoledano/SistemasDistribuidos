package client;

import java.io.Serializable;

public class Configuration implements Serializable{
	
	//CONFIGURATION: IP, JOINT NUMBER, ROTATION, TRANSLATION
	private String configurationRobot[][];
	private String ipToStop = "0";
	private boolean done[];
	
	public Configuration(int maxNodes) {
		//CONFIGURATION
		String configuration[][] = new String[maxNodes][4];
		for(int i = 0; i < maxNodes; i++) {
			for(int j = 0; j < 4; j++) {
				configuration[i][j] = "0";
			}
		}
		this.configurationRobot = configuration;
		
		//DONE
		boolean doneArray[] = new boolean[maxNodes];
		for(int i = 0; i < maxNodes; i++) {
			doneArray[i] = false;
		}
		this.done = doneArray;
	}

	public String[][] getConfigurationRobot() {
		return configurationRobot;
	}

	public void setConfigurationRobot(String configurationRobot[][]) {
		this.configurationRobot = configurationRobot;
	}

	public String getIpToStop() {
		return ipToStop;
	}

	public void setIpToStop(String ipToStop) {
		this.ipToStop = ipToStop;
	}

	public boolean[] getDone() {
		return done;
	}

	public void setDone(boolean done[]) {
		this.done = done;
	}
}
