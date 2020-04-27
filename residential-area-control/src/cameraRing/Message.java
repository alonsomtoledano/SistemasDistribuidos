package cameraRing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {	
	private List<List<String>> matriculasInLog = new ArrayList<List<String>>(); //RING
	private List<List<String>> matriculasOutLog = new ArrayList<List<String>>(); //RING
	
	private String content; //PUBLIC ADMINISTRATION
	private String arr[]; //PUBLIC ADMINISTRATION
	
	private int number; //PUBLIC ADMINISTRATION AND PARKING
	private long longNumber; //PUBLIC ADMINISTRATION AND PARKING
	
	private String data; //PARKING
	private boolean status; //PARKING
	
	//CONSTRUCTORS
	public Message() {
	}
	
	public Message(String content) {
		this.content = content;
		this.data = content;
	}
	
	public Message(String arr[]) {
		this.arr = arr;
	}
	
	public Message(int number) {
		this.number = number;
	}
	
	public Message(long longNumber) {
		this.longNumber = longNumber;
	}
	
	public Message(boolean status) {
		this.status = status;
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
	
	
	//GETTERS AND SETTERS PUBLIC ADMINISTRATION
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

	//GETTERS AND SETTERS PARKING
	public String getData() {
		return data;
	}
	public void setData(String data) {
		this.data = data;
	}

	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	
	//GETTERS AND SETTERS PUBLIC ADMINISTRATION AND PARKING
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	
	public long getLongNumber() {
		return longNumber;
	}
	public void setLongNumber(long longNumber) {
		this.longNumber = longNumber;
	}
}
