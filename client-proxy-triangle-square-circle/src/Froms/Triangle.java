package Froms;

import java.io.Serializable;

public class Triangle implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private float base;
	private float height;
	
	public Triangle(float base, float height) {
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
