package ilusr.iroshell.documentinterfaces.sdi;

import java.util.Map;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ISDIModel {
	/**
	 * 
	 * @return A @see Map of view id to node.
	 */
	Map<String, Node> viewPool();
	/**
	 * 
	 * @return A @see SimpleObjectProperty of type @see Node that represents the currently selected view.
	 */
	SimpleObjectProperty<Node> currentView();
	/**
	 * 
	 * @return A @see SimpleBooleanProperty that controls if the sizer can be re-sized.
	 */
	SimpleBooleanProperty canResize();
	/**
	 * 
	 * @param viewId The views id to switch to.
	 */
	void changeView(String viewId);
	/**
	 * 
	 * @return The selected view's id.
	 */
	String selectedViewId();
}
