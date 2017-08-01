package ilusr.iroshell.statusbar;

import java.util.logging.Level;

import ilusr.iroshell.core.LocationParameters;
import ilusr.logrunner.LogRunner;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class StatusBarService implements IStatusBarService{

	private final Object statusBarLock;
	private ObservableList<Node> statusBars;
	private SimpleBooleanProperty hideOnEmpty;
	
	/**
	 * Creates a status bar service.
	 */
	public StatusBarService() {
		statusBarLock = new Object();
		statusBars = FXCollections.observableArrayList();
		hideOnEmpty = new SimpleBooleanProperty();
	}

	@Override
	public void addStatusBar(Node statusBar, LocationParameters location) throws IllegalArgumentException {
		synchronized(statusBarLock) {
			LogRunner.logger().log(Level.INFO, String.format("Adding StatusBar: %s to %s", statusBar, location));
			switch (location.type()) {
				case First:
					statusBars.add(0, statusBar);
					break;
				case Last:
					statusBars.add(statusBar);
					break;
				case Index:
					statusBars.add(location.index(), statusBar);
					break;
				case AfterName:
				case BeforeName:
				default:
					//Should we really throw?
					throw new IllegalArgumentException("Location Type");
			}
		}
	}

	@Override
	public void removeStatusBar(Node statusBar) {
		synchronized(statusBarLock) {
			LogRunner.logger().log(Level.INFO, String.format("Removing status bar: %s", statusBar));
			statusBars.remove(statusBar);
		}
	}

	@Override
	public ObservableList<Node> statusBars() {
		//TODO: This should not be modifiable.
		return statusBars;
	}

	@Override
	public SimpleBooleanProperty hideWhenEmpty() {
		return hideOnEmpty;
	}
}
