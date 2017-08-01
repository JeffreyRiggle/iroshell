package ilusr.iroshell.features;

import javafx.event.EventHandler;
import javafx.stage.Stage;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IPreApplicationScreen {
	/**
	 * Sets the completion event for the view to trigger when it is done.
	 * @param handler A handler to fire when the view is done.
	 */
	void setOnCompleted(EventHandler<PreApplicationCompletionResult> handler);
	/**
	 * Starts the pre application view and provides a stage to display on. 
	 * This stage will be shown and closed by the application shell.
	 * 
	 * @param stage The @see Stage to show the pre application view in.
	 */
	void run(Stage stage);
}
