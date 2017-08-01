package ilusr.iroshell.core;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import ilusr.logrunner.LogRunner;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jeff Riggle
 *
 * @param <T> The switcher key type.
 */
@SuppressWarnings("rawtypes")
public class ViewSwitcher<T> extends AnchorPane{

	private Map<T, IViewProvider> viewMap;
	
	/**
	 * Creates a view switcher.
	 */
	public ViewSwitcher() {
		viewMap = new HashMap<T, IViewProvider>();
	}
	
	/**
	 * 
	 * @param viewId The key for the view.
	 * @param view A @see ViewProvider to use to generate the view.
	 */
	public void addView(T viewId, IViewProvider view) {
		LogRunner.logger().log(Level.INFO, String.format("Adding view id: %s", viewId));
		viewMap.put(viewId, view);
	}
	
	/**
	 * 
	 * @param viewId The key for the view.
	 */
	public void removeView(T viewId) {
		LogRunner.logger().log(Level.INFO, String.format("Removing view id: %s", viewId));
		viewMap.remove(viewId);
	}
	
	/**
	 * 
	 * @param viewId The view to switch to.
	 */
	public void switchView(T viewId) {
		if (!viewMap.containsKey(viewId)) {
			LogRunner.logger().log(Level.INFO, String.format("Unable to find view id: %s", viewId));
			return;
		}
		
		Node view = viewMap.get(viewId).getView();
		this.getChildren().clear();
		this.getChildren().add(view);
		AnchorPane.setBottomAnchor(view, 0.0);
		AnchorPane.setTopAnchor(view, 0.0);
		AnchorPane.setLeftAnchor(view, 0.0);
		AnchorPane.setRightAnchor(view, 0.0);
	}
	
	/**
	 * Removes all views from the view map.
	 */
	public void clearViews() {
		LogRunner.logger().log(Level.INFO, "Clearing all views from view switcher");
		viewMap.clear();
	}
}
