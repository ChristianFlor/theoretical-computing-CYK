/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Icesi University (Cali - Colombia)
 * @author Daniel Alejandro Fernandez Robles <daniel.fernandez1@correo.icesi.edu.co>
 * @author Christian David Flor Astudillo <christian.flor1@correo.icesi.edu.co>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package ui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * This class 
 */
public class Main extends Application{
	
	/**
	 * This class represents
	 */
	public static void main(String[] args) {
		launch();
	}
	
	/**
	 * This class represents
	 */
	@Override
	public void start(Stage stage) throws IOException {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("gui.fxml"));
		Parent root = loader.load();
		Scene scene = new Scene(root);
		stage.setTitle("Algortimo CYK");
		stage.setScene(scene);
		stage.show();
	}

}
