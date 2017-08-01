package ilusr.iroshell.services;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface INotificationService {
	/**
	 * 
	 * @param notification The @see INotification to add to the notification area.
	 */
	void addNotification(INotification notification);
	/**
	 * 
	 * @param notification The @see INotification to remove from the notification area.
	 */
	void removeNotification(INotification notification);
	/**
	 * 
	 * @param max The maximum amount of notifications that can be seen at any time.
	 */
	void setMaxNotifications(int max);
	/**
	 * 
	 * @return The maximum amount of notifications that can be seen at any time.
	 */
	int getMaxNotifications();
}
