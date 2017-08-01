package ilusr.iroshell.statusbar;

import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.core.LocationType;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;

public interface IStatusBarService {
	/**
	 * Adds a status bar item to the status bar area.
	 * 
	 * @param statusBar A @see Node to add to the status bar area.
	 * @param location The @see LocationParameters that specify where the status bar item is going to be added
	 * @throws IllegalArgumentException If LocationParameters use @see {@link LocationType#AfterName} or @see {@link LocationType#BeforeName}
	 */
	void addStatusBar(Node statusBar, LocationParameters location) throws IllegalArgumentException;
	/**
	 * Removes a status bar item from the status bar area.
	 * 
	 * @param statusBar The @see Node to remove from the status bar area.
	 */
	void removeStatusBar(Node statusBar);
	/**
	 * Gets a list of current status bar items.
	 * 
	 * @return A @see ObservableList that represents the added status bar items.
	 */
	ObservableList<Node> statusBars();
	/**
	 * When true this option will hide the status bar when no items are in it.
	 * 
	 * @return A @see SimpleBooleanProperty that represnets if the status bar should be hidden when empty.
	 */
	SimpleBooleanProperty hideWhenEmpty();
}
