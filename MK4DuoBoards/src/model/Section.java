package model;

import java.util.ArrayList;
import java.util.List;

public class Section{
	private String name;
	private List<String> pins;
	private Boolean isDigital;
	
	public Section(String name, Boolean isDigital) {
		this.name = name;
		this.isDigital= isDigital;
		this.pins = new ArrayList<>();
	}

	public final String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name= name;
	}
	
	public final List<String> getPins() {
		return pins;
	}
	
	public final Boolean isDigital() {
		return isDigital;
	}

}
