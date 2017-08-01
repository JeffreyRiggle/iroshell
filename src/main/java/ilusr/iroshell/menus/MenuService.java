package ilusr.iroshell.menus;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ilusr.iroshell.core.LocationParameters;
import ilusr.logrunner.LogRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

/**
 * 
 * @author Jeffrey Riggle
 *
 */
public class MenuService implements IMenuService{

	private final Object menuLock = new Object();
	private ObservableList<Menu> menus;
	
	/**
	 * Basic Constructor for the MenuService.
	 */
	public MenuService() {
		menus = FXCollections.observableArrayList();
	}
	
	@Override
	public void addMenu(Menu menu, LocationParameters params) {
		synchronized(menuLock) {
			LogRunner.logger().log(Level.INFO, String.format("Adding menu: %s, with parameter type: %s\n", menu.getText(), params));
			Menu foundMenu = null;
			
			for (Menu m : menus) {
				if (m.getText().equals(menu.getText())) {
					foundMenu = m;
					break;
				}
			}
			
			if (foundMenu == null) {
				LogRunner.logger().log(Level.INFO, String.format("Unable to find menu for %s\n", menu.getText()));
				addMenuToLocation(menu, params);
				return;
			}

			LogRunner.logger().log(Level.INFO, String.format("Found menu for %s\n", foundMenu.getText()));
			mergeMenuItems(foundMenu, menu);
		}
	}
	
	private void addMenuToLocation(Menu menu, LocationParameters params) {
		switch (params.type()) {
			case First:
				menus.add(0, menu);
				break;
			case AfterName:
				int aIndex = findIndex(params.locationName()) + 1;
				menus.add(aIndex, menu);
				break;
			case BeforeName:
				int bIndex = findIndex(params.locationName());
				menus.add(bIndex, menu);
				break;
			case Index:
				menus.add(params.index(), menu);
				break;
			case Last:
				menus.add(menu);
				break;
			default:
				//TODO: Error trace
				break;
		}
	}
	
	private int findIndex(String name) {
		int index = -1;
		
		for (Menu m : menus) {
			index++;
			if (!m.getText().equals(name)) continue;
			break;
		}
		
		return index;
	}
	
	private void mergeMenuItems(Menu source, Menu target) {
		List<MenuItem> itemsToAdd = new ArrayList<MenuItem>();
		
		for (MenuItem mi : target.getItems()) {
			if (hasMenuItem(source.getItems(), mi.getText())) continue;
			
			LogRunner.logger().log(Level.INFO, String.format("Found sub item to add %s\n", mi.getText()));
			itemsToAdd.add(mi);
		}
		
		if (itemsToAdd.size() == 0) return;
		
		source.getItems().addAll(itemsToAdd);
	}
	
	private boolean hasMenuItem(List<MenuItem> source, String name) {
		boolean found = false;
		for (MenuItem mi : source) {
			if (!mi.getText().equals(name)) continue;
			
			found = true;
			break;
		}
		
		return found;
	}
	
	@Override
	public void removeMenu(Menu menu) {
		synchronized(menuLock) {
			LogRunner.logger().log(Level.INFO, String.format("Removing menu: %s\n", menu.getText()));
			menus.remove(menu);
		}
	}
	
	@Override
	public ObservableList<Menu> menus() {
		synchronized(menuLock) {
			return menus;
		}
	}
	
	@Override
	public void addMenuItem(MenuItem item, String parent, LocationParameters location) {
		synchronized(menuLock) {
			LogRunner.logger().log(Level.INFO, String.format("Adding menu item: %s, to parent: %s, with parameter type: %s\n", item.getText(), parent, location));
			Menu parentMenu = null;
			
			for (Menu m : menus) {
				if (!m.getText().equals(parent)) continue;
			
				parentMenu = m;
				break;
			}
			
			if (parentMenu == null) {
				parentMenu = new Menu(parent);
				menus.add(parentMenu);
			}
			
			addMenuItemToLocation(parentMenu, item, location);
		}
	}
	
	private void addMenuItemToLocation(Menu menu, MenuItem item, LocationParameters params) {
		switch (params.type()) {
			case First:
				menu.getItems().add(0, item);
				break;
			case AfterName:
				int aIndex = findChildIndex(menu, params.locationName()) + 1;
				menu.getItems().add(aIndex, item);
				break;
			case BeforeName:
				int bIndex = findChildIndex(menu, params.locationName());
				menu.getItems().add(bIndex, item);
				break;
			case Index:
				menu.getItems().add(params.index(), item);
				break;
			case Last:
				menu.getItems().add(item);
				break;
			default:
				//TODO: Error trace
				break;
		}
	}
	
	private int findChildIndex(Menu menu, String name) {
		int index = -1;
		
		for (MenuItem mi : menu.getItems()) {
			index++;
			if (!mi.getText().equals(name)) continue;
			break;
		}
		
		return index;
	}
	
	@Override
	public void removeMenuItem(MenuItem item, String parent) {
		synchronized(menuLock) {
			LogRunner.logger().log(Level.INFO, String.format("Removing menu item: %s from parent %s\n", item.getText(), parent));
			for (Menu m : menus) {
				if (!m.getText().equals(parent)) continue;
			
				m.getItems().remove(item);
				break;
			}
		}
	}
}
