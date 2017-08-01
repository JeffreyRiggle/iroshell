package ilusr.iroshell.services;

import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.persistence.ToolBarAreaPersistence;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IPersistableToolBarService {
	/**
	 * 
	 * @param bluePrintName The name of the toolbar blue print.
	 * @param bluePrint The blue print. This is responsible for creating toolbars.
	 */
	void registerBluePrint(String bluePrintName, IToolBarBluePrint bluePrint);
	/**
	 * 
	 * @param bluePrintName The name of the toolbar blue print to remove.
	 */
	void unregisterBluePrint(String bluePrintName);
	/**
	 * 
	 * @param bluePrintName The name of the blue print to use to create the toolbar.
	 * @param parameters The @see LocationParameters to use when adding this toolbar.
	 * @param position The @see DockPosition to put the toolbar in.
	 * @return A String representing the unique identifier for the created toolbar.
	 */
	String addToolBar(String bluePrintName, LocationParameters parameters, DockPosition position);
	/**
	 * 
	 * @param bluePrintName The name of the blue print to use to create the toolbar.
	 * @param parameters The @see LocationParameters to use when adding this toolbar.
	 * @param position The @see DockPosition to put the toolbar in.
	 * @param canDrag If the toolbar can be dragged or not.
	 * @return A String representing the unique identifier for the created toolbar.
	 */
	String addToolBar(String bluePrintName, LocationParameters parameters, DockPosition position, boolean canDrag);
	/**
	 * 
	 * @return A @see ToolBarAreaPersistence object representing the current layout.
	 */
	ToolBarAreaPersistence getPersistence();
	/**
	 * 
	 * @return A String representing the current layout.
	 */
	String getLayout();
	/**
	 * Updates see {@link #getPersistence()} and {@link #getLayout()}
	 */
	void savePersistence();
	/**
	 * 
	 * @param persistence A @see ToolBarAreaPersistence object to create a layout from.
	 */
	void loadLayout(ToolBarAreaPersistence persistence);
	/**
	 * 
	 * @param layout A String to create a layout from.
	 */
	void loadLayout(String layout);
	/**
	 * 
	 * @param id The unqiue identifier for the toolbar to be removed.
	 */
	void removeToolBar(String id);
	/**
	 * Removes all toolbars that are managed by the PersistableToolBarService.
	 */
	void clearToolBars();
}
