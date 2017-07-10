package view;

import java.util.HashMap;
import java.util.Map;

import i18n.i18n;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Const;
import model.DataManager;
import model.Pin;
import model.Section;
import persistence.ConfigPersister;

public class MainPinsTab extends Tab {
	private VBox tablesLayout;
	private Map<String, TableView<Pin>> everyTable;

	
	public MainPinsTab(DataManager model, i18n lang){
		this.setText(lang.getString("Tabs.PIN_TABLES_MAIN"));
		tablesLayout= new VBox();
		tablesLayout.setAlignment(Pos.BOTTOM_CENTER);
		tablesLayout.setSpacing(10);
		tablesLayout.setPadding(new Insets(20, 20, 50, 20));
		Text hotTip= new Text(lang.getString("PinTable.HOT_TIP"));
		hotTip.setFont(Font.font("Arial", FontWeight.BOLD, 14));
		tablesLayout.getChildren().add(hotTip);
		everyTable= new HashMap<>();
		
		for(Section section: ConfigPersister.getKnownPins()){
			if(section.getName().startsWith(Const.EXTRUDERS_SECTION_START)==false &&
					section.getName().startsWith(Const.SERVOS_SECTION_START)==false){
				Label sectionTitle= new Label(section.getName());
				sectionTitle.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 22));
				tablesLayout.getChildren().add(sectionTitle);
				
				int numOfElements= section.getPins().size();
						
				TableView<Pin> table= newPinTable(lang, numOfElements);
				tablesLayout.getChildren().add(table);
				everyTable.put(section.getName(), table);
			}
		}
	
		ScrollPane sp= new ScrollPane();
		sp.setFitToWidth(true);
		sp.setContent(tablesLayout);
		setContent(sp);
		
	
	}
	
	private TableView<Pin> newPinTable(i18n lang, int numOfElements) {
		TableView<Pin> pinTable= new TableView<>();
		
		pinTable.setPlaceholder(new Label(lang.getString("PinTable.PLACEHOLDER")));
		pinTable.setEditable(true);
		pinTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
		
		TableColumn<Pin, String> nameColumn= new TableColumn<>(lang.getString("PinTable.Column.PIN_NAME"));
		nameColumn.setCellValueFactory((new PropertyValueFactory<>("name")));
		
		TableColumn<Pin, Integer> valueColumn= new TableColumn<>(lang.getString("PinTable.Column.PIN_VALUE"));
		valueColumn.setCellValueFactory((new PropertyValueFactory<>("value")));
		valueColumn.setCellFactory(col -> new IntegerEditingCell());
	
		TableColumn<Pin, String> commentColumn= new TableColumn<>(lang.getString("PinTable.Column.PIN_COMMENT"));
		commentColumn.setCellValueFactory((new PropertyValueFactory<>("comment")));
		commentColumn.setCellFactory(TextFieldTableCell.<Pin>forTableColumn());
		
		nameColumn.setSortable(false);
		valueColumn.setSortable(false);
		commentColumn.setSortable(false);
		
		pinTable.getColumns().add(nameColumn);
		pinTable.getColumns().add(valueColumn);
		pinTable.getColumns().add(commentColumn);
		
		pinTable.setFixedCellSize(25);
		pinTable.prefHeightProperty().bind(pinTable.fixedCellSizeProperty().multiply(numOfElements+1.1));
		pinTable.minHeightProperty().bind(pinTable.prefHeightProperty());
		pinTable.maxHeightProperty().bind(pinTable.prefHeightProperty());
		
		return pinTable;
	}

	public Map<String, TableView<Pin>> getTables(){
		return everyTable;
	}

	public void setItemsForEveryTable(ObservableList<Pin> pins) {
		for(Map.Entry<String, TableView<Pin>> t: getTables().entrySet()){
			
			FilteredList<Pin> pinsFiltered= new FilteredList<>(pins, pin-> pin.getSection().equals(t.getKey()) &&
																		   pin.getSection().startsWith(Const.EXTRUDERS_SECTION_START)==false &&
																		   pin.getSection().startsWith(Const.SERVOS_SECTION_START)==false);
			t.getValue().setItems(pinsFiltered);
			
		}
	}
}
