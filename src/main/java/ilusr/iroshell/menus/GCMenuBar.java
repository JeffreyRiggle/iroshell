package ilusr.iroshell.menus;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.StyleContainerService;
import ilusr.logrunner.LogRunner;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class GCMenuBar extends AnchorPane implements ListChangeListener<Menu>, Initializable, IStyleWatcher {

	private final MenuService menuService;
	private final StyleContainerService styleService;
	private final InternalURLProvider urlProvider;
	private StyleUpdater styleUpdater;
	
	@FXML
	private MenuBar menuBar;
	
	public GCMenuBar() {
		urlProvider = ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider");
		menuService = ServiceManager.getInstance().<MenuService>get("MenuService");
		styleService = ServiceManager.getInstance().<StyleContainerService>get("IStyleContainerService");
		
		FXMLLoader menuLoader = new FXMLLoader(getClass().getResource("gc_menu_bar.fxml"));
		menuLoader.setRoot(this);
		menuLoader.setController(this);
		
		try {
			menuLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		menuBar.getMenus().addAll(menuService.menus());
		menuService.menus().addListener(this);
		
		styleUpdater = new StyleUpdater(urlProvider, "menustylesheet.css", menuBar);
		
		styleService.startWatchStyle(Arrays.asList(StyleArea.MENU), this, false);
	}
	
	@Override
	public void onChanged(Change<? extends Menu> c) {
		if (!c.next()) return;
		
		final Change<? extends Menu> change = c;
		Platform.runLater(() -> {
				//TODO: Consider if this is the best way to do this. It seems error prone
				menuBar.getMenus().clear();
				menuBar.getMenus().addAll(change.getList());
			});
	}

	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}
}
