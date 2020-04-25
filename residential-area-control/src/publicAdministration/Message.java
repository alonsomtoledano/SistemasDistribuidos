package publicAdministration;

import java.io.Serializable;

public class Message implements Serializable {
	private String content;
	private String arr[];
	private int number;
	private long longNumber;

	public Message(String content) {
		this.content = content;
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