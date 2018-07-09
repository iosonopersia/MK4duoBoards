package view;

import i18n.i18n;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import model.DataManager;

public class MBSetupTab extends Tab {
	private TextArea MBSetupArea;
	
	public MBSetupTab(DataManager model, i18n lang){
		this.setText(lang.getString("Tabs.MB_SETUP"));
		MBSetupArea= new TextArea();
		MBSetupArea.setEditable(true);
		Label MBSetupLabel= new Label(lang.getString("Tabs.MB_SETUP_LABEL"));
		MBSetupLabel.setLabelFor(MBSetupArea);
		BorderPane content=new BorderPane();
		content.setPadding(new Insets(20, 20, 20, 20));
		content.setTop(MBSetupLabel);
		content.setCenter(MBSetupArea);
		setContent(content);
	}

	public TextArea getMBSetupArea() {
		return MBSetupArea;
	}
	
	
}
