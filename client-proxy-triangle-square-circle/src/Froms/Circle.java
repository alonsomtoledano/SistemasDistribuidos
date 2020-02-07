package Froms;

import java.io.Serializable;

public class Circle implements Serializable {
	private float radio;
	
	public Circle(float radio) {
		this.radio = radio;
	}
	
	public float getRadio() {
		return radio;
	}
}
