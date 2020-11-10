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
	 * 
	 */
	@FXML
	private TextArea cfgTextArea;

	/**
	 * 
	 */
	@FXML
	private TextArea cfgCNFTextArea;
	/**
	 *
	 */
	@FXML
	private ScrollPane scrollPane;
	
	/**
	 * 
	 */
	@FXML
	private TextField stringW;

	/**
	 * 
	 */
	private ContextFreeGrammarOperations operator;

	/**
	 * The GridPane represents the view matrix for the algorithm
	 */
	private GridPane matrix;

	private ContextFreeGrammar cfg;

	/**
	 *
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
	 * 
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
	 *
	 */
	@FXML
	public void algorithmCYK(ActionEvent event) {
		cfg = operator.read(cfgCNFTextArea.getText());
		String w = stringW.getText();
		matrix = new GridPane();
		matrix.setHgap(3);
		matrix.setVgap(3);
		scrollPane.setContent(matrix);
		if(operator.CYK(cfg, w)){
			Alert a = new Alert(AlertType.INFORMATION);
			a.setHeaderText("The grammar does produce the string");
			a.setTitle("YES");
			a.setContentText(w);
			a.show();
			for (int i = 0; i < operator.getCykMatrix().length; i++) {
				for (int j = 0; j < operator.getCykMatrix()[i].length; j++) {
					TextField ta = new TextField(w.charAt(i)+"");
					ta.setEditable(false);
					ta.setPrefWidth(30);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(125, 156, 205, 0.84)");
					matrix.add(ta, 0, (i+1));

					ta = new TextField("j="+(i+1));
					ta.setEditable(false);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color: rgba(205, 125, 125, 0.84)");
					ta.setPrefWidth(30);
					matrix.add(ta, (i+1), 0);

					ta = new TextField("" + operator.getCykMatrix()[i][j]);
					ta.setStyle(ta.getStyle() + "\n-fx-background-color:  rgba(70, 214, 70, 0.75)");
					ta.setEditable(false);
					ta.setPrefWidth(80);
					if(operator.getCykMatrix()[i][j]!=null){
						matrix.add(ta, j + 1, i+1);
					}

				}
			}
		}else{
			Alert a = new Alert(AlertType.ERROR);
			a.setHeaderText("The grammar does not produce the string");
			a.setTitle("NO");
			a.setContentText(w);
			a.show();
		}

	}

	/**
	 * 
	 */
	public void initialize() {
		operator = new ContextFreeGrammarOperations();
	}
}
