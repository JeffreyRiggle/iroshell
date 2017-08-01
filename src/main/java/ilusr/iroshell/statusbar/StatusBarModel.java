package ilusr.iroshell.statusbar;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class StatusBarModel {

	private final IStatusBarService service;
	
	/**
	 * 
	 * @param service A @see IStatusBarService.
	 */
	public StatusBarModel(IStatusBarService service) {
		this.service = service;
	}
	
	/**
	 * 
	 * @return A list of @see Node being displayed in the status bar area.
	 */
	public ObservableList<Node> statusBars() {
		return service.statusBars();
	}
	
	/**
	 * 
	 * @return The the status bar should be hidden when there are no items in it.
	 */
	public SimpleBooleanProperty hideWhenEmpty() {
		return service.hideWhenEmpty();
	}
}
