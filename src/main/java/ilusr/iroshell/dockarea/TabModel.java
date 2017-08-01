package ilusr.iroshell.dockarea;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.MenuItem;

public class TabModel implements ICloseable {

	private SimpleObjectProperty<Node> title;
	private String id;
	private SimpleObjectProperty<Node> content;
	private SimpleBooleanProperty canClose;
	private SimpleStringProperty toolTip;
	private List<ICloseable> closeListeners;
	private ISelectable selectable;
	private ObservableList<MenuItem> contextMenuItems;
	private boolean hasClosed;
	
	public TabModel() {
		this(UUID.randomUUID().toString());
	}
	
	public TabModel(String id) {
		title = new SimpleObjectProperty<Node>();
		toolTip = new SimpleStringProperty();
		canClose = new SimpleBooleanProperty();
		content = new SimpleObjectProperty<Node>();
		closeListeners = new ArrayList<ICloseable>();
		contextMenuItems = FXCollections.observableArrayList();
		this.id = id;
	}
	
	public SimpleObjectProperty<Node> title() {
		return title;
	}
	
	public SimpleObjectProperty<Node> content() {
		return content;
	}
	
	public ObservableList<MenuItem> contextMenuItems() {
		return contextMenuItems;
	}
	
	public final String id() {
		return id;
	}
	
	public SimpleBooleanProperty canClose() {
		return canClose;
	}
	
	public SimpleStringProperty toolTip() {
		return toolTip;
	}
	
	public void addCloseListener(ICloseable closeable) {
		closeListeners.add(closeable);
	}
	
	public void removeCloseListener(ICloseable closeable) {
		closeListeners.remove(closeable);
	}
	
	@Override
	public void close() {
		if (hasClosed) {
			return;
		}
		
		hasClosed = true;
		
		for (ICloseable closeable : closeListeners) {
			closeable.close();
		}
	}
	
	public void setSelectionListener(ISelectable selectable) {
		this.selectable = selectable;
	}
	
	public void select() {
		selectable.select();
	}
}
