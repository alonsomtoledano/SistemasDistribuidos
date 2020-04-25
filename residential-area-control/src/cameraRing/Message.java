package cameraRing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {	
	private List<List<String>> matriculasInLog = new ArrayList<List<String>>();
	private List<List<String>> matriculasOutLog = new ArrayList<List<String>>();
	
	private String content;
	private String arr[];
	private int number;
	private long longNumber;
	
	public Message() {
	}
	
	public Message(String content) {
		this.content = content;
	}
	
	//GETTERS AND SETTERS RING
	public List<List<String>> getMatriculasInLog() {
		return matriculasInLog;
	}
	public void setMatriculasInLog(List<List<String>> matriculasInLog) {
		this.matriculasInLog = matriculasInLog;
	}
	
	public List<List<String>> getMatriculasOutLog() {
		return matriculasOutLog;
	}
	public void setMatriculasOutLog(List<List<String>> matriculasOutLog) {
		this.matriculasOutLog = matriculasOutLog;
	}
	
	
	//GETTERS AND SETTERS RING PUBLIC ADMINISTRATION
	public Message(String arr[]) {
		this.arr = arr;
	}

	public Message(int number) {
		this.number = number;
	}

	public Message(long longNumber) {
		this.longNumber = longNumber;
	}

	public long getLongNumber() {
		return longNumber;
	}

	public void setLongNumber(long longNumber) {
		this.longNumber = longNumber;
	}

	public int getNumber() {
		return number;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String[] getArr() {
		return arr;
	}

	public void setArr(String[] arr) {
		this.arr = arr;
	}
}
