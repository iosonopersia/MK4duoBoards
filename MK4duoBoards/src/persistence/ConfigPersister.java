package persistence;

//############################################################
//                    PATTERN SINGLETON
//############################################################

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.StringTokenizer;

import javafx.application.Platform;
import model.Microcontroller;
import model.Section;
import utilities.Const;
import utilities.SecureTokenizer;

public class ConfigPersister{
	
	private Path configFile, knownPinsFile, microcontrollersFile;
	
	private  Locale locale;
	private  Path lastDirFiles, lastDirDB;
	private static List<Section> knownPins;
	private static LinkedHashMap<String, Microcontroller> microcontrollers;
	
	private static ConfigPersister instance;
	
	private ConfigPersister(){
		Path configDir = Paths.get(System.getProperty("user.home"));
		configDir= configDir.resolve(Const.CONFIG_DIR);
		
		configFile= configDir.resolve(Const.CONFIG_FILE);
		knownPinsFile= configDir.resolve(Const.KNOWN_PINS_FILE);
		microcontrollersFile= configDir.resolve(Const.MICROCONTROLLERS_FILE);
		
		ensureConfigFileExistence(configDir);
		
		
		try {
			parseConfigs();
			parseKnownPins();
			parseMicrocontrollers();
		} catch (IOException e) {
			locale= new Locale("en", "US");
			lastDirFiles= Paths.get(System.getProperty("user.home"));
			lastDirDB= Paths.get(System.getProperty("user.home"));
			knownPins= new ArrayList<>();
			microcontrollers= new LinkedHashMap<>();
		}

	}

	private void ensureConfigFileExistence(Path configDir) {
		if(Files.exists(configFile)==false || Files.isRegularFile(configFile)==false || Files.isWritable(configFile)==false){
			try {
				Files.createDirectories(configDir);
				Files.createFile(configFile);
			} catch (IOException e) {
				e.printStackTrace();
				Platform.exit();
			}
		}
		
		if(Files.exists(knownPinsFile)==false || Files.isRegularFile(knownPinsFile)==false || Files.isWritable(knownPinsFile)==false){
			try {
				Files.createDirectories(configDir);
				Files.createFile(knownPinsFile);
			} catch (IOException e) {
				e.printStackTrace();
				Platform.exit();
			}
		}
		
		if(Files.exists(microcontrollersFile)==false || Files.isRegularFile(microcontrollersFile)==false || Files.isWritable(microcontrollersFile)==false){
			try {
				Files.createDirectories(configDir);
				Files.createFile(microcontrollersFile);
			} catch (IOException e) {
				e.printStackTrace();
				Platform.exit();
			}
		}
	}

	
	public static ConfigPersister getInstance(){
		if(instance==null){
			instance= new ConfigPersister();
		}
		return instance;
	}
	
	private void parseConfigs() throws IOException{
		Reader reader= new FileReader(configFile.toFile());
		BufferedReader br= new BufferedReader(reader);
		
		if(br.ready()==false){
			br.close();
			throw new IOException();
		}
		
		String line;
		while((line= br.readLine()) != null){
			StringTokenizer st= new StringTokenizer(line);
			int numOfTokens= st.countTokens();
			if(numOfTokens == 0){
				continue;
			}
			String firstToken= SecureTokenizer.readToken(st);
			
			if(numOfTokens==Const.NUM_OF_LOCALE_TOKENS && firstToken.equals(Const.LOCALE_TOKEN)){
				locale= new Locale(SecureTokenizer.readToken(st), SecureTokenizer.readToken(st));
			}
			
			else if(numOfTokens>=Const.NUM_OF_LASTDIR_FILES_MINIMUM_TOKENS && firstToken.equals(Const.LASTDIR_FILES_TOKEN)){
				Path lastDir= Paths.get(SecureTokenizer.readFileNameWithSpaces(st));
				lastDirFiles= (Files.exists(lastDir) && Files.isDirectory(lastDir)) ? lastDir : Paths.get(System.getProperty("user.home"));
			}
			
			else if(numOfTokens>=Const.NUM_OF_LASTDIR_DB_MINIMUM_TOKENS && firstToken.equals(Const.LASTDIR_DB_TOKEN)){
				Path lastDir= Paths.get(SecureTokenizer.readFileNameWithSpaces(st));
				lastDirDB= (Files.exists(lastDir) && Files.isDirectory(lastDir)) ? lastDir : Paths.get(System.getProperty("user.home"));
			}
		}
		br.close();
	}
	
	private void parseKnownPins() throws IOException {
		Reader reader= new FileReader(knownPinsFile.toFile());
		BufferedReader br= new BufferedReader(reader);
		
		if(br.ready()==false){
			br.close();
			throw new IOException();
		}
		
		knownPins= new ArrayList<>();
		
		String currentSection= null;
		Boolean replaceTokenActive= false;
		Integer minReplace= 0, maxReplace= 0;
		String line;
		while((line= br.readLine())!=null){
			StringTokenizer st= new StringTokenizer(line);
			if(st.hasMoreTokens()==false){
				continue;
			}
			String firstToken= SecureTokenizer.readToken(st);
			
			if(firstToken.startsWith(Const.START_TOKEN_PREFIX)){
				firstToken= firstToken.substring(Const.START_TOKEN_PREFIX_NUM_OF_CHARS, firstToken.length());
				
				
				if(firstToken==null || firstToken.equals(Const.EMPTY)){
					br.close();
					return;
				}
				
				Boolean isAnalog;

				String secondToken= SecureTokenizer.readToken(st);
				isAnalog= secondToken.equals(Const.IS_ANALOG_TOKEN);

				if(st.hasMoreTokens()){
					if(SecureTokenizer.readToken(st).equals(Const.ENUM_TOKEN_START)){
						replaceTokenActive= true;
						minReplace= Integer.parseInt(SecureTokenizer.readToken(st));
						maxReplace= Integer.parseInt(SecureTokenizer.readToken(st));
					}
				}else{
					replaceTokenActive= false;
				}
				
				currentSection= firstToken;
				if(replaceTokenActive){
					for(Integer i= minReplace; i<=maxReplace; ++i){
						addSection(currentSection.replaceAll(Const.ENUM_TO_REPLACE_TOKEN, i.toString()), isAnalog);
					}
				}else{
					addSection(currentSection, isAnalog);
				}
			}else{
				if(firstToken!=null && firstToken.equals(Const.EMPTY)==false){
					if(replaceTokenActive){
						for(Integer i= minReplace; i<=maxReplace; ++i){
							addPinName(currentSection.replaceAll(Const.ENUM_TO_REPLACE_TOKEN, i.toString()), 
									firstToken.replaceAll(Const.ENUM_TO_REPLACE_TOKEN, i.toString()));
						}
					}else{
						addPinName(currentSection, firstToken);
					}
				}
			}
		}
		br.close();
	}
	
	private void parseMicrocontrollers() throws IOException {
		Reader reader= new FileReader(microcontrollersFile.toFile());
		BufferedReader br= new BufferedReader(reader);
		
		if(br.ready()==false){
			br.close();
			throw new IOException();
		}
		
		microcontrollers= new LinkedHashMap<>();
		
		addMicrocontroller(new Microcontroller(Const.UNDEFINED_MICROCONTROLLER, Const.EMPTY));
		String currentChip= null;
		String checkCode= Const.EMPTY;
		String line;
		StringBuilder sb= new StringBuilder();
		while((line= br.readLine())!=null){
			StringTokenizer st= new StringTokenizer(line);
			if(st.hasMoreTokens()==false){
				continue;
			}
			String firstToken= SecureTokenizer.readToken(st);
			
			if(firstToken.startsWith(Const.START_TOKEN_PREFIX) && st.countTokens()== Const.NUM_OF_CHIP_START_TOKENS -1){
				firstToken= firstToken.substring(Const.START_TOKEN_PREFIX_NUM_OF_CHARS, firstToken.length());
				
				if(firstToken==null || firstToken.equals(Const.EMPTY)){
					br.close();
					return;
				}
				checkCode= sb.toString();
				if(currentChip != null){
					addMicrocontroller(new Microcontroller(currentChip, checkCode));
					sb= new StringBuilder();
				}

				currentChip= firstToken;
				
			}else{
				sb.append(line);
				sb.append(Const.EOL);
			}
		}
		checkCode= sb.toString();
		addMicrocontroller(new Microcontroller(currentChip, checkCode));
		br.close();
	}

	private void addSection(String currentSection, Boolean isDigital) {
		Boolean found= false;
		for(Section s: knownPins){
			if(s.getName().equals(currentSection)){
				found= true;
				break;
			}
		}
		
		if(found==false){
			knownPins.add(new Section(currentSection, isDigital));
		}
	}

	private void addPinName(String currentSection, String pinName) {
		for(Section section: knownPins){
			if(section.getName().equals(currentSection)){
				section.getPins().add(pinName);
				break;
			}
		}
		
	}
	
	private void addMicrocontroller(Microcontroller toAdd){
		if(microcontrollers.containsKey(toAdd.getName())==false){
			microcontrollers.put(toAdd.getName(), toAdd);
		}
	}

	public void saveConfigs() throws IOException {
		Writer w= new FileWriter(configFile.toFile());
		BufferedWriter writer= new BufferedWriter(w);
		
		writer.write(Const.LOCALE_TOKEN);
		writer.write(Const.SPACE);
		writer.write(locale.getLanguage());
		writer.write(Const.SPACE);
		writer.write(locale.getCountry());
		writer.write(Const.EOL);
		writer.write(Const.LASTDIR_FILES_TOKEN);
		writer.write(Const.SPACE);
		writer.write(lastDirFiles.toAbsolutePath().toString());
		writer.write(Const.EOL);
		writer.write(Const.LASTDIR_DB_TOKEN);
		writer.write(Const.SPACE);
		writer.write(lastDirDB.toAbsolutePath().toString());
		writer.write(Const.EOL);
		
		writer.flush();
		writer.close();
	}

	public Locale getLocale() {
		return locale;
	}
	
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public Path getLastDirFiles() {
		return lastDirFiles;
	}

	public void setLastDirFiles(Path lastDirFiles) {
		this.lastDirFiles = lastDirFiles;
	}

	public Path getLastDirDB() {
		return lastDirDB;
	}

	public void setLastDirDB(Path lastDirDB) {
		this.lastDirDB = lastDirDB;
	}
	
	public final static List<Section> getKnownPins() {
		return knownPins;
	}

	public final static Microcontroller getChip(String chipName){
		return microcontrollers.get(chipName);
	}
	
	public final static Set<String> getChipNames(){
		return microcontrollers.keySet();
	}
	
	public static String getSectionNameOf(String pinNameToken) {
		for(Section s: getKnownPins()){
			for(String pinName: s.getPins()){
				if(pinName.equals(pinNameToken)){
					return s.getName();
				}
			}
		}
		return null;
	}
	
}
