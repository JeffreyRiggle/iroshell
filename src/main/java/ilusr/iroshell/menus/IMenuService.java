package ilusr.iroshell.menus;

import ilusr.iroshell.core.LocationParameters;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public interface IMenuService {
	/**
	 * Adds a menu to the menu bar.
	 * 
	 * @param menu The @see Menu to add to the menu toolbar.
	 * @param params The @see LocationParameters to use to determine where to add the menu.
	 */
	void addMenu(Menu menu, LocationParameters params);
	/**
	 * Removes a menu from the menu toolbar.
	 * 
	 * @param menu The @see Menu to remove from the menu toolbar area.
	 */
	void removeMenu(Menu menu);
	/**
	 * Gets the current menu toolbar menus.
	 * 
	 * @return A @see ObservableList of @see Menu that represents the menus in the toolbar area.
	 */
	ObservableList<Menu> menus();
	/**
	 * Adds a MenuItem to a menu in the menu toolbar area.
	 * 
	 * @param item The @see MenuItem to add to the menu.
	 * @param parent A @see String representing the parent menu to add this item to.
	 * @param location A @see LocationParameters to define where the item item should be added.
	 */
	void addMenuItem(MenuItem item, String parent, LocationParameters location);
	/**
	 * Removes a menu item from a menu.
	 * 
	 * @param item The @see MenuItem to remove from the menu.
	 * @param parent A @see String representing the menu.
	 */
	void removeMenuItem(MenuItem item, String parent);
}
