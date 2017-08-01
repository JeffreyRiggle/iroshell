package ilusr.iroshell.services;

import javafx.scene.Parent;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface INotification {
	/**
	 * 
	 * @return The @see Parent object to be displayed as a notification.
	 */
	Parent parent();
	/**
	 * 
	 * @return The title for the notification.
	 */
	String title();
	/**
	 * This controls how long a notification will be shown. To have a notification that has 
	 * no auto close time set this value to -1.
	 * 
	 * @return How long the notification should show.
	 */
	long getTimeToLive();
}
