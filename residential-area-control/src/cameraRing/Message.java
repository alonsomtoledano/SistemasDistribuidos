package cameraRing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {	
	private List<List<String>> matriculasInLog = new ArrayList<List<String>>();
	private List<List<String>> matriculasOutLog = new ArrayList<List<String>>();
	
	//GETTERS AND SETTERS
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
}
