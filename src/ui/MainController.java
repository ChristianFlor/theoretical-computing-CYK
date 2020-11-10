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
import model.ContextFreeGrammarOperations;

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
	private TextArea gramaticFNC;
	/**
	 *
	 */
	@FXML
	private ScrollPane scrollP2;
	
	/**
	 * 
	 */
	@FXML
	private TextField stringw;

	/**
	 * 
	 */
	private ContextFreeGrammarOperations program;

	/**
	 * The GridPane represents the view matrix for the algorithm
	 */
	private GridPane gridP2;

	private ContextFreeGrammar gfr;

	/**
	 * 
	 */
	@FXML
	void aboutProgram(ActionEvent event) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setContentText("Program that from a context-independent grammar G and a string w establishes whether the grammar generates said string, is say w ∈ L (G). \n\nCreated by: \n -Daniel Fernandez \n -Christian Flor ");
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

		gramatic.setEditable(true);
		gramatic.setText("");
		gramaticFNC.setText("");
		stringw.setText("");
		scrollP2.setContent(null);

		Alert a = new Alert(AlertType.INFORMATION);
		a.setContentText("Ingrese la gramatica");
		a.show();
	}

	/**
	 * 
	 */
	@FXML
	void convert(ActionEvent event) {
		try {
			if( !gramatic.getText().isEmpty() ){
				program = new ContextFreeGrammarOperations();
				gfr= program.read(gramatic.getText()); //FIXME the method is in ContextFreeGrammarOperations.java
				Alert a = new Alert(AlertType.INFORMATION);
				a.setContentText("La gramatica se ha sido registrado correctamente");
				a.show();
				gramatic.setEditable(false);
				gfr=program.convertToCNF(gfr);
				gramaticFNC.setText(program.convertToCNF(gfr)+"");
				gramaticFNC.setEditable(false);
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
	void algorithmCYK(ActionEvent event) throws Exception {
		String w = stringw.getText();
		gridP2 = new GridPane();
		gridP2.setHgap(3);
		gridP2.setVgap(3);
		scrollP2.setContent(gridP2);
		//traer matrix resultante
		if(program.CYK(gfr, w)){
			Alert a = new Alert(AlertType.INFORMATION);
			a.setContentText("La gramatica SI genera la cadena "+w);
			a.show();
			for (int i = 0; i < program.getCykMatrix().length; i++) {
				for (int j = 0; j < program.getCykMatrix()[i].length; j++) {
					TextField ta = new TextField(w.charAt(i)+"");
					ta.setEditable(false);
					ta.setPrefWidth(30);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(125, 156, 205, 0.84)");
					gridP2.add(ta, 0, (i+1));

					ta = new TextField("j="+(i+1));
					ta.setEditable(false);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(205, 125, 125, 0.84)");
					ta.setPrefWidth(30);
					gridP2.add(ta, (i+1), 0);

					ta = new TextField("" + program.getCykMatrix()[i][j]);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color:  rgba(70, 214, 70, 0.75)");
					ta.setEditable(false);
					ta.setPrefWidth(80);
					if(program.getCykMatrix()[i][j]!=null){
						gridP2.add(ta, j + 1, i+1);
					}

				}
			}
		}else{
			Alert a = new Alert(AlertType.ERROR);
			a.setContentText("La gramatica NO genera la cadena "+w);
			a.show();
		}

	}

	/**
	 * 
	 */
	public void initialize() throws FileNotFoundException, IOException {

		File file = new File(ContextFreeGrammar.DATA_PATH);

		if (file.exists()) {
			ObjectInputStream entrada = new ObjectInputStream(new FileInputStream(file));
			try {
				
				program = (ContextFreeGrammarOperations) entrada.readObject();
				entrada.close();

			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}
}
