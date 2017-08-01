package ilusr.iroshell.services;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import ilusr.logrunner.LogRunner;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class NotificationService implements INotificationService{

	private Map<INotification, Stage> shownNotifications;
	private List<INotification> queuedNotifications;
	
	private int maxNotifications;
	
	/**
	 * Creates a Notification Service.
	 */
	public NotificationService() {
		this(new LinkedHashMap<INotification, Stage>(), new ArrayList<INotification>());
	}
	
	/**
	 * 
	 * @param shown The Shown Notifications.
	 * @param queue The queued up notifications.
	 */
	public NotificationService(Map<INotification, Stage> shown, List<INotification> queue) {
		shownNotifications = shown;
		queuedNotifications = queue;
		maxNotifications = 1;
	}
	
	@Override
	public void addNotification(INotification notification) {
		if (shownNotifications.size() == maxNotifications) {
			LogRunner.logger().log(Level.INFO, String.format("Queuing up notification: %s", notification.title()));
			queuedNotifications.add(notification);
			return;
		}

		LogRunner.logger().log(Level.INFO, String.format("Adding notification: %s, to shown list.", notification.title()));
		shownNotifications.put(notification, createAndShowStage(notification));
	}
	
	@Override
	public void removeNotification(INotification notification) {
		removeNotificationImpl(notification);
	}
	
	private void removeNotificationImpl(INotification notification) {
		if (queuedNotifications.contains(notification)) {
			LogRunner.logger().log(Level.INFO, String.format("Removing notification: %s, from queue.", notification.title()));
			queuedNotifications.remove(notification);
			return;
		}
		
		LogRunner.logger().log(Level.INFO, String.format("Removing and closing notification: %s", notification.title()));
		shownNotifications.get(notification).close();
		shownNotifications.remove(notification);
		showNext();
	}
	
	private boolean showNext() {
		if (queuedNotifications.size() <= 0 || shownNotifications.size() >= maxNotifications) {
			return false;
		}
		
		INotification next = queuedNotifications.get(0);
		
		LogRunner.logger().log(Level.INFO, String.format("Moving notification: %s, from queue to shown", next.title()));
		shownNotifications.put(next, createAndShowStage(next));
		queuedNotifications.remove(0);
		return true;
	}
	
	private Stage createAndShowStage(INotification parent) {
		Stage notificationStage = new Stage();
		notificationStage.setTitle(parent.title());
		notificationStage.initStyle(StageStyle.UTILITY);
		notificationStage.setScene(new Scene(parent.parent()));
		notificationStage.show();
		positionStage(notificationStage);
		
		notificationStage.setOnCloseRequest((e) -> {
			removeNotificationImpl(parent);
		});
		
		return notificationStage;
	}
	
	private void positionStage(Stage stage) {
		Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
		
		if (shownNotifications.size() == 0) {
			stage.setX(screenBounds.getMaxX() - stage.getWidth());
			stage.setY(screenBounds.getMaxY() - stage.getHeight());
			return;
		}
		
		List<Entry<INotification, Stage>> entryList = new ArrayList<Entry<INotification, Stage>>(shownNotifications.entrySet());
		Entry<INotification, Stage> lastEntry = entryList.get(entryList.size()-1);
		Stage lastStage = lastEntry.getValue();
		
		if (lastStage.getY() - stage.getHeight() - 5 < screenBounds.getMinY()) {
			stage.setX(lastStage.getX() - 5 - stage.getWidth());
			stage.setY(screenBounds.getMaxY() - stage.getHeight());
			return;
		} 
		
		stage.setX(screenBounds.getMaxX() - stage.getWidth());
		stage.setY(lastStage.getY() - 5 - stage.getHeight());
	}
	
	@Override
	public void setMaxNotifications(int max) {
		LogRunner.logger().log(Level.INFO, String.format("Updating max notifications to %s", max));
		maxNotifications = max;
		
		boolean addQueuedNotifications = true;
		while (addQueuedNotifications) {
			addQueuedNotifications = showNext();
		}
	}
	
	@Override
	public int getMaxNotifications() {
		return maxNotifications;
	}
}
