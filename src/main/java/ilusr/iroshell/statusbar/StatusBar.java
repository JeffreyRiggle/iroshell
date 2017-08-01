package ilusr.iroshell.statusbar;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.StyleContainerService;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class StatusBar extends AnchorPane implements Initializable, ListChangeListener<Node>, IStyleWatcher{

	private final Object statusLock;
	private final InternalURLProvider urlProvider;
	private final StyleContainerService styleService;

	private StatusBarModel model;
	private StyleUpdater styleUpdater;
	
	@FXML
	private HBox statusBar;
	
	@FXML
	private VBox vbox;
	
	/**
	 * Creates a status bar.
	 */
	public StatusBar() {
		urlProvider = ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider");
		statusLock = new Object();
		model = ServiceManager.getInstance().<StatusBarModel>get("StatusBarModel");
		styleService = ServiceManager.getInstance().<StyleContainerService>get("IStyleContainerService");

		
		FXMLLoader statusLoader = new FXMLLoader(getClass().getResource("statusbar.fxml"));
		statusLoader.setRoot(this);
		statusLoader.setController(this);
		
		try {
			statusLoader.load();
		} catch (Exception exception) {
			//TODO
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		for (Node item : model.statusBars()) {
			statusBar.getChildren().add(item);
			statusBar.getChildren().add(new Separator(Orientation.VERTICAL));
		}
		
		if (statusBar.getChildren().size() == 0 && model.hideWhenEmpty().get()) {
			super.setVisible(false);
		}
		
		model.statusBars().addListener(this);
		
		model.hideWhenEmpty().addListener((p, o, n) -> {
			if (statusBar.getChildren().size() == 0 && n) {
				super.setVisible(false);
			} else if (!n && !super.visibleProperty().get()) {
				super.setVisible(true);
			}
		});
		
		super.getStyleClass().add("pane");
		statusBar.getStyleClass().add("pane");
		vbox.getStyleClass().add("pane");
		
		styleUpdater = new StyleUpdater(urlProvider, "statusstylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.STATUS), this, false);
	}

	@Override
	public void onChanged(ListChangeListener.Change<? extends Node> arg0) {
		if (!arg0.next()) return;
		
		final List<? extends Node> removedItems = arg0.getRemoved();
		final List<? extends Node> addedItems = arg0.getList();
		Platform.runLater(() -> {
				synchronized(statusLock) {
					if (removedItems.size() > 0) {
						statusBar.getChildren().removeAll(removedItems);
					}
					if (addedItems.size() > 0) {
						for (Node n : addedItems) {
							if (statusBar.getChildren().contains(n)) continue;
							
							statusBar.getChildren().add(n);
							statusBar.getChildren().add(new Separator(Orientation.VERTICAL));
						}
					}
					
					if (statusBar.getChildren().size() == 0 && model.hideWhenEmpty().get()) {
						super.setVisible(false);
					} else if (!super.visibleProperty().get()) {
						super.setVisible(true);
					}
				}
			});
	}
	
	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}
}
