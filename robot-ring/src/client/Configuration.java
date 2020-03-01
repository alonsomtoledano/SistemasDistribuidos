package client;

import java.io.Serializable;

public class Configuration implements Serializable{
	private String ip;
	private int jointNumber;
	private float jointRotation;
	private float jointTranslation;
	private boolean stop;
		
	public Configuration() {
		this.setIp("0");
		this.setJointNumber(0);
		this.setJointRotation(0);
		this.setJointTranslation(0);
		this.setStop(false);
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getJointNumber() {
		return jointNumber;
	}

	public void setJointNumber(int jointNumber) {
		this.jointNumber = jointNumber;
	}

	public float getJointRotation() {
		return jointRotation;
	}

	public void setJointRotation(float jointRotation) {
		this.jointRotation = jointRotation;
	}

	public float getJointTranslation() {
		return jointTranslation;
	}

	public void setJointTranslation(float jointTranslation) {
		this.jointTranslation = jointTranslation;
	}

	public boolean getStop() {
		return stop;
	}

	public void setStop(boolean stop) {
		this.stop = stop;
	}
}
