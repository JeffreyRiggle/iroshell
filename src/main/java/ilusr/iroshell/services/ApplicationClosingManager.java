package ilusr.iroshell.services;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ilusr.iroshell.core.ApplicationClosingListener;
import ilusr.logrunner.LogRunner;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ApplicationClosingManager implements IApplicationClosingManager {

	private final List<ApplicationClosingListener> listeners;
	
	/**
	 * Creates an application closing manager.
	 */
	public ApplicationClosingManager() {
		this(new ArrayList<ApplicationClosingListener>());
	}
	
	public ApplicationClosingManager(List<ApplicationClosingListener> listeners) {
		this.listeners = listeners;
	}
	
	@Override
	public void addApplicationClosingListener(ApplicationClosingListener listener) {
		listeners.add(listener);
	}
	
	@Override
	public void removeApplicationClosingListener(ApplicationClosingListener listener) {
		listeners.remove(listener);
	}
	
	@Override
	public void onClose() {
		LogRunner.logger().log(Level.INFO, "Notifying listeners about application closing.");
		for (ApplicationClosingListener listener : listeners) {
			listener.onClose();
		}
	}
}
