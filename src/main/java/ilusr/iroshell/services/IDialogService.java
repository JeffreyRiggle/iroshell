package ilusr.iroshell.services;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Jeffrey Riggle
 *
 */
public interface IDialogService {
	/**
	 * Displays a @see Stage as a modal view.
	 * @param stage The @see Stage to display.
	 */
	void displayModal(Stage stage);
	/**
	 * Displays a @see Scene as a modal view.
	 * @param scene The @see Scene to display.
	 */
	void displayModal(Scene scene);
	/**
	 * Displays a @see Scene as a modal view.
	 * @param scene The @see Scene to display.
	 * @param title A title to display with the scene.
	 */
	void displayModal(Scene scene, String title);
	/**
	 * Displays a @see Parent as a modal view.
	 * @param root The @see Parent to display.
	 */
	void displayModal(Parent root);
	/**
	 * Displays a @see Parent as a modal view.
	 * @param root The @see Parent to display.
	 * @param title the title to display.
	 */
	void displayModal(Parent root, String title);
	/**
	 * Displays a @see Stage as a modeless view.
	 * @param stage The @see Stage to display.
	 */
	void displayModeless(Stage stage);
	/**
	 * Displays a @see Scene as a modeless view.
	 * @param scene The @see Scene to display.
	 */
	void displayModeless(Scene scene);
	/**
	 * Displays a @see Scene as a modeless view.
	 * @param scene The @see Scene to display.
	 * @param title the title to display.
	 */
	void displayModeless(Scene scene, String title);
	/**
	 * Displays a @see Parent as a modeless view.
	 * @param stage The @see Parent to display.
	 */
	void displayModeless(Parent root);
	/**
	 * Displays a @see Parent as a modeless view.
	 * @param stage The @see Parent to display.
	 * @param title the title to display.
	 */
	void displayModeless(Parent root, String title);
	/**
	 * Displays a @see Node as an embedded overlay in the main application.
	 * @param node The @see Node to display.
	 */
	void displayEmbedded(Node node);
	/**
	 * Removes a @see Node as an embedded overlay in the main application.
	 * @param node The @see Node to clear from the overlay.
	 */
	void clearEmbedded(Node node);
}
