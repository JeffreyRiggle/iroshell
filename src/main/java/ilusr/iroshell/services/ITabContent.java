package ilusr.iroshell.services;

import ilusr.core.interfaces.IDispose;
import ilusr.iroshell.dockarea.ICloseable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ITabContent extends IDispose{
	/**
	 * 
	 * @return The identifier associated with this tab.
	 */
	String getId();
	/**
	 * 
	 * @param id The identifier associated with this tab.
	 */
	void setId(String id);
	/**
	 * 
	 * @return The content to display in the tab.
	 */
	SimpleObjectProperty<Node> content();
	/**
	 * 
	 * @return If the tab can be closed by the user or not.
	 */
	SimpleBooleanProperty canClose();
	/**
	 * 
	 * @return The tooltip to show on hover of the tab header.
	 */
	SimpleStringProperty toolTip();
	/**
	 * 
	 * @return The @see MenuItems to show when a user right clicks the tab.
	 */
	ObservableList<MenuItem> contextMenuItems();
	/**
	 * 
	 * @param title A @see Node to display in the header area of a tab.
	 */
	void titleGraphic(Node title);
	/**
	 * 
	 * @param title A String to display in the header area of a tab.
	 */
	void titleGraphic(String title);
	/**
	 * 
	 * @return A @see Node to display in the header area of a tab.
	 */
	SimpleObjectProperty<Node> titleGraphic();
	/**
	 * Closes the tab.
	 */
	void close();
	/**
	 * Adds a close listener to this tab. This listener will be notified when the tab is closed.
	 * @param closeable A @see IClosable to add to this tab.
	 */
	void addCloseListener(ICloseable closeable);
	/**
	 * 
	 * @param closeable A @see IClosable to remove from this tab.
	 */
	void removeCloseListener(ICloseable closeable);
	/**
	 * 
	 * @return The custom data to associate with this tab.
	 */
	SimpleStringProperty customData();
}
