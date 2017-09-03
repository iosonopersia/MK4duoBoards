package utilities;

import java.util.ArrayList;
import java.util.List;

import model.Board;
import model.Pin;


public class PinChecker {
	//Utility class which implements methods useful to check that there aren't duplicates amongst the pins of a Board.
	
	public static List<List<String>> findDuplicatesInDigitalPins(Board board){
		List<List<String>> duplicatesNames= new ArrayList<>();
		List<Pin> nonDuplicates= new ArrayList<>();
		List<Integer> duplicatesValues= new ArrayList<>();
		
		
		for(Pin p : board.getPinList()){
			if(p.isDigital()==true && p.getValue()!=Const.UNDEFINED_PIN){
				findDuplicates(p, nonDuplicates, duplicatesNames, duplicatesValues);
			}
		}
		
		return duplicatesNames;
	}
	
	public static List<List<String>> findDuplicatesInAnalogPins(Board board){
		List<List<String>> duplicatesNames= new ArrayList<>();
		List<Pin> nonDuplicates= new ArrayList<>();
		List<Integer> duplicatesValues= new ArrayList<>();
		
		for(Pin p : board.getPinList()){
			if(p.isDigital()==false && p.getValue()!=Const.UNDEFINED_PIN){
				findDuplicates(p, nonDuplicates, duplicatesNames, duplicatesValues);
			}
		}
		return duplicatesNames;
	}
	
	private static void findDuplicates(Pin currentPin, List<Pin> nonDuplicates, List<List<String>> duplicatesNames, List<Integer> duplicatesValues){
		Boolean foundInDuplicates= false;
		//We retrieve some info about the pin
		Integer currentPinValue= currentPin.getValue();
		String currentPinName= currentPin.getName();
		
		//We search our pin in all of the duplicatesNames internal lists: each of them has a related entry
		//in the duplicatesValues list.
		//It seems complicate but is worse :(
		for(int i=0; i< duplicatesNames.size(); i++){
			//If we find that the value of our pin is actually already present in the duplicatesValues list,
			//it means that we just have to add the name of our pin in the correct duplicatesNames internal list.
			//We also set a flag to indicate that we've found the pin and we exit the for loop since we've already done our job :) 
			if(duplicatesValues.get(i).equals(currentPinValue)){
				duplicatesNames.get(i).add(currentPinName);
				foundInDuplicates= true;
				continue;
			}

		}
		
		//If we haven't found the pin in none of the duplicateNames internal lists,
		//we check if it's already defined in the nonDuplicates pins.
		if(foundInDuplicates== false){
			//If the value of our pin is already contained in the nonDuplicates list,
			//we have to add a new internal list in duplicatesNames list. This list should
			//contain both the name of our pin and the name of the already present pin.
			Boolean containedInNonDuplicates= false;
			Pin duplicatedPin;
			for(Pin p : nonDuplicates){
				if(p.getValue()== currentPinValue){
					containedInNonDuplicates=true;
					duplicatedPin= p;
					//We remove the duplicated pin from the nonDuplicates list.
					nonDuplicates.remove(duplicatedPin);
					
					//We create a new duplicatesNames internal list. This
					//will contain both the current pin and the old pin
					//which has the same value of the current one.
					List<String> newDuplicatesNamesList= new ArrayList<>();
					newDuplicatesNamesList.add(duplicatedPin.getName());
					newDuplicatesNamesList.add(currentPinName);
					
					//we update the duplicatesNames and the duplicatesValues lists.
					duplicatesNames.add(newDuplicatesNamesList);
					duplicatesValues.add(currentPinValue);
					break;
				}
			}
			if(containedInNonDuplicates==false){
				//Only now we are sure that this pin is not a duplicate!
				//So we add it to the nonDuplicate list :)
				nonDuplicates.add(currentPin);
			}
		}
	
	}
}
