package view;

import i18n.i18n;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import model.DataManager;

public class IfBlocksTab extends Tab {
	private TextArea ifBlocksArea;
	
	public IfBlocksTab(DataManager model, i18n lang){
		this.setText(lang.getString("Tabs.IF_BLOCKS"));
		ifBlocksArea= new TextArea();
		ifBlocksArea.setEditable(true);
		Label ifBlocksLabel= new Label(lang.getString("Tabs.IF_BLOCKS_LABEL"));
		ifBlocksLabel.setLabelFor(ifBlocksArea);
		BorderPane content=new BorderPane();
		content.setPadding(new Insets(20, 20, 20, 20));
		content.setTop(ifBlocksLabel);
		content.setCenter(ifBlocksArea);
		setContent(content);
	}

	public TextArea getIfBlocksArea() {
		return ifBlocksArea;
	}
	
	
}
