package view;

import i18n.i18n;
import javafx.scene.control.TabPane;
import model.DataManager;

public class EditingTabPane extends TabPane {

	private MetadataTab metadataTab;
	private MainPinsTab mainPinsTab;
	private ExtrudersPinsTab extrudersPinsTab;
	private UnknownPinsTab unknownPinsTab;
	private IfBlocksTab ifBlocksTab;

	public EditingTabPane(DataManager model, i18n lang){
		setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
		
		metadataTab= new MetadataTab(model, lang);
		mainPinsTab= new MainPinsTab(model, lang);
		extrudersPinsTab= new ExtrudersPinsTab(model, lang);
		unknownPinsTab= new UnknownPinsTab(model, lang);
		ifBlocksTab= new IfBlocksTab(model, lang);
		
		getTabs().addAll(metadataTab, mainPinsTab, extrudersPinsTab, unknownPinsTab, ifBlocksTab);
	}
	
	public final MetadataTab getMetadataTab() {
		return metadataTab;
	}

	public final MainPinsTab getMainPinsTab() {
		return mainPinsTab;
	}
	
	public final ExtrudersPinsTab getExtrudersPinsTab() {
		return extrudersPinsTab;
	}
	
	public final UnknownPinsTab getUnknownPinsTab(){
		return this.unknownPinsTab;
	}
	
	public final IfBlocksTab getIfBlocksTab() {
		return ifBlocksTab;
	}
}
