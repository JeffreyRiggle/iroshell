package ilusr.iroshell.services;

import java.util.logging.Level;

import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.toolbar.DraggableToolBar;
import ilusr.logrunner.LogRunner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.control.ToolBar;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ToolBarService implements IToolBarService {

	private final InternalURLProvider urlProvider;
	private final StyleContainerService styleService;
	
	private ObservableList<DraggableToolBar> topToolBars;
	private ObservableList<DraggableToolBar> leftToolBars;
	private ObservableList<DraggableToolBar> rightToolBars;
	private ObservableList<DraggableToolBar> bottomToolBars;
	
	/**
	 * Creates a new ToolBarService.
	 */
	public ToolBarService(InternalURLProvider urlProvider, StyleContainerService styleService) {
		this.urlProvider = urlProvider;
		this.styleService = styleService;
		topToolBars = FXCollections.observableArrayList();
		leftToolBars = FXCollections.observableArrayList();
		rightToolBars = FXCollections.observableArrayList();
		bottomToolBars = FXCollections.observableArrayList();
	}
	
	@Override
	public void addToolBar(ToolBar toolBar, LocationParameters parameters, DockPosition position) {
		addToolBar(toolBar, parameters, position, true);
	}
	
	@Override
	public void addToolBar(ToolBar toolBar, LocationParameters parameters, DockPosition position, boolean canDrag) {
		LogRunner.logger().log(Level.INFO, String.format("Adding toolbar: %s, to %s with parameters %s and canDrag %s", toolBar, position, parameters, canDrag));
		switch(position) {
			case Top:
				toolBar.orientationProperty().set(Orientation.HORIZONTAL);
				addToolBarImpl(toolBar, parameters, topToolBars, canDrag);
				return;
			case Left:
				toolBar.orientationProperty().set(Orientation.VERTICAL);
				addToolBarImpl(toolBar, parameters, leftToolBars, canDrag);
				return;
			case Right:
				toolBar.orientationProperty().set(Orientation.VERTICAL);
				addToolBarImpl(toolBar, parameters, rightToolBars, canDrag);
				return;
			case Bottom:
				toolBar.orientationProperty().set(Orientation.HORIZONTAL);
				addToolBarImpl(toolBar, parameters, bottomToolBars, canDrag);
				return;
		}
	}
	
	@Override
	public void removeToolBar(ToolBar toolBar) {
		LogRunner.logger().log(Level.INFO, String.format("Attempting to remove toolbar: %s", toolBar));
		if (removeToolBarImpl(toolBar, topToolBars)) {
			return;
		}
		
		if (removeToolBarImpl(toolBar, leftToolBars)) {
			return;
		}
		
		if (removeToolBarImpl(toolBar, rightToolBars)) {
			return;
		}
		
		if (removeToolBarImpl(toolBar, bottomToolBars)) {
			return;
		}
	}
	
	@Override
	public final ObservableList<DraggableToolBar> topToolBars() {
		//return FXCollections.unmodifiableObservableList(topToolBars);
		return topToolBars;
	}
	
	@Override
	public final ObservableList<DraggableToolBar> rightToolBars() {
		//return FXCollections.unmodifiableObservableList(rightToolBars);
		return rightToolBars;
	}
	
	@Override
	public final ObservableList<DraggableToolBar> leftToolBars() {
		//return FXCollections.unmodifiableObservableList(leftToolBars);
		return leftToolBars;
	}
	
	@Override
	public final ObservableList<DraggableToolBar> bottomToolBars() {
		//return FXCollections.unmodifiableObservableList(bottomToolBars);
		return bottomToolBars;
	}
	
	private void addToolBarImpl(ToolBar toolBar, LocationParameters parameters, ObservableList<DraggableToolBar> collection, boolean canDrag) {
		DraggableToolBar dToolBar = new DraggableToolBar(toolBar, canDrag, urlProvider, styleService);
		
		switch (parameters.type()) {
			case AfterName:
				collection.add(findToolBarIndex(toolBar, parameters.locationName(), collection)+1, dToolBar);
				break;
			case BeforeName:
				collection.add(findToolBarIndex(toolBar, parameters.locationName(), collection), dToolBar);
				break;
			case First:
				collection.add(0, dToolBar);
				break;
			case Last:
				collection.add(dToolBar);
				break;
			case Index:
				collection.add(parameters.index(), dToolBar);
				break;
		}
	}
	
	private int findToolBarIndex(ToolBar toolbar, String name, ObservableList<DraggableToolBar> collection) {
		int retVal = -1;
		
		for (DraggableToolBar dtb : collection) {
			retVal++;
			
			if (dtb.toolBar().getId().equals(name)) {
				return retVal;
			}
		}
		
		retVal = 0;
		return retVal;
	}
	
	private boolean removeToolBarImpl(ToolBar toolBar, ObservableList<DraggableToolBar> collection) {
		for (DraggableToolBar dtb : collection) {
			if (dtb.toolBar() != toolBar) {
				continue;
			}
			
			collection.remove(dtb);
			return true;
		}
		
		return false;
	}
}
