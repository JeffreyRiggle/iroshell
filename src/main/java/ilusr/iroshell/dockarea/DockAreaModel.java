package ilusr.iroshell.dockarea;

import java.util.UUID;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Orientation;

public class DockAreaModel {

	private final UUID uniqueId;
	private DockPanelModel dockPanel;
	private SimpleBooleanProperty hasChildren;
	private SimpleObjectProperty<Orientation> orientation;
	private SimpleObjectProperty<Integer> index;
	private SimpleObjectProperty<DockAreaModel> child;
	private SimpleDoubleProperty width;
	private SimpleDoubleProperty height;
	
	public DockAreaModel() {
		uniqueId = UUID.randomUUID();
		hasChildren = new SimpleBooleanProperty();
		orientation = new SimpleObjectProperty<Orientation>();
		index = new SimpleObjectProperty<Integer>();
		child = new SimpleObjectProperty<DockAreaModel>();
		width = new SimpleDoubleProperty();
		height = new SimpleDoubleProperty();
	}
	
	public UUID id() {
		return uniqueId;
	}
	
	public void setDockPanel(DockPanelModel model) {
		dockPanel = model;
	}
	
	public DockPanelModel getDockPanel() {
		return dockPanel;
	}
	
	public SimpleBooleanProperty hasChildren() {
		return hasChildren;
	}
	
	public SimpleObjectProperty<Orientation> orientation() {
		return orientation;
	}
	
	public SimpleObjectProperty<Integer> index() {
		return index;
	}
	
	public SimpleObjectProperty<DockAreaModel> child() {
		return child;
	}
	
	public SimpleDoubleProperty width() {
		return width;
	}
	
	public SimpleDoubleProperty height() {
		return height;
	}
}
