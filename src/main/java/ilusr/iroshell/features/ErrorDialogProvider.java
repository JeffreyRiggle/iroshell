package ilusr.iroshell.features;

import javafx.stage.Stage;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ErrorDialogProvider {
	/**
	 * 
	 * @param e The exception to use when creating the error dialog.
	 * @return A @see Stage to show for the error dialog.
	 */
	Stage create(Throwable e);
}
