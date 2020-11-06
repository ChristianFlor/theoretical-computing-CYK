/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Icesi University (Cali - Colombia)
 * @author Daniel Alejandro Fernandez Robles <daniel.fernandez1@correo.icesi.edu.co>
 * @author Christian David Flor Astudillo <christian.flor1@correo.icesi.edu.co>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.ContextFreeGrammar;

/**
 * This class represents
 */
public class MainController {
	

	/**
	 * 
	 */
	@FXML
	private TextArea gramatic;

	/**
	 * 
	 */
	@FXML
	private ScrollPane scrollP1;
	
	/**
	 * 
	 */
	@FXML
	private TextField stirngw;


	/**
	 * 
	 */
	private ContextFreeGrammar program;

	/**
	 * The GridPane represents the view matrix for the algorithm
	 */
	private GridPane gridP1;

	/**
	 * 
	 */
	@FXML
	void aboutProgram(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText("Programa para poder pasar Informatica Teorica");
		alert.show();
	}

	/**
	 * 
	 */
	@FXML
	void exit(ActionEvent event) {
		((Stage) (((Button) event.getSource()).getScene().getWindow())).close();
	}

	/**
	 * 
	 */
	private void save() throws IOException {
		try {
			FileOutputStream fos = new FileOutputStream(ContextFreeGrammar.DATA_PATH);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(program);
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@FXML
	void save(ActionEvent event) {

		try {
			save();
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setContentText("La respuesta se ha guardado correctamente");
			alert.show();
		} catch (FileNotFoundException e) {

			Alert alert = new Alert(AlertType.ERROR);
			alert.setContentText("File not found");
			alert.show();
			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 *
	 */
	@FXML
	void clean(ActionEvent event) {

		gramatic.setEditable(true); gramatic.setText("");

		Alert a = new Alert(AlertType.INFORMATION);
		a.setContentText("Ingrese la gramatica");
		a.show();
	}

	/**
	 * 
	 */
    @FXML
    void registerGramatic(ActionEvent event) {
    	try {
    	if( !gramatic.getText().isEmpty() ){
    		program = new ContextFreeGrammar(gramatic.getText());
        	Alert a = new Alert(AlertType.INFORMATION);
    		a.setContentText("La gramatica se ha sido registrado correctamente");
    		a.show();
    		gramatic.setEditable(false);

   		}else {
   			Alert b = new Alert(AlertType.ERROR);
   	   		b.setContentText("Los gramatica no puede ser registrar, debe tener contenido");
   	   		b.show();
   		}
    	
		
    	}catch(Exception e) {
    		Alert a = new Alert(AlertType.ERROR);
    		a.setContentText("Digite los gramatica valida");
    		a.show();
    	}
    	
    }

	/**
	 *
	 */
	@FXML
	void algorithmCYK(ActionEvent event) {
		String w = stirngw.getText();
		gridP1 = new GridPane();
		gridP1.setHgap(3);
		gridP1.setVgap(3);
		scrollP1.setContent(gridP1);
		//traer matrix resultante
		//program.possibleW(w);


	}

	/**
	 * 
	 */
	public void initialize() throws FileNotFoundException, IOException {

		File file = new File(ContextFreeGrammar.DATA_PATH);

		if (file.exists()) {
			ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(file));
			try {
				
				program = (ContextFreeGrammar) entrada.readObject();
				entrada.close();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
