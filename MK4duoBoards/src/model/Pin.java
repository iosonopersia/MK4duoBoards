package model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utilities.Const;

public class Pin implements Externalizable{	
	private String name, section;
	private Boolean isDigital;
	private StringProperty comment;
	private IntegerProperty value;	

	public Pin(String name, Boolean isAnalog, String section){
		assertValidParameters(name, isAnalog, section);
		
		this.name= name;
		this.isDigital= ! isAnalog;
		this.section= section;

		setComment(Const.EMPTY);
		setValue(Const.UNDEFINED_PIN);
	}
	
	public Pin(){
		//THIS IS NEEDED ONLY FOR PERSISTENCE,
		//Externalizable REQUIRES IT!
	}

	private void assertValidParameters(String NAME, Boolean ISDIGITAL, String SECTION) {
		if(NAME==null || NAME.isEmpty() || ISDIGITAL==null || SECTION==null || SECTION.isEmpty()){
			throw new IllegalArgumentException();
		}
	}

	public final Boolean isDigital() {
		return isDigital;
	}
	
	public final String getName() {
		return this.name;
	}
	
	public final String getSection() {
		return this.section;
	}
	
	//############################################################
	//                        PROPERTIES
	//############################################################

	public final StringProperty commentProperty() {
		if(this.comment== null){
			this.comment= new SimpleStringProperty(this, "comment", Const.EMPTY);
		}
		return this.comment;
	}
	
	public String getComment() {
		return this.commentProperty().get();
	}

	public void setComment(String comment) {
		this.commentProperty().set(comment);
	}
	
	//------------------------------------------------------------
	public final IntegerProperty valueProperty() {
		if(this.value== null){
			this.value= new SimpleIntegerProperty(this, "value", Const.UNDEFINED_PIN);
		}
		return this.value;
	}
	
	public Integer getValue() {
		return this.valueProperty().get();
	}

	public void setValue(Integer value) {
		this.valueProperty().set(value);
	}
	
	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		this.name=(String) in.readObject();
		this.isDigital=(Boolean) in.readObject();
		this.section=(String) in.readObject();
		
		setValue((Integer) in.readObject());
		setComment(((String) in.readObject()));
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getName());
		out.writeObject(isDigital());
		out.writeObject(getSection());
		
		out.writeObject(getValue());
		out.writeObject(getComment());	
	}

}
