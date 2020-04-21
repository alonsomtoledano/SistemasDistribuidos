package cameraRing;

import java.io.Serializable;

public class FallConfiguration implements Serializable {
	private int puertoDerecha;
	private int puertoIzquierda2;
	private String ip;
	private boolean masterNode;
	
	//GETTERS AND SETTERS
	public int getPuertoDerecha() {
		return puertoDerecha;
	}
	public void setPuertoDerecha(int puertoDerecha) {
		this.puertoDerecha = puertoDerecha;
	}
	
	public int getPuertoIzquierda2() {
		return puertoIzquierda2;
	}
	public void setPuertoIzquierda2(int puertoIzquierda2) {
		this.puertoIzquierda2 = puertoIzquierda2;
	}
	
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	public boolean isMasterNode() {
		return masterNode;
	}
	public void setMasterNode(boolean masterNode) {
		this.masterNode = masterNode;
	}
}
