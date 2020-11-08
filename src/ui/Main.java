/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Icesi University (Cali - Colombia)
 * @author Daniel Alejandro Fernandez Robles <daniel.fernandez3@correo.icesi.edu.co>
 * @author Christian David Flor Astudillo <christian.flor1@correo.icesi.edu.co>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package ui;

import java.io.IOException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.ContextFreeGrammar;
import model.ContextFreeGrammarOperations;

/**
 * This class 
 */
public class Main extends Application{
	
	/**
	 * This class represents
	 */
	public static void main(String[] args) {
		/*Scanner s = new Scanner(System.in);
		ContextFreeGrammarOperations cfgo = new ContextFreeGrammarOperations();
		System.out.println("How many variables?");
		int vars = Integer.parseInt(s.nextLine());
		System.out.println("Write the productions in this format: V : AB | XY | a | b | ... | ");
		String input = "";
		while(vars > 0) {
			input += s.nextLine() + "\n";
			vars--;
		}

		try {
			ContextFreeGrammar cfg = cfgo.read(input);

			System.out.println("****************");
			cfg.getProductionRules().forEach((head, bodies) -> {
				String line = head + " --> ";
				for(String body : bodies) {
					line += body + " ";
				}
				System.out.println(line);
			});
			System.out.println("****************");

			System.out.println("What string will you validate?");
			System.out.println("RESULT: " + cfgo.CYK(cfg, s.nextLine()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		s.close();
		*/

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
