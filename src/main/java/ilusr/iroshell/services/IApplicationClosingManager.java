package ilusr.iroshell.services;

import ilusr.iroshell.core.ApplicationClosingListener;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IApplicationClosingManager extends ApplicationClosingListener {
	/**
	 * 
	 * @param listener A @see ApplicationClosingListener to add to the manager.
	 */
	void addApplicationClosingListener(ApplicationClosingListener listener);
	/**
	 * 
	 * @param listener A @see ApplicationClosingListener to remove from the manager.
	 */
	void removeApplicationClosingListener(ApplicationClosingListener listener);
}
