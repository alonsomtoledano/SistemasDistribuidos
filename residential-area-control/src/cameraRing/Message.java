package cameraRing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {	
	private List<List<String>> matriculasInLog = new ArrayList<List<String>>();
	private List<String> matriculasOutLog = new ArrayList<String>();
	
	//GETTERS AND SETTERS
	public List<List<String>> getMatriculasInLog() {
		return matriculasInLog;
	}
	public void setMatriculasInLog(List<List<String>> matriculasInLog) {
		this.matriculasInLog = matriculasInLog;
	}
	
	public List<String> getMatriculasOutLog() {
		return matriculasOutLog;
	}
	public void setMatriculasOutLog(List<String> matriculasOutLog) {
		this.matriculasOutLog = matriculasOutLog;
	}
	
	//METHODS
}
