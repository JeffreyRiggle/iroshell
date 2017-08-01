package ilusr.iroshell.documentinterfaces.sdi;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import ilusr.logrunner.LogRunner;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class SDIModel implements ISDIModel {

	private Map<String, Node> viewPool;
	private SimpleObjectProperty<Node> currentView;
	private SimpleBooleanProperty canResize;
	private String selectedViewId;
	
	/**
	 * Base ctor.
	 */
	public SDIModel() {
		viewPool = new HashMap<String, Node>();
		currentView = new SimpleObjectProperty<Node>();
		canResize = new SimpleBooleanProperty(true);
	}
	
	@Override
	public Map<String, Node> viewPool() {
		return viewPool;
	}
	
	@Override
	public SimpleObjectProperty<Node> currentView() {
		return currentView;
	}
	
	@Override
	public void changeView(String viewId) {
		if (!viewPool.containsKey(viewId)) {
			LogRunner.logger().log(Level.INFO, String.format("Unable to find view %s", viewId));
			return;
		}
		
		LogRunner.logger().log(Level.INFO, String.format("Changing view to %s", viewId));
		selectedViewId = viewId;
		currentView.setValue(viewPool.get(viewId));
	}
	
	@Override
	public String selectedViewId() {
		return selectedViewId;
	}
	
	@Override
	public SimpleBooleanProperty canResize() {
		return canResize;
	}
}
