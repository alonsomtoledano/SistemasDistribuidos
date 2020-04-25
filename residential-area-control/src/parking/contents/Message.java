package parking.contents;

import java.io.Serializable;

public class Message implements Serializable{
	private String data; //MATRICULA
	private int number; 
	private long longNumber; //HORA
	private boolean status;

	public Message(String data) {
		this.data = data;
	}
	public Message(int number) {
		this.number = number;
	}
	public String getData() {
		return data;
	}
	public Message(boolean status) {
		this.status = status;
	}
	public Message(long longNumber) {
		this.longNumber = longNumber;
	}
	public void setData(String data) {
		this.data = data;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public boolean isStatus() {
		return status;
	}
	public void setStatus(boolean status) {
		this.status = status;
	}
	public long getLongNumber() {
		return longNumber;
	}
	public void setLongNumber(long longNumber) {
		this.longNumber = longNumber;
	}
}
