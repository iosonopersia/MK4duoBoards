package model;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import persistence.ConfigPersister;

public class Board implements Externalizable{
	
	private List<Pin> pinList;
	
	//Properties
	private StringProperty name, fileName, description, unknownPins, ifBlocks;
	private ObjectProperty<Microcontroller> microcontroller;
	
	public Board(){
		setName(Const.EMPTY);
		setFileName(Const.UNDEFINED_FILENAME);
		setDescription(Const.EMPTY);
		setMicrocontroller(new Microcontroller(Const.UNDEFINED_MICROCONTROLLER, Const.EMPTY));

		this.pinList=new ArrayList<>();
		for(Section section : ConfigPersister.getKnownPins()){
			for(String pinName: section.getPins()){
				pinList.add(new Pin(pinName, section.isDigital(), section.getName()));
			}
		}
		
		setUnknownPins(Const.EMPTY);
		setIfBlocks(Const.EMPTY);
	}
	
	public final List<Pin> getPinList() {
		return pinList;
	}
	
	public final Pin getPinByNameAndSection(String name, String section){
		for(Pin s : getPinList()){
			if(s.getName().equals(name) && s.getSection().equals(section)){
				return s;
			}
		}
		return null;
	}
	
	//############################################################
	//                        PROPERTIES
	//############################################################
	
	public final StringProperty nameProperty(){
		if(name== null){
			name= new SimpleStringProperty(this, "name", Const.UNDEFINED_NAME);
		}
		return name;
	}
	
	public void setName(final String name){
		nameProperty().set(name);
	}
	
	public String getName() {
		return nameProperty().get();
	}
	
	//------------------------------------------------------------
	public final StringProperty fileNameProperty() {
		if(fileName== null){
			fileName= new SimpleStringProperty(this, "fileName", Const.UNDEFINED_FILENAME);
		}
		return fileName;
	}
	
	public String getFileName() {
		return fileNameProperty().get();
	}

	public void setFileName(String fileName) {
		fileNameProperty().set(fileName);
	}
	
	//------------------------------------------------------------
	public final StringProperty descriptionProperty(){
		if(description== null){
			description= new SimpleStringProperty(this, "description", Const.EMPTY);
		}
		return description;
	}
	public String getDescription() {
		return descriptionProperty().get();
	}

	public void setDescription(String description) {
		descriptionProperty().set(description);
	}
	
	//------------------------------------------------------------
	public final StringProperty ifBlocksProperty() {
		if(ifBlocks== null){
			ifBlocks= new SimpleStringProperty(this, "ifBlocks", Const.EMPTY);
		}
		return ifBlocks;
	}
	
	public String getIfBlocks() {
		return ifBlocksProperty().get();
	}

	public void setIfBlocks(String additionalPins) {
		ifBlocksProperty().set(additionalPins);
	}
	
	//------------------------------------------------------------
	public final StringProperty unknownPinsProperty() {
		if(unknownPins== null){
			unknownPins= new SimpleStringProperty(this, "unknownPins", Const.EMPTY);
		}
		return unknownPins;
	}
	
	public String getUnknownPins() {
		return unknownPinsProperty().get();
	}

	public void setUnknownPins(String unknownPins) {
		unknownPinsProperty().set(unknownPins);
	}
	
	//------------------------------------------------------------
	public final ObjectProperty<Microcontroller> microcontrollerProperty() {
		if(microcontroller== null){
			microcontroller= new SimpleObjectProperty<Microcontroller>(this,
									"microcontroller",
									new Microcontroller(Const.UNDEFINED_MICROCONTROLLER, Const.EMPTY)
								);
		}
		return microcontroller;
	}
	
	public Microcontroller getMicrocontroller() {
		return microcontrollerProperty().get();
	}

	public void setMicrocontroller(Microcontroller microcontroller) {
		microcontrollerProperty().set(microcontroller);
	}

	
	@Override
	public String toString() {
		StringBuilder sb=new StringBuilder();
		sb.append("[")
		.append(getFileName())
		.append("] ")
		.append(getName());
		return sb.toString();
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		setName((String) in.readObject());
		setFileName((String) in.readObject());
		setDescription((String) in.readObject());
		setMicrocontroller((Microcontroller) in.readObject());
		setUnknownPins((String) in.readObject());
		setIfBlocks((String) in.readObject());
		
		getPinList().clear();
		int size= in.readInt();
		for(int i=0; i<size; i++){
			getPinList().add((Pin) in.readObject());
		}	
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeObject(getName());
		out.writeObject(getFileName());
		out.writeObject(getDescription());
		out.writeObject(getMicrocontroller());
		out.writeObject(getUnknownPins());
		out.writeObject(getIfBlocks());
		
		out.writeInt(getPinList().size());
		for(Pin p: getPinList()){
			out.writeObject(p);
		}
		
	}

}
