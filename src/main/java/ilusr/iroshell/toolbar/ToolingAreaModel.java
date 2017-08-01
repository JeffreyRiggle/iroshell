package ilusr.iroshell.toolbar;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.ObservableListBinder;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.services.IToolBarService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ToolingAreaModel {

	private final ObservableList<DraggableToolBar> toolBars;
	private final SimpleObjectProperty<DockPosition> position;
	private final IToolBarService toolBarService;
	private ObservableListBinder<DraggableToolBar> listBinder;
	
	/**
	 * Creates a tooling area model.
	 */
	public ToolingAreaModel() {
		this(DockPosition.Top, ServiceManager.getInstance().<IToolBarService>get("ToolBarService"));
	}
	
	/**
	 * 
	 * @param position A @see DockPosition.
	 * @param service A @see IToolBarService.
	 */
	public ToolingAreaModel(DockPosition position, IToolBarService service) {
		toolBars = FXCollections.observableArrayList();
		this.position = new SimpleObjectProperty<DockPosition>(position);
		toolBarService = service;
		
		initialize();
	}
	
	private void initialize() {
		position.addListener((l, o, n) -> {
			changePosition(n);
		});
		
		changePosition(position.get());
	}
	
	private void changePosition(DockPosition pos) {
		toolBars.clear();
		
		if (listBinder != null) {
			listBinder.unbind();
		}
		
		switch (pos) {
			case Top:
				updateBinding(toolBarService.topToolBars());
				break;
			case Bottom:
				updateBinding(toolBarService.bottomToolBars());
				break;
			case Left:
				updateBinding(toolBarService.leftToolBars());
				break;
			case Right:
				updateBinding(toolBarService.rightToolBars());
				break;
		}
	}
	
	private void updateBinding(ObservableList<DraggableToolBar> baseList) {
		toolBars.addAll(baseList);
		
		listBinder = new ObservableListBinder<DraggableToolBar>(baseList, toolBars);
	}
	
	/**
	 * 
	 * @return The toolbars shown in this tooling area.
	 */
	public ObservableList<DraggableToolBar> toolBars() {
		return toolBars;
	}
	
	/**
	 * 
	 * @return The position of this tooling area.
	 */
	public SimpleObjectProperty<DockPosition> getPosition() {
		return position;
	}
	
	/**
	 * 
	 * @param position The position of this tooling area.
	 */
	public void setPosition(DockPosition position) {
		this.position.set(position);
	}
}
