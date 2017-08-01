package ilusr.iroshell.services;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import ilusr.iroshell.dockarea.ICloseable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

/**
 * Data model that gets converted down to a tab.
 * This will separate the need for the consumer to really
 * generate the tab.
 * 
 * @author Jeff Riggle
 *
 */
public class TabContent implements ITabContent{

	private String uniqueId;
	private SimpleObjectProperty<Node> content;
	private SimpleBooleanProperty closeable;
	private SimpleStringProperty toolTip;
	private SimpleObjectProperty<Node> title;
	private SimpleStringProperty customData;
	private List<ICloseable> closeListeners;
	private ObservableList<MenuItem> contextMenuItems;
	
	/**
	 * Base Ctor.
	 */
	public TabContent() {
		uniqueId = UUID.randomUUID().toString();
		closeable = new SimpleBooleanProperty();
		toolTip = new SimpleStringProperty();
		title = new SimpleObjectProperty<Node>();
		content = new SimpleObjectProperty<Node>();
		customData = new SimpleStringProperty();
		closeListeners = new ArrayList<ICloseable>();
		contextMenuItems = FXCollections.observableArrayList();
	}
	
	@Override
	public String getId() {
		return uniqueId;
	}
	
	@Override
	public void setId(String id) {
		uniqueId = id;
	}
	
	@Override
	public SimpleObjectProperty<Node> content() {
		return content;
	}
	
	@Override
	public SimpleBooleanProperty canClose() {
		return closeable;
	}
	
	@Override
	public SimpleStringProperty toolTip() {
		return toolTip;
	}
	
	@Override
	public ObservableList<MenuItem> contextMenuItems() {
		return contextMenuItems;
	}
	
	@Override
	public void titleGraphic(Node title) {
		this.title.setValue(title);
	}
	
	@Override
	public void titleGraphic(String title) {
		this.title.setValue(new Label(title));
	}
	
	@Override
	public SimpleObjectProperty<Node> titleGraphic() {
		return title;
	}
	
	@Override
	public SimpleStringProperty customData() {
		return customData;
	}
	
	@Override
	public void close() {
		System.out.println("Got Close");
		List<ICloseable> closers = new ArrayList<ICloseable>(closeListeners);
		for(ICloseable closer : closers) {
			closer.close();
		}
	}
	
	@Override
	public void addCloseListener(ICloseable closeable) {
		closeListeners.add(closeable);
	}
	
	@Override
	public void removeCloseListener(ICloseable closeable) {
		closeListeners.add(closeable);
	}
	
	@Override
	public void dispose() {
		title.unbind();
		toolTip.unbind();
		closeable.unbind();
		content.unbind();
		customData.unbind();
	}
}
