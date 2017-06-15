package view;

import i18n.i18n;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SeparatorMenuItem;
import model.Board;
import model.DataManager;

public class BoardListView extends ListView<Board> {
	private MenuItem newBoard,
					 sortBoards,
					 pinCheck,
					 exportBoard,
					 removeBoard,
					 disableSelected,
					 emptyList;
	
	public BoardListView(DataManager model, i18n lang){
		this.setMinWidth(200);
		this.setMaxWidth(300);
		this.setPlaceholder(new Label(lang.getString("BoardList.PLACEHOLDER")));
		this.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		
		ContextMenu contextMenu= new ContextMenu();
		newBoard= new MenuItem(lang.getString("ContextMenu.NEW_BOARD"));
		sortBoards= new MenuItem(lang.getString("ContextMenu.SORT_BOARDS"));
		pinCheck= new MenuItem(lang.getString("Menu.TOOLS.PIN_CHECK"));
		exportBoard= new MenuItem(lang.getString("ContextMenu.EXPORT_THIS_BOARD"));
		SeparatorMenuItem separatorMenuItem = new SeparatorMenuItem();
		removeBoard= new MenuItem(lang.getString("ContextMenu.REMOVE_BOARD"));
		disableSelected= new MenuItem(lang.getString("ContextMenu.DISABLE_SELECTED"));
		emptyList= new MenuItem(lang.getString("ContextMenu.EMPTY_LIST"));

		contextMenu.getItems().addAll(newBoard, sortBoards, pinCheck, exportBoard, separatorMenuItem, disableSelected, removeBoard, emptyList);
		
		this.setContextMenu(contextMenu);
	}
	
	public final MenuItem getContextMenuNewBoard(){
		return this.newBoard;
	}
	
	public final MenuItem getContextMenuSortBoards(){
		return this.sortBoards;
	}
	
	public final MenuItem getContextMenuRemoveBoard(){
		return this.removeBoard;
	}
	
	public final MenuItem getContextMenuEmptyList(){
		return this.emptyList;
	}

	public final MenuItem getContextMenuDisableSelected() {
		return this.disableSelected;
	}

	public final MenuItem getContextMenuPinCheck() {
		return this.pinCheck;
	}

	public final MenuItem getContextMenuExportBoard() {
		return this.exportBoard;
	}
}
