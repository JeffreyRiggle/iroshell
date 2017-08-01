package ilusr.iroshell.services;

import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.toolbar.DraggableToolBar;
import javafx.collections.ObservableList;
import javafx.scene.control.ToolBar;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IToolBarService {
	/**
	 * 
	 * @param toolBar A @see ToolBar to add to the application.
	 * @param parameters The @see LocationParameters to determine where the @see ToolBar will get added.
	 * @param position The @see DockPosition to determine what dock the @see ToolBar will be added to.
	 */
	void addToolBar(ToolBar toolBar, LocationParameters parameters, DockPosition position);
	/**
	 * 
	 * @param toolBar A @see ToolBar to add to the application.
	 * @param parameters The @see LocationParameters to determine where the @see ToolBar will get added.
	 * @param position The @see DockPosition to determine what dock the @see ToolBar will be added to.
	 * @param canDrag Determines if the @see ToolBar should be draggable.
	 */
	void addToolBar(ToolBar toolBar, LocationParameters parameters, DockPosition position, boolean canDrag);
	/**
	 * 
	 * @param toolBar A @see ToolBar to remove from the application.
	 */
	void removeToolBar(ToolBar toolBar);
	/**
	 * 
	 * @return An @see ObservableList{DraggableToolBar} the represents the @see ToolBar 's shown in the top dock area.
	 */
	ObservableList<DraggableToolBar> topToolBars();
	/**
	 * 
	 * @return An @see ObservableList{DraggableToolBar} the represents the @see ToolBar 's shown in the right dock area.
	 */
	ObservableList<DraggableToolBar> rightToolBars();
	/**
	 * 
	 * @return An @see ObservableList{DraggableToolBar} the represents the @see ToolBar 's shown in the left dock area.
	 */
	ObservableList<DraggableToolBar> leftToolBars();
	/**
	 * 
	 * @return An @see ObservableList{DraggableToolBar} the represents the @see ToolBar 's shown in the bottom dock area.
	 */
	ObservableList<DraggableToolBar> bottomToolBars();
}
