package model;

import java.io.Serializable;

public class Microcontroller implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String checkCode;
	
	public Microcontroller(String name, String checkCode) {
		this.name = name;
		this.checkCode = checkCode;
	}

	public final String getName() {
		return name;
	}

	public final String getCheckCode() {
		return checkCode;
	}
	
	@Override
	public String toString(){
		return this.name;
	}
}
