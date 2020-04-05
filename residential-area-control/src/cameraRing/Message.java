package cameraRing;

import java.io.Serializable;

public class Message implements Serializable {
	
	private String content;
	
	public Message(String content) {
		this.content = content;
	}
	
	
	//GETTERS AND SETTERS
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
