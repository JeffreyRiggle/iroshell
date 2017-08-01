package ilusr.iroshell.documentinterfaces.mdi;

import ilusr.iroshell.dockarea.DockArea;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class MultipleDocumentInterface extends AnchorPane {
	
	@FXML
	private DockArea dock;
	
	/**
	 * Base Ctor.
	 */
	public MultipleDocumentInterface() {
		FXMLLoader loader = new FXMLLoader(getClass().getResource("MultipleDocumentInterface.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (Exception exception) {
			//TODO
			exception.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return The @see DockArea associated with this document interface.
	 */
	public DockArea getDock() {
		return dock;
	}
}