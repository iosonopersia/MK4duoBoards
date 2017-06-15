package model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import persistence.DBPersister;
import persistence.BoardPersister;
import persistence.ConfigPersister;

public class DataManager{
	private List<Board> boardList;
	
	private BoardPersister boardPersister;
	private DBPersister dbPersister;
	private ConfigPersister configPersister;
	
	public DataManager(){
		boardList= new ArrayList<>();
		
		boardPersister= new BoardPersister();
		dbPersister= new DBPersister();
		configPersister= ConfigPersister.getInstance(); 
	}
	
	
	public void importDB(Path database) throws FileNotFoundException, ClassNotFoundException, IOException {
		FileInputStream fis= new FileInputStream(database.toFile());
		this.boardList= dbPersister.loadDB(fis);
		setLastDirDB(database.getParent());
	}
	
	
	public void saveDB(Path database) throws IOException{
		FileOutputStream fos= new FileOutputStream(database.toFile());
		dbPersister.saveDB(fos, this.getBoards());
		setLastDirDB(database.getParent());
	}
	
	public void exportFiles(List<Board> list, Path rootDir) throws IOException {
		boardPersister.export(list, rootDir);
		setLastDirFiles(rootDir);
	}
	
	public void importFromFiles(List<Path> headerFiles) throws ClassNotFoundException, NumberFormatException, IOException {
		this.boardList= boardPersister.parse(headerFiles);
		setLastDirFiles(headerFiles.get(0).getParent());
	}
	
	
	public void saveConfigs(){
		try {
			configPersister.saveConfigs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public Locale getLocale() {
		return configPersister.getLocale();
	}
	
	
	public void setLocale(Locale locale){
		if(locale!=null){
			configPersister.setLocale(locale);
		}
		saveConfigs();
	}

	
	public Path getLastDirFiles() {
		return configPersister.getLastDirFiles();
	}
	
	public void setLastDirFiles(Path lastDirFiles){
		if(lastDirFiles!=null){
			configPersister.setLastDirFiles(lastDirFiles);
		}
		saveConfigs();
	}

	public Path getLastDirDB() {
		return configPersister.getLastDirDB();
	}
	
	public void setLastDirDB(Path lastDirDB){
		if(lastDirDB!=null){
			configPersister.setLastDirDB(lastDirDB);
		}
		saveConfigs();
	}

	public List<Section> getKnownPins(){
		return ConfigPersister.getKnownPins();
	}
	
	public List<Board> getBoards() {
		return this.boardList;
	}
	
}
