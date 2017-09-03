package view;

import javafx.application.Platform;
import javafx.beans.Observable;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import model.Board;
import model.DataManager;
import model.Pin;
import persistence.ConfigPersister;
import utilities.Const;
import utilities.PinChecker;
import i18n.i18n;

public class PrimaryPane extends BorderPane{
	//VIEW
	private AppMenuBar menuBar; 		
	private SplitPane splitPane;
	private BoardListView boardList;
	private EditingTabPane editingTabs; 
	
	//VIEWMODEL
	private ObservableList<Board> boards;
	private ObservableList<Pin> pins;
	private Stage primaryStage;
	
	public PrimaryPane(DataManager model, i18n lang, Stage primaryStage){
		
	    boards= FXCollections.observableList(model.getBoards(), new Callback<Board, Observable[]>() {
	        @Override
	        public Observable[] call(Board b) {
	            return new Observable[] {b.nameProperty(), b.fileNameProperty(), b.unknownPinsProperty(), b.ifBlocksProperty(), b.microcontrollerProperty()};
	        }
	        
	    });
	    
	    pins= FXCollections.observableList(new ArrayList<>(), new Callback<Pin, Observable[]>() {
	        @Override
	        public Observable[] call(Pin p) {
	            return new Observable[] {p.valueProperty(), p.commentProperty()};
	        }
		});
	    
	    this.primaryStage= primaryStage;
	    
		this.primaryStage.setOnCloseRequest(event->{
			if(boards.isEmpty()==false){
				Alert alert=new Alert(AlertType.CONFIRMATION);
				alert.initOwner(primaryStage);
				alert.setTitle(lang.getString("Stage.CLOSE.CONFIRMATION_TITLE"));
				alert.setHeaderText(lang.getString("Stage.CLOSE.CONFIRMATION_HEADER"));
				alert.setContentText(lang.getString("Stage.CLOSE.CONFIRMATION_CONTENT"));
				alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
				alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.CANCEL);
			
				Optional<ButtonType> result= alert.showAndWait();
				if(result.get()==ButtonType.YES){
					primaryStage.close();
					Platform.exit();
				}else if(result.isPresent()==false || result.get()!=ButtonType.YES){
					event.consume();
				}
			}
		});
		
	    boardList= new BoardListView(model, lang);
	    boardList.setItems(boards);
	    contextMenuItemsSetOnAction(model, lang);
	    
	    menuBar= new AppMenuBar(model, lang);
		menuItemsSetOnAction(model, lang);
		
	    
	    boardList.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) ->{	
    		if(newValue!=null){
    			editingTabs.getUnknownPinsTab().getUnknownPinsArea().setText(newValue.getUnknownPins());
    			editingTabs.getIfBlocksTab().getIfBlocksArea().setText(newValue.getIfBlocks());
    			if(newValue.getMicrocontroller().getName().equals(Const.UNDEFINED_MICROCONTROLLER)){
    				editingTabs.getMetadataTab().getMicrocontroller().getSelectionModel().selectFirst();
    			}else{
    				editingTabs.getMetadataTab().getMicrocontroller().getSelectionModel().select(newValue.getMicrocontroller().getName());
    			}
    			editingTabs.getMetadataTab().getBoardName().setText(newValue.getName());
    			editingTabs.getMetadataTab().getFileName().setText(newValue.getFileName());
    			editingTabs.getMetadataTab().getDescription().setText(newValue.getDescription());
    			pins.clear();
    			pins.addAll(newValue.getPinList());
    			editingTabs.getMainPinsTab().setItemsForEveryTable(pins);
    			editingTabs.getExtrudersPinsTab().setItemsForEveryTable(pins);
    			editingTabs.getServosPinsTab().setItemsForEveryTable(pins);
    			
    		}else{
    			clearEditingTabsContent();
    		}
        	
        });
	    
	    
	    editingTabs= new EditingTabPane(model, lang);
		editingTabsListeners();
		
		VBox vBoxEditingTabs=new VBox();
		vBoxEditingTabs.setSpacing(10);
		vBoxEditingTabs.setPadding(new Insets(10, 0, 0, 0));
		vBoxEditingTabs.setAlignment(Pos.CENTER);
		vBoxEditingTabs.getChildren().addAll(new ImageView("MK4duoIcon_125x125.png"), editingTabs);
		
		splitPane= new SplitPane();
		splitPane.setDividerPositions(0.1);
		
		SplitPane.setResizableWithParent(boardList, false);
		SplitPane.setResizableWithParent(editingTabs, true);
		splitPane.getItems().addAll(boardList, vBoxEditingTabs);
		
        
		this.setTop(menuBar);
		this.setCenter(splitPane);
	}

	
	//############################################################
	//                         MENU BAR
	//############################################################
	private void menuItemsSetOnAction(DataManager model, i18n lang) {
		
		menuBar.getImportDB().setOnAction(event -> {
					Optional<ButtonType> result= showConfirmationDialog(lang);
					
					if(result.get()==ButtonType.YES){
						FileChooser fc= new FileChooser();
						fc.setTitle(lang.getString("FileChooser.IMPORT_DB.TITLE"));
						fc.setInitialDirectory(model.getLastDirDB().toFile());
						File database= fc.showOpenDialog(primaryStage);
						if(database!=null){
								try {
									model.importDB(database.toPath());
								} catch (ClassNotFoundException | IOException e) {
									e.printStackTrace();
								}
								boards.addAll(model.getBoards());
						}
					}
		});
				
		menuBar.getImportFromFiles().setOnAction(event -> {
			Optional<ButtonType> result= showConfirmationDialog(lang);
			
			if(result.get()==ButtonType.YES){
				FileChooser fc= new FileChooser();
				fc.setTitle(lang.getString("FileChooser.PARSE_FILES.TITLE"));
				fc.setInitialDirectory(model.getLastDirFiles().toFile());
				List<File> files= fc.showOpenMultipleDialog(primaryStage);
				
				if(files!=null && files.isEmpty()== false){
					model.importFromFiles(files);
					boards.addAll(model.getBoards());
					int numOfNonParsedFiles= files.size() - model.getBoards().size();
					if(numOfNonParsedFiles > 0){
						showErrorFileNotParsed(numOfNonParsedFiles, lang);
					}
				}
			}
		});
		
		menuBar.getSaveDB().setOnAction(event->{
			FileChooser fc= new FileChooser();
			fc.setTitle(lang.getString("FileChooser.SAVE_DB.TITLE"));
			fc.setInitialDirectory(model.getLastDirDB().toFile());
			File database= fc.showSaveDialog(primaryStage);

			if(database!=null){
				try {
					model.saveDB(database.toPath());
				} catch (IOException e1) {
					showError(lang);
				}
			}
		});
		
		menuBar.getExportBoards().setOnAction(event->{
			showExportBoards(model.getBoards(), model, lang);
		});
		
		menuBar.getPinCheck().setOnAction(event-> {
			showPinCheck(lang);
		});
		
		menuBar.getInfo().setOnAction(event->{
			showInfoDialog(lang);
		});
		
		menuBar.getSaveDB().disableProperty().bind(Bindings.isEmpty(boardList.getItems()));
		menuBar.getExportBoards().disableProperty().bind(Bindings.isEmpty(boardList.getItems()));
		
		menuBar.getLanguagesGroup().selectedToggleProperty().addListener((obj, oldValue, newValue)->{
			if(newValue!=null){
				showRebootWarningDialog(lang);
				
				if(newValue==menuBar.getItaliano()){
					model.setLocale(new Locale("it", "IT"));
				}else if(newValue==menuBar.getEnglish()){
					model.setLocale(new Locale("en", "US"));
				}
			}
		});
		
	}


	//############################################################
	//                  BOARDLIST CONTEXT MENU
	//############################################################
	private void contextMenuItemsSetOnAction(DataManager model, i18n lang){
		boardList.getContextMenuNewBoard().setOnAction(event->{
			boards.add(new Board());
			boardList.getSelectionModel().selectLast();
		});
		
		boardList.getContextMenuRemoveBoard().setOnAction(event->{
			if(showRemoveConfirmation(lang)){
				boards.remove(boardList.getSelectionModel().getSelectedIndex());
			}else{
				showAbortedOperation(lang);
			}
		});
		
		boardList.getContextMenuDisableSelected().setOnAction(event->{
			boardList.getSelectionModel().clearSelection();
		});
		
		boardList.getContextMenuRemoveEveryBoard().setOnAction(event->{
			if(showRemoveConfirmation(lang)){
				boards.clear();
			}else{
				showAbortedOperation(lang);
			}
		});
		
		boardList.getContextMenuPinCheck().setOnAction(event->{
			showPinCheck(lang);
		});
		
		boardList.getContextMenuExportBoard().setOnAction(event->{
			List<Board> list= new ArrayList<>();
			list.add(boardList.getSelectionModel().getSelectedItem());
			showExportBoards(list, model, lang);
		});
		
		boardList.getContextMenuSortBoards().setOnAction(event->{
			boards.sort((board0, board1)-> {
				String fileName0=board0.getFileName();
				Integer file0= Integer.parseInt(fileName0.substring(0, fileName0.length()-2));
				String fileName1= board1.getFileName();
				Integer file1= Integer.parseInt(fileName1.substring(0, fileName1.length()-2));
				return file0.compareTo(file1);
			});
		});
		
		boardList.getContextMenuExportBoard().disableProperty().bind(boardList.getSelectionModel().selectedItemProperty().isNull());
		boardList.getContextMenuRemoveBoard().disableProperty().bind(boardList.getSelectionModel().selectedItemProperty().isNull());
		boardList.getContextMenuRemoveEveryBoard().disableProperty().bind(Bindings.isEmpty(boardList.getItems()));
		boardList.getContextMenuSortBoards().disableProperty().bind(Bindings.isEmpty(boardList.getItems()));
		boardList.getContextMenuDisableSelected().disableProperty().bind(boardList.getSelectionModel().selectedItemProperty().isNull());
		boardList.getContextMenuPinCheck().disableProperty().bind(boardList.getSelectionModel().selectedItemProperty().isNull());
	}


	//############################################################
	//                       EDITING TABS
	//############################################################
	private void editingTabsListeners() {
		editingTabs.getIfBlocksTab().getIfBlocksArea().textProperty().addListener((obj, oldValue, newValue)->{
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				if(newValue==null)
					boardList.getSelectionModel().getSelectedItem().setIfBlocks("");
				else
					boardList.getSelectionModel().getSelectedItem().setIfBlocks(newValue);
			}
		});
		
		editingTabs.getUnknownPinsTab().getUnknownPinsArea().textProperty().addListener((obj, oldValue, newValue)->{
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				if(newValue==null)
					boardList.getSelectionModel().getSelectedItem().setUnknownPins("");
				else
					boardList.getSelectionModel().getSelectedItem().setUnknownPins(newValue);
			}
		});
		
		editingTabs.getMetadataTab().getMicrocontroller().getSelectionModel().selectedItemProperty().addListener((obj, oldValue, newValue)->{
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				boardList.getSelectionModel().getSelectedItem().setMicrocontroller(ConfigPersister.getChip(newValue));
				editingTabs.getMetadataTab().getCheckCode().setText(ConfigPersister.getChip(newValue).getCheckCode());
			}
		});
		
		editingTabs.getMetadataTab().getBoardName().textProperty().addListener((Obj,oldValue,newValue)-> {
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				if(newValue==null)
					boardList.getSelectionModel().getSelectedItem().setName("");
				else
					boardList.getSelectionModel().getSelectedItem().setName(newValue);
			}
		});
		
		editingTabs.getMetadataTab().getFileName().textProperty().addListener((Obj,oldValue,newValue)-> {
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				if(newValue==null)
					boardList.getSelectionModel().getSelectedItem().setFileName("");
				else
					boardList.getSelectionModel().getSelectedItem().setFileName(newValue);
			}
		});
		
		editingTabs.getMetadataTab().getDescription().textProperty().addListener((Obj,oldValue,newValue)-> {
			if(boardList.getSelectionModel().getSelectedItem()!=null){
				if(newValue==null)
					boardList.getSelectionModel().getSelectedItem().setDescription("");
				else
					boardList.getSelectionModel().getSelectedItem().setDescription(newValue);
			}
		});
		
	}
	
	private void clearEditingTabsContent() {
		pins.clear();
		editingTabs.getIfBlocksTab().getIfBlocksArea().setText("");
		editingTabs.getUnknownPinsTab().getUnknownPinsArea().setText("");
		editingTabs.getMetadataTab().getBoardName().setText("");
		editingTabs.getMetadataTab().getFileName().setText("");
		editingTabs.getMetadataTab().getDescription().setText("");
		editingTabs.getMetadataTab().getMicrocontroller().getSelectionModel().selectFirst();
		editingTabs.getMetadataTab().getCheckCode().setText("");
	}


	//############################################################
	//                        DIALOGS
	//############################################################
	
	private Optional<ButtonType> showConfirmationDialog(i18n lang) {
		Alert alert=new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("Menu.IMPORT_DATA.CONFIRMATION_TITLE"));
		alert.setHeaderText(lang.getString("Menu.IMPORT_DATA.CONFIRMATION_HEADER"));
		alert.setContentText(lang.getString("Menu.IMPORT_DATA.CONFIRMATION_CONTENT"));
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getButtonTypes().setAll( ButtonType.YES, ButtonType.NO);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		return alert.showAndWait();
	}
	
	private void showExportBoards(List<Board> list, DataManager model, i18n lang) {
		DirectoryChooser fc= new DirectoryChooser();
		fc.setTitle(lang.getString("FileChooser.EXPORT_FILES.TITLE"));
		fc.setInitialDirectory(model.getLastDirFiles().toFile());
		File rootDir= fc.showDialog(primaryStage);

		if(rootDir!=null && Files.exists(rootDir.toPath()) && Files.isDirectory(rootDir.toPath())){
			try {
				model.exportFiles(list, rootDir.toPath());
			} catch (IOException e1) {
				showError(lang);
			}
			showDoneMessage(lang);
		}else{
			showAbortedOperation(lang);
		}
		
	}
	
	private void showDoneMessage(i18n lang){
		Alert done= new Alert(AlertType.INFORMATION);
		done.initOwner(primaryStage);
		done.setTitle(lang.getString("Done.TITLE"));
		done.setHeaderText(lang.getString("Done.HEADER"));
		done.setContentText(lang.getString("Done.CONTENT"));
		done.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		done.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		done.showAndWait();
	}
	
	private void showAbortedOperation(i18n lang){
		Alert done= new Alert(AlertType.WARNING);
		done.initOwner(primaryStage);
		done.setTitle(lang.getString("Aborted.TITLE"));
		done.setHeaderText(lang.getString("Aborted.HEADER"));
		done.setContentText(lang.getString("Aborted.CONTENT"));
		done.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		done.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		done.showAndWait();
	}
	
	private boolean showRemoveConfirmation(i18n lang) {
		Alert alert=new Alert(AlertType.CONFIRMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("ContextMenu.REMOVE_CONFIRM.TITLE"));
		alert.setHeaderText(lang.getString("ContextMenu.REMOVE_CONFIRM.HEADER"));
		alert.setContentText(lang.getString("ContextMenu.REMOVE_CONFIRM.CONTENT"));
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getButtonTypes().setAll( ButtonType.YES, ButtonType.NO);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		Optional<ButtonType> response= alert.showAndWait();
		if(response != null && response.isPresent() && response.get().equals(ButtonType.YES)){
			return true;
		}else{
			return false;
		}
	}
	
	private void showInfoDialog(i18n lang) {
		Alert alert=new Alert(AlertType.INFORMATION);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("Info.TITLE"));
		alert.setHeaderText(lang.getString("Info.HEADER"));
		StringBuilder sb= new StringBuilder();
		sb.append(Const.APP_NAME);
		sb.append(Const.SPACE);
		sb.append(lang.getString("Info.CONTENT.WAS_MADE_BY"));
		sb.append(Const.SPACE);
		sb.append("Simone Persiani [2017, Bologna, Italy]");
		sb.append(Const.SPACE);
		sb.append(lang.getString("Info.CONTENT.MADE_FOR_MK4DUO_COMMUNITY")+Const.SPACE+"MK4duo");
		sb.append(".");
		sb.append(System.lineSeparator());
		sb.append(System.lineSeparator());
		sb.append(lang.getString("Info.CONTENT.SOURCE_CODE_LOCATION"));
		sb.append(Const.SPACE);
		sb.append(Const.GITHUB_URL);
		alert.setContentText(sb.toString());
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		alert.showAndWait();
		
	}
	
	private void showError(i18n lang){
		Alert alert=new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("Errors.TITLE"));
		alert.setHeaderText(lang.getString("Errors.HEADER"));
		alert.setContentText(lang.getString("Errors.CONTENT"));
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	private void showErrorFileNotParsed(int numOfNonParsedFiles, i18n lang) {
		Alert alert=new Alert(AlertType.ERROR);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("Errors.FILE_NOT_PARSED.TITLE"));
		alert.setHeaderText(lang.getString("Errors.FILE_NOT_PARSED.HEADER"));
		alert.setContentText(lang.getString("Errors.FILE_NOT_PARSED.CONTENT") + Const.SPACE + numOfNonParsedFiles);
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	private void showRebootWarningDialog(i18n lang) {
		Alert alert= new Alert(AlertType.WARNING);
		alert.initOwner(primaryStage);
		alert.setTitle(lang.getString("Menu.LOCALE.WARNING_TITLE"));
		alert.setHeaderText(lang.getString("Menu.LOCALE.WARNING_HEADER"));
		alert.setContentText(lang.getString("Menu.LOCALE.WARNING_CONTENT"));
		alert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		alert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		alert.showAndWait();
	}
	
	private void showPinCheck(i18n lang){
		Board selectedBoard= boardList.getSelectionModel().getSelectedItem();
		if(selectedBoard==null){
			return;
		}
		List<List<String>> resultDigitals= PinChecker.findDuplicatesInDigitalPins(selectedBoard);
		List<List<String>> resultAnalogs= PinChecker.findDuplicatesInAnalogPins(selectedBoard);
		
		Alert pinCheckAlert= new Alert(AlertType.INFORMATION);
		pinCheckAlert.initOwner(primaryStage);
		pinCheckAlert.setTitle(lang.getString("PinChecker.TITLE"));
		pinCheckAlert.setHeaderText(lang.getString("PinChecker.HEADER"));
		StringBuilder sb= new StringBuilder();
		sb.append("DIGITAL PINS");
		sb.append(System.lineSeparator());
		for(List<String> list: resultDigitals){
			sb.append(list.toString());
			sb.append(System.lineSeparator());
		}
		sb.append(System.lineSeparator());
		sb.append("ANALOG PINS");
		sb.append(System.lineSeparator());
		for(List<String> list: resultAnalogs){
			sb.append(list.toString());
			sb.append(System.lineSeparator());
		}
		pinCheckAlert.setContentText(sb.toString());
		pinCheckAlert.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
		pinCheckAlert.getDialogPane().setMinWidth(Region.USE_PREF_SIZE);
		
		pinCheckAlert.showAndWait();
	}
}