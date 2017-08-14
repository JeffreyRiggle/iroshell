package ilusr.iroshell.services;

import java.util.Timer;
import java.util.TimerTask;

import ilusr.logrunner.LogRunner;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class Notification implements INotification{

	private Parent parent;
	private String title;
	private long ttl;
	private Timer timer;
	
	/**
	 * To have a notification that has no auto close time set ttl to -1.
	 * 
	 * @param parent The @see Parent object to be displayed as a notification.
	 * @param title The title for the notification.
	 * @param ttl How long the notification should show.
	 */
	public Notification(Parent parent, String title, long ttl) {
		this.parent = parent;
		this.title = title;
		this.ttl = ttl;
		startTimer();
	}
	
	@Override
	public Parent parent() {
		return parent;
	}
	
	@Override
	public String title() {
		return title;
	}
	
	public void setTimeToLive(long ttl) {
		this.ttl = ttl;
		startTimer();
	}
	
	@Override
	public long getTimeToLive() {
		return ttl;
	}
	
	private void startTimer() {		
		tryCancelTimer();
		
		if (ttl == -1) {
			return;
		}
		
		LogRunner.logger().info(String.format("Starting close timer for: %s, with ttl: %s", title, ttl));
		
		if (timer == null) {
			timer = new Timer();
		}
		
		timer.schedule(new TimerTask() {
			public void run() {
				close();
			}
		}, ttl);
	}
	
	private void tryCancelTimer() {
		if (timer != null) {
			timer.cancel();
			timer = null;
		}
	}
	
	private void close() {
		Platform.runLater(() -> {
			LogRunner.logger().info(String.format("Closing notification: %s", title));
			Stage stage = (Stage)parent.getScene().getWindow();
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		});
	}
}
