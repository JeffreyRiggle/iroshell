package ilusr.iroshell.dockarea;

import java.util.UUID;

import ilusr.iroshell.services.IStyleContainerService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DockPanelModel implements ISelectable{

	private ObservableList<DockTab> tabs;
	private final UUID uniqueId;
	private final IStyleContainerService styleService;
	private boolean selected;
	private SimpleObjectProperty<Integer> selectedTab;
	
	public DockPanelModel(IStyleContainerService styleService) {
		this.styleService = styleService;
		tabs = FXCollections.observableArrayList();
		uniqueId = UUID.randomUUID();
		selectedTab = new SimpleObjectProperty<Integer>();
	}
	
	public ObservableList<DockTab> tabs() {
		return tabs;
	}
	
	public SimpleObjectProperty<Integer> selectedTab() {
		return selectedTab;
	}
	
	public final UUID id() {
		return uniqueId;
	}
	
	public String panelDragStyle() {
		return styleService.get(DockStyleNames.PANEL_DRAG);
	}
	
	public boolean selected() {
		return selected;
	}

	@Override
	public void select() {
		selected = true;
	}

	@Override
	public void deSelect() {
		selected = false;
	}
}
