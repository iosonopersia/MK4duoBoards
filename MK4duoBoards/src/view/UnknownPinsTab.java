package view;

import i18n.i18n;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import model.DataManager;

public class UnknownPinsTab extends Tab {
	private TextArea unknownPinsArea;
	public UnknownPinsTab(DataManager model, i18n lang){
		this.setText(lang.getString("Tabs.UNKNOWN_PINS"));
		unknownPinsArea= new TextArea();
		unknownPinsArea.setEditable(true);
		Label unknownPinsLabel= new Label(lang.getString("Tabs.UNKNOWN_PINS_LABEL"));
		unknownPinsLabel.setLabelFor(unknownPinsArea);
		BorderPane content=new BorderPane();
		content.setPadding(new Insets(20, 20, 20, 20));
		content.setTop(unknownPinsLabel);
		content.setCenter(unknownPinsArea);
		setContent(content);
	}

	public TextArea getUnknownPinsArea() {
		return unknownPinsArea;
	}
	
}
