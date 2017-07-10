package view;

import java.io.IOException;
import java.util.Locale;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

import i18n.i18n;
import model.Const;
import model.DataManager;


public class MK4duoBoardsApplication extends Application {
	
	@Override
	public void init(){
		// do nothing
	}

	@Override
	public void start(Stage primaryStage) throws IOException {
		DataManager dataManager= new DataManager();
		i18n lang= new i18n(dataManager.getLocale());
		Locale.setDefault(dataManager.getLocale());
		
		//Dimensions
		Rectangle2D screen= Screen.getPrimary().getVisualBounds();
		primaryStage.setHeight(screen.getHeight() / 1.1);
		primaryStage.setWidth(screen.getWidth() / 2);
		primaryStage.setResizable(true);
		
		//Position
		primaryStage.setX((screen.getWidth() - primaryStage.getWidth()) / 2);
		primaryStage.setY((screen.getHeight() - primaryStage.getHeight()) / 2);

		//Setting the scene
		PrimaryPane primaryPane= new PrimaryPane(dataManager, lang, primaryStage);
		Scene scene = new Scene(primaryPane);
		primaryStage.setScene(scene);
		primaryStage.setTitle(lang.getString("Application.TITLE.WELCOME")+Const.SPACE+Const.APP_NAME+"!");
		primaryStage.getIcons().addAll(new Image("MK4duoIcon_64x64.png"), new Image("MK4duoIcon_48x48.png"), new Image("MK4duoIcon_32x32.png"));
		primaryStage.show();
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
