package cameraRing;

import java.io.Serializable;

public class FallConfiguration implements Serializable {
	private int puertoDerecha;
	private int puertoDerecha2;
	private String ip;
	private boolean masterNode = false;
	private boolean closeRing = false;
	
	public FallConfiguration(int puertoDerecha, int puertoDerecha2, String ip) {
		this.puertoDerecha = puertoDerecha;
		this.puertoDerecha2 = puertoDerecha2;
		this.ip = ip;
	}
	
	//GETTERS AND SETTERS
	public int getPuertoDerecha() {
		return puertoDerecha;
	}
	public void setPuertoDerecha(int puertoDerecha) {
		this.puertoDerecha = puertoDerecha;
	}
	
	public int getPuertoDerecha2() {
		return puertoDerecha2;
	}
	public void setPuertoDerecha2(int puertoIzquierda2) {
		this.puertoDerecha2 = puertoIzquierda2;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public boolean getMasterNode() {
		return masterNode;
	}
	public void setMasterNode(boolean masterNode) {
		this.masterNode = masterNode;
	}

	public boolean getCloseRing() {
		return closeRing;
	}
	public void setCloseRing(boolean closeRing) {
		this.closeRing = closeRing;
	}
}
