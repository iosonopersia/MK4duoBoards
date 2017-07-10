package view;

import java.util.Locale;
import i18n.i18n;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import model.DataManager;

public class AppMenuBar extends MenuBar {
	
	private Menu menuData, menuImport, menuSave, menuTools, menuSettings, menuLanguage;
	private MenuItem importDB, importFromFiles, saveDB, exportBoards, pinCheck, info;
	private RadioMenuItem italiano, english;
	private ToggleGroup languagesGroup;
	
	public AppMenuBar(DataManager model, i18n lang){
		menuData= new Menu(lang.getString("Menu.DATA"));
		menuImport= new Menu(lang.getString("Menu.DATA.IMPORT"));
		menuSave= new Menu(lang.getString("Menu.SAVE"));
		
		importDB= new MenuItem(lang.getString("Menu.DATA.IMPORT.IMPORT_DB"));
		importFromFiles= new MenuItem(lang.getString("Menu.DATA.IMPORT.PARSE_FILES"));
		
		saveDB=  new MenuItem(lang.getString("Menu.SAVE.SAVE_DB"));
		exportBoards= new MenuItem(lang.getString("Menu.SAVE.EXPORT_BOARDS"));
		
		menuTools= new Menu(lang.getString("Menu.TOOLS"));
		pinCheck= new MenuItem(lang.getString("Menu.TOOLS.PIN_CHECK"));
		
		menuSettings= new Menu(lang.getString("Menu.SETTINGS"));
		info= new MenuItem(lang.getString("Info.TITLE"));
		menuLanguage= new Menu(lang.getString("Menu.TOOLS.LOCALE"));
		
		languagesGroup=new ToggleGroup();
		english= new RadioMenuItem(lang.getString("Menu.TOOLS.LOCALE.EN_US"));
		italiano= new RadioMenuItem(lang.getString("Menu.TOOLS.LOCALE.IT_IT"));
		
		english.setToggleGroup(languagesGroup);
		italiano.setToggleGroup(languagesGroup);

		if(model.getLocale().equals(new Locale("it","IT"))){
			italiano.setSelected(true);
		}else if(model.getLocale().equals(new Locale("en", "US"))){
			english.setSelected(true);
		}
		
		menuLanguage.getItems().addAll(english, italiano);
		
		menuImport.getItems().addAll(importDB, importFromFiles);
		menuSave.getItems().addAll(saveDB, exportBoards);
		menuData.getItems().addAll(menuImport,menuSave);
		
		menuTools.getItems().addAll(pinCheck);
		menuSettings.getItems().addAll(info, menuLanguage);
		this.getMenus().addAll(menuData, menuTools, menuSettings);		
	}


	public final MenuItem getImportDB() {
		return importDB;
	}

	public final MenuItem getImportFromFiles() {
		return importFromFiles;
	}

	public final MenuItem getSaveDB() {
		return saveDB;
	}

	public final MenuItem getExportBoards() {
		return exportBoards;
	}
	
	public final MenuItem getPinCheck(){
		return pinCheck;
	}

	public final Menu getInfo() {
		return menuSettings;
	}


	public final ToggleGroup getLanguagesGroup() {
		return languagesGroup;
	}


	public final RadioMenuItem getItaliano() {
		return italiano;
	}


	public final RadioMenuItem getEnglish() {
		return english;
	}
}
