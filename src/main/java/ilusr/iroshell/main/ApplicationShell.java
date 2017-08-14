package ilusr.iroshell.main;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.core.ViewSwitcher;
import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.documentinterfaces.mdi.MDIProvider;
import ilusr.iroshell.documentinterfaces.sdi.SDIProvider;
import ilusr.iroshell.menus.IMenuService;
import ilusr.iroshell.menus.MenuService;
import ilusr.iroshell.services.IOverlayProvider;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.LayoutService;
import ilusr.iroshell.services.StyleContainerService;
import ilusr.iroshell.statusbar.StatusBar;
import ilusr.iroshell.toolbar.ToolingArea;
import ilusr.logrunner.LogRunner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ApplicationShell extends GridPane implements Initializable, IStyleWatcher, IOverlayProvider {

	private final InternalURLProvider urlProvider;
	private final StyleContainerService styleService;
	
	private StyleUpdater styleUpdater;

	private IMenuService menuService;
	private LayoutService layoutService;
	private ViewSwitcher<DocumentType> switcher;
	
	@FXML
	private GridPane root;
	
	@FXML
	private GridPane footer;
	
	@FXML
	private StatusBar statusBar;
	
	@FXML
	private StackPane stackPane;
	
	@FXML
	private ToolingArea topToolingArea;
	
	@FXML
	private ToolingArea rightToolingArea;
	
	@FXML
	private ToolingArea leftToolingArea;
	
	@FXML
	private ToolingArea bottomToolingArea;
	
	@FXML
	private BorderPane borderPane;
	
	public ApplicationShell() {
		menuService = ServiceManager.getInstance().<MenuService>get("MenuService");
		layoutService = ServiceManager.getInstance().<LayoutService>get("LayoutService");
		urlProvider = ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider");
		styleService = ServiceManager.getInstance().<StyleContainerService>get("IStyleContainerService");

		FXMLLoader appLoader = new FXMLLoader(getClass().getResource("application_shell.fxml"));
		appLoader.setRoot(this);
		appLoader.setController(this);
		
		try {
			appLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		setupViewSwitcher();
		stackPane.getChildren().add(switcher);
		
		switcher.switchView(layoutService.documentType().get());
		layoutService.documentType().addListener((ob, o, n) -> {
			switcher.switchView(n);
		});
		
		setupMenuBar();
		setupStatusBar();
		setupToolBar();
		styleUpdater = new StyleUpdater(urlProvider, "appstylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.APP), this, false);
	}
	
	private void setupViewSwitcher() {
		SDIProvider sdi = ServiceManager.getInstance().<SDIProvider>get("SDIProvider");
		MDIProvider mdi = ServiceManager.getInstance().<MDIProvider>get("MDIProvider");
		
		switcher = new ViewSwitcher<DocumentType>();
		switcher.addView(DocumentType.MDI, mdi);
		switcher.addView(DocumentType.SDI, sdi);
	}
	
	private void setupMenuBar() {
		Menu file = new Menu("File");
		MenuItem exit = new MenuItem("Exit");
		exit.setOnAction((event) -> {
			LogRunner.logger().info("File -> Exit pressed.");
			Stage stage  = (Stage)super.getScene().getWindow();
			stage.getOnCloseRequest().handle(new WindowEvent(stage, WindowEvent.WINDOW_CLOSE_REQUEST));
		});
		file.getItems().add(exit);
		menuService.addMenu(file, LocationProvider.last());
	}
	
	private void setupStatusBar() {
		if (!statusBar.visibleProperty().get()) {
			collapseStatusBar();
		}
		
		statusBar.visibleProperty().addListener((p, o, n) -> {
			if (n) {
				expandStatusBar();
			} else {
				collapseStatusBar();
			}
		});
	}
	
	private void setupToolBar() {
		topToolingArea.model().setPosition(DockPosition.Top);
		bottomToolingArea.model().setPosition(DockPosition.Bottom);
		leftToolingArea.model().setPosition(DockPosition.Left);
		rightToolingArea.model().setPosition(DockPosition.Right);
	}
	
	private void collapseStatusBar() {
		LogRunner.logger().info("Collapsing status bar.");
		footer.getRowConstraints().get(1).setMinHeight(0);
		footer.getRowConstraints().get(1).setPrefHeight(0);
		footer.getRowConstraints().get(1).setMaxHeight(0);
	}
	
	private void expandStatusBar() {
		LogRunner.logger().info("Expanding status bar.");
		footer.getRowConstraints().get(1).setMinHeight(25);
		footer.getRowConstraints().get(1).setMaxHeight(30);
		footer.getRowConstraints().get(1).setPrefHeight(30);
	}
	
	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}

	@Override
	public void overlay(Node node) {
		LogRunner.logger().fine("Displaying overlay");
		stackPane.getChildren().add(0, node);
	}
	
	@Override
	public void clearOverlay(Node node) {
		LogRunner.logger().fine("Removing overlay");
		stackPane.getChildren().remove(node);
	}
}