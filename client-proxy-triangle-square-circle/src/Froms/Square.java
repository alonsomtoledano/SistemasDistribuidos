package Froms;

import java.io.Serializable;

public class Square implements Serializable {
	private float base;
	private float height;
	
	public Square(float base, float height) {
		this.base = base;
		this.height = height;
	}
	
	public float getBase() {
		return base;
	}

	public float getHeight() {
		return height;
	}
}
