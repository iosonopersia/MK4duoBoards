package view;

import i18n.i18n;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import model.DataManager;
import persistence.ConfigPersister;

public class MetadataTab extends Tab {
	private TextField boardName, fileName;
	private TextArea description, checkCode;
	private ChoiceBox<String> microcontroller;
	
	public MetadataTab(DataManager model, i18n lang){
		this.setText(lang.getString("Tabs.METADATA"));
		
		VBox metadata= new VBox();
		metadata.setSpacing(10);
		metadata.setPadding(new Insets(20, 20, 20, 20));
		
		Label boardNameLabel= new Label(lang.getString("Metadata.BOARD_NAME_LABEL"));
		boardName= new TextField();
		boardNameLabel.setLabelFor(boardName);
		
		Label fileNameLabel= new Label(lang.getString("Metadata.BOARD_FILENAME_LABEL"));
		fileName= new TextField();
		fileNameLabel.setLabelFor(fileName);
		
		Label descriptionLabel= new Label(lang.getString("Metadata.DESCRIPTION_LABEL"));
		description= new TextArea();
		descriptionLabel.setLabelFor(description);
		
		Label microcontrollerLabel= new Label(lang.getString("Metadata.MICROCONTROLLER_LABEL"));
		microcontroller= new ChoiceBox<>();
		microcontroller.getItems().addAll(ConfigPersister.getChipNames());
		microcontroller.getSelectionModel().selectFirst();
		microcontrollerLabel.setLabelFor(microcontroller);
		
		checkCode= new TextArea();
		checkCode.setEditable(false);
		
		metadata.getChildren().addAll(boardNameLabel, boardName, fileNameLabel, fileName, descriptionLabel, description, microcontrollerLabel, microcontroller, checkCode);
		setContent(metadata);
	}

	public TextField getBoardName() {
		return boardName;
	}

	public TextField getFileName() {
		return fileName;
	}

	public TextArea getDescription() {
		return description;
	}
	
	public TextArea getCheckCode() {
		return checkCode;
	}
	
	public ChoiceBox<String> getMicrocontroller() {
		return microcontroller;
	}

}
