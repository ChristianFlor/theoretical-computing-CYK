/**~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 * Icesi University (Cali - Colombia)
 * @author Daniel Alejandro Fernandez Robles <daniel.fernandez3@correo.icesi.edu.co>
 * @author Christian David Flor Astudillo <christian.flor1@correo.icesi.edu.co>
 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 */
package ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import model.ContextFreeGrammar;
import model.ContextFreeGrammarOperations;

/**
 *
 */
public class MainController {

	/**
	 * The cfgTextArea represents the grammar entered by the user
	 */
	@FXML
	private TextArea cfgTextArea;

	public final static int CHAR_LENGTH = 20;

	/**
	 * The cfgCNFTextArea represents grammar in chomsky normal form
	 */
	@FXML
	private TextArea cfgCNFTextArea;
	/**
	 * The ScrollPane is the area where the result of matrix ij is created
	 */
	@FXML
	private ScrollPane scrollPane;
	
	/**
	 * represents the attribute that allows us to obtain w for the algorithm cyk
	 */
	@FXML
	private TextField stringW;

	/**
	 * Represents the relationship with the operations of context-free grammar
	 */
	private ContextFreeGrammarOperations operator;

	/**
	 * The GridPane represents the view matrix for the algorithm
	 */
	private GridPane matrix;
	/**
	 * This cfg represents the relationship with context-free grammar
	 */
	private ContextFreeGrammar cfg;

	/**
	 * This method clears the areas where grammar is displayed or written
	 */
	@FXML
	public void clearAll(ActionEvent event) {
		cfgTextArea.setEditable(true);
		cfgTextArea.setText("");
		cfgCNFTextArea.setText("");
		stringW.setText("");
		scrollPane.setContent(null);
	}

	/**
	 * This method reads the grammar registered by the user and converts it to chomsky normal form and displays it
	 */
	@FXML
	public void convert(ActionEvent event) {
		try {
			if( !cfgTextArea.getText().isEmpty() ){
				operator = new ContextFreeGrammarOperations();
				cfg = operator.read(cfgTextArea.getText());
				Alert a = new Alert(AlertType.INFORMATION);
				a.setContentText("Grammar was converted");
				a.show();
				cfg = operator.convertToCNF(cfg);
				cfgCNFTextArea.setText(operator.convertToCNF(cfg)+"");
			}else {
				Alert b = new Alert(AlertType.ERROR);
				b.setContentText("Provide a grammar");
				b.show();
			}
		} catch(Exception e) {
			Alert a = new Alert(AlertType.ERROR);
			a.setContentText("Malformed format");
			a.show();
		}
	}

	/**
	 * this method reads the grammar registered by the user and applies the algorithm cyk
	 */
	@FXML
	public void algorithmCYK(ActionEvent event) {
		cfg = operator.read(cfgCNFTextArea.getText());
		String w = stringW.getText();
		if(operator.CYK(cfg, w)){
			Alert a = new Alert(AlertType.INFORMATION);
			a.setHeaderText("The grammar does produce the string");
			a.setTitle("YES");
			a.setContentText(w);
			a.show();
		}else{
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("The grammar does not produce the string");
			a.setTitle("NO");
			a.setContentText(w);
			a.show();
		}
		fillCykMatrix(w);
	}

	/**
	 * This method fills the matrix cyk obtained in the model to show it to the user in an intuitive way
	 * @param w The String that wants to be validated. It is assumed that the String contains only terminals or is lambda
	 */
	private void fillCykMatrix(String w) {
		matrix = new GridPane();
		matrix.setHgap(3);
		matrix.setVgap(3);
		scrollPane.setContent(matrix);
		for (int i = 0; i < operator.getCykMatrix().length; i++) {
			for (int j = 0; j < operator.getCykMatrix()[i].length; j++) {
				TextField ta = new TextField(w.charAt(i)+"");
				ta.setEditable(false);
				ta.setPrefWidth(30);
				ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(125, 156, 205, 0.84)");
				matrix.add(ta, 0, (i+1));

				String content = "";

				if(operator.getCykMatrix()[i][j] != null){
					content = operator.getCykMatrix()[i][j].toString().replace("[", "{").replace("]", "}");
					ta = new TextField("" + content);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color:  rgba(70, 214, 70, 0.75)");
					ta.setEditable(false);
					ta.setPrefWidth(content.replace(", ", "").length() * CHAR_LENGTH);
					matrix.add(ta, j + 1, i+1);

					ta = new TextField("j="+(i+1));
					ta.setEditable(false);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(205, 125, 125, 0.84)");
					ta.setPrefWidth(3 * CHAR_LENGTH);
					matrix.add(ta, (i+1), 0);
				}
			}
		}
	}

	/**
	 * <b>Description:</b>
	 * The initialize function of the fxml file, called as soon as the graphical interface is loaded.
	 */
	public void initialize() {
		operator = new ContextFreeGrammarOperations();
	}
}
