package ring;

public class Robot {
	private String ip;
	private float jointRotation[] = {0, 0, 0, 0, 0, 0};
	private float jointTranslation[] = {0, 0, 0, 0, 0, 0};
	
	public Robot(String ip) {
		this.ip = ip;
	}

	public float[] getJointRotation() {
		return jointRotation;
	}

	public void setJointRotation(float jointRotation[]) {
		this.jointRotation = jointRotation;
	}

	public float[] getJointTranslation() {
		return jointTranslation;
	}

	public void setJointTranslation(float jointTranslation[]) {
		this.jointTranslation = jointTranslation;
	}

	public String getIp() {
		return ip;
	}
}