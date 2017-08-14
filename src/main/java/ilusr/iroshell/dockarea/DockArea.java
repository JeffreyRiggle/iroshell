package ilusr.iroshell.dockarea;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.LocalDragboard;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.dockarea.overlay.DockOverlay;
import ilusr.iroshell.dockarea.overlay.DockPosition;
import ilusr.iroshell.dockarea.overlay.IDropListener;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.logrunner.LogRunner;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;

public class DockArea extends AnchorPane implements Initializable, IDropListener, ListChangeListener<Tab>, IStyleWatcher {

	@FXML
	private DockPanel dockPanel;

	@FXML
	private StackPane stackPane;
	
	private DockOverlay dockOverlay;
	private SplitPane splitPane;
	private DockAreaModel model;
	private DockArea splitDock;
	
	//TODO: Should this be in the model?
	private StyleUpdater styleUpdater;
	private final IStyleContainerService styleService;
	private final InternalURLProvider urlProvider;
	
	public DockArea() {
		this(ServiceManager.getInstance().<DockAreaModel>get("DockAreaModel"));
	}
	
	public DockArea(DockAreaModel model) {
		this(model, ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider"), ServiceManager.getInstance().<IStyleContainerService>get("IStyleContainerService"));
	}
	
	public DockArea(DockAreaModel model, InternalURLProvider urlProvider, IStyleContainerService styleService) {
		this.model = model;
		this.urlProvider = urlProvider;
		this.styleService = styleService;
		
		LogRunner.logger().info(String.format("Created new area controller with id: %s", this.model.id()));
		FXMLLoader dockLoader = new FXMLLoader(getClass().getResource("dockarea.fxml"));
		dockLoader.setRoot(this);
		dockLoader.setController(this);
		
		try {
			dockLoader.load();
			dockOverlay = new DockOverlay();
			dockOverlay.model().addListener(this);
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model.setDockPanel(dockPanel.model());
		model.width().bind(dockPanel.widthProperty());
		model.height().bind(dockPanel.heightProperty());
		
		dockPanel.setOnDragEntered((event) -> {
			onDragEntered(event);
		});
		dockPanel.setOnDragOver((event) -> {
			onDragOver(event);
		});
		
		styleUpdater = new StyleUpdater(urlProvider, "dockstylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.DOCKAREA), this, false);
	}
	
	public DockAreaModel model() {
		return model;
	}
	
	public double getDockWidth() {
		return super.getWidth();
	}
	
	public double getDockHeight() {
		return super.getHeight();
	}
	
	public void setModel(DockAreaModel model) {
		if (this.model != null) {
			LogRunner.logger().info("Cleaning up and unbinding model.");
			this.model.setDockPanel(null);
			this.model.width().unbind();
			this.model.height().unbind();
		}
		
		LogRunner.logger().info("Setting up model.");
		tabs().clear();
		this.model = model;
		tabs().addAll(this.model.getDockPanel().tabs());
		dockPanel.model().selectedTab().set(this.model.getDockPanel().selectedTab().get());
		
		if (this.model.hasChildren().get()) {
			restoreDockArea(this.model.orientation().get(), this.dockPanel, this.model.index().get(), this.model.child().get());
		}
			
		this.model.setDockPanel(dockPanel.model());
		this.model.width().bind(dockPanel.widthProperty());
		this.model.height().bind(dockPanel.heightProperty());
	}
	
	public ObservableList<DockTab> tabs() {
		return dockPanel.tabs();
	}
	
	public void selectTab(DockTab tab) {
		LogRunner.logger().fine(String.format("Selecting tab: %s", tab.idProperty().get()));
		dockPanel.updateSelection(tab);
	}
	
	private void onDragEntered(DragEvent event) {		
		if (!LocalDragboard.getInstance().hasType(DockTab.class)) {
			return;
		}
	
		if (inBox(event) && dockPanel.tabs().contains(LocalDragboard.getInstance().getValue(DockTab.class))) {
			return;
		}
		
		setupOverlay(event);
	}
	
	private void onDragLeftOverlay(DragEvent event) {
		LogRunner.logger().fine("Removing Overlay.");
		
        if (stackPane.getChildren().contains(dockOverlay)) {
        	stackPane.getChildren().remove(dockOverlay);
        }
        
        event.consume();
	}
	
	private void onDragOver(DragEvent event) {
		if (!LocalDragboard.getInstance().hasType(DockTab.class) || inBox(event)) return;
		
		setupOverlay(event);
	}
	
	private boolean inBox(DragEvent event) {
		return dockPanel.headerRegion().contains(event.getX(), event.getY());
	}
	
	private void setupOverlay(DragEvent event) {
		LogRunner.logger().fine("Adding Overlay.");
		
		event.acceptTransferModes(TransferMode.ANY);

		if (stackPane.getChildren().contains(dockOverlay)) {
			event.consume();
			return;
		}
		
        stackPane.getChildren().add(dockOverlay);
        
		dockOverlay.setOnDragExited((dEvent) -> {
			onDragLeftOverlay(dEvent);
		});
		
		event.consume();
	}
	
	@Override
	public void dragDropped(DockPosition pos) {
		
		if (!LocalDragboard.getInstance().hasType(DockTab.class)) return;
		
		final DockTab tab = LocalDragboard.getInstance().getValue(DockTab.class);
		
		Platform.runLater(() -> {
			createTabArea(pos, tab);
		});
	}
	
	private void createTabArea(DockPosition pos, DockTab tab) {
				switch (pos) {
					case Left:
						stackPane.getChildren().remove(dockPanel);
						stackPane.getChildren().add(addDockArea(Orientation.HORIZONTAL, dockPanel, 0, tab));
						break;
					case Right:
						stackPane.getChildren().remove(dockPanel);
						stackPane.getChildren().add(addDockArea(Orientation.HORIZONTAL, dockPanel, 1, tab));
						break;
					case Top:
						stackPane.getChildren().remove(dockPanel);
						stackPane.getChildren().add(addDockArea(Orientation.VERTICAL, dockPanel, 0, tab));
						break;
					case Bottom:
						stackPane.getChildren().remove(dockPanel);
						stackPane.getChildren().add(addDockArea(Orientation.VERTICAL, dockPanel, 1, tab));
						break;
					case Center:
						if (dockPanel.tabs().contains(tab)) return;
						tab.getTabPane().getTabs().remove(tab);
						dockPanel.tabs().add(tab);
						break;
				}
	}

	private Node addDockArea(Orientation orientation, DockPanel parent, int index, DockTab tab) {
		LogRunner.logger().info("Adding Dock Area.");
		//TODO Fix this up a lot
		try {
			DockArea container = new DockArea();
			tab.getTabPane().getTabs().remove(tab);
			container.tabs().add(tab);
			splitPane = new SplitPane();
			splitPane.orientationProperty().set(orientation);
			if (index == 0) {
				splitPane.getItems().add(container);
			}
			DockArea parentDock = new DockArea();
			parentDock.tabs().clear();
			parentDock.tabs().addAll(parent.tabs());
			splitPane.getItems().add(parentDock);
			if (index != 0) {
				splitPane.getItems().add(container);
			}
			splitDock = parentDock;
			
			//TODO: Figure this out I think its a memory leak.
			container.tabs().addListener(this);
			container.selectTab(tab);
			//TODO Update model
			model.orientation().set(orientation);
			model.index().set(index);
			model.hasChildren().set(true);
			model.child().set(container.model());
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
		return splitPane;
	}
	
	private void restoreDockArea(Orientation orientation, DockPanel parent, int index, DockAreaModel model) {
		LogRunner.logger().info("Restoring Dock Area.");
		//TODO Fix this up a lot
		try {
			DockArea container = new DockArea();
			splitPane = new SplitPane();
			splitPane.orientationProperty().set(orientation);
			if (index == 0) {
				splitPane.getItems().add(container);
			}
			DockArea parentDock = new DockArea();
			parentDock.tabs().clear();
			parentDock.tabs().addAll(parent.tabs());
			splitPane.getItems().add(parentDock);
			if (index != 0) {
				splitPane.getItems().add(container);
			}
			splitDock = parentDock;
			
			//TODO: Figure this out I think its a memory leak.
			container.tabs().addListener(this);
			stackPane.getChildren().remove(dockPanel);
			stackPane.getChildren().add(splitPane);
			container.setModel(model);
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
	}
	
	@Override
	public void onChanged(Change<? extends Tab> c) {
		if (!c.next()) return;
		
		if (!c.wasRemoved() || c.getList().size() > 0) return;
		
		Platform.runLater(() -> {
				System.out.println("All panels missing clearing panel");
				splitPane.getItems().clear();
				stackPane.getChildren().remove(splitPane);
				stackPane.getChildren().add(splitDock);
			});
	}
	
	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}
}