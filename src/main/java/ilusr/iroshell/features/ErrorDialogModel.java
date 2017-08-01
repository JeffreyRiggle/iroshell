package ilusr.iroshell.features;

import javafx.beans.property.SimpleStringProperty;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ErrorDialogModel {

	private SimpleStringProperty exceptionString;
	
	/**
	 * Base ctor.
	 */
	public ErrorDialogModel() {
		exceptionString = new SimpleStringProperty();
	}
	
	/**
	 * 
	 * @param exception The exception to be shown.
	 */
	public ErrorDialogModel(String exception) {
		exceptionString = new SimpleStringProperty(exception);
	}
	
	/**
	 * 
	 * @return The exception to be shown.
	 */
	public SimpleStringProperty exceptionProperty() {
		return exceptionString;
	}
}
