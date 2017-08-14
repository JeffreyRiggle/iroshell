package ilusr.iroshell.dockarea;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import com.sun.javafx.scene.control.skin.TabPaneSkin;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Rectangle;
import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.LocalDragboard;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.StyleContainerService;
import ilusr.logrunner.LogRunner;

@SuppressWarnings("restriction")
public class DockPanel extends AnchorPane implements Initializable, ListChangeListener<DockTab>, IStyleWatcher {

	@FXML
	private TabPane dockArea;
	
	private DockPanelModel dockPanelModel;
	private final Object tabLock = new Object();
	//TODO: Should this really be here?
	private final SelectionManager selectionManager;

	//TODO: Should this be in the model?
	//TODO: Replace with styleupdater.
	private final StyleContainerService styleService;
	private final InternalURLProvider urlProvider;
	private StyleUpdater styleUpdater;
	
	public DockPanel() {
		this(ServiceManager.getInstance().<DockPanelModel>get("DockPanelModel"));
	}
	
	public DockPanel(DockPanelModel model) {
		dockPanelModel = model;
		selectionManager = ServiceManager.getInstance().<SelectionManager>get("SelectionManager");
		
		urlProvider = ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider");
		styleService = ServiceManager.getInstance().<StyleContainerService>get("IStyleContainerService");

		FXMLLoader dockLoader = new FXMLLoader(getClass().getResource("dockpanel.fxml"));
		dockLoader.setRoot(this);
		dockLoader.setController(this);
		
		try {
			dockLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		selectionManager.addSelectionRequester(dockPanelModel);
		setupDragAndDrop();
		dockPanelModel.tabs().addListener(this);
		
		//TODO: This feels like a hack
		selectionManager.RequestSelection(dockPanelModel);
		
		dockArea.getTabs().addListener((ListChangeListener.Change<? extends Tab> c) -> {
			baseCollectionChanged(c);
		});
		
		dockArea.selectionModelProperty().getValue().selectedItemProperty().addListener((ov, oldTab, newTab) -> {
			selectionManager.RequestSelection(dockPanelModel);
		});
		
		dockArea.selectionModelProperty().getValue().selectedIndexProperty().addListener((l, o, n) -> {
			dockPanelModel.selectedTab().set((Integer)n);
		});
		
		dockPanelModel.selectedTab().addListener((list, oldVal, newVal) -> {
			if (dockArea.selectionModelProperty().get().getSelectedIndex() == newVal) {
				return;
			}
			
			dockArea.selectionModelProperty().get().select(newVal);
		});
		
		styleUpdater = new StyleUpdater(urlProvider, "panelstylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.DOCKPANEL), this, false);
	}
	
	private void baseCollectionChanged(Change<? extends Tab> c) {
		if (!c.next()) return;

		synchronized(tabLock) {
			for (Tab t : c.getRemoved()) {
				if (!dockPanelModel.tabs().contains(t)) continue;
			
				dockPanelModel.tabs().remove(t);
			}
		
			for (Tab t : c.getAddedSubList()) {
				if (dockPanelModel.tabs().contains(t)) continue;
			
				dockPanelModel.tabs().add((DockTab)t);
			}
		}
	}
	
	private void setupDragAndDrop() {
		dockArea.setOnDragOver((event) -> {
			onDrag(event);
		});
		dockArea.setOnDragDropped((event) -> {
			onDrop(event);
		});
	}
	
	private void onDrag(DragEvent event) {
		if (!LocalDragboard.getInstance().hasType(DockTab.class)) {
			return;
        }
		
        event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
        dockArea.styleProperty().set(dockPanelModel.panelDragStyle());
        dockArea.applyCss();
	}
	
	private void onDrop(DragEvent event) {
        boolean success = false;
        
        if (LocalDragboard.getInstance().hasType(DockTab.class)) {
        	DockTab tab = LocalDragboard.getInstance().getValue(DockTab.class);
        	
        	int index = findNearestIndex(event.getSceneX(), event.getSceneY());
        	if (tab != null) {
            	dockArea.getTabs().remove(tab);
            	dockArea.getTabs().add(index, tab);

        		updateSelection(tab);
            	success = true;
        	}
        }
        
        event.setDropCompleted(success);
        dockArea.styleProperty().set("");
        dockArea.applyCss();
	}
	
	private int findNearestIndex(double x, double y) {
		double remainingWidth = x;
		int index = -1;
		
		synchronized(tabLock) {
			for (Tab t : dockArea.getTabs()) {
				double width = t.getGraphic().getBoundsInLocal().getWidth();
			
				remainingWidth = remainingWidth - width;
			
				if (remainingWidth < 0) break;
			
				index++;
			}
		}
		
		if (index == -1) {
			index = 0;
		}
		
		return index;
	}
	
	public void updateSelection(Tab tab) {
		Platform.runLater(() -> {
			synchronized(tabLock) {
				dockArea.getSelectionModel().select(tab);
			}
		});
	}

	public DockPanelModel model() {
		return dockPanelModel;
	}
	
	public ObservableList<DockTab> tabs() {
		return dockPanelModel.tabs();
	}
	
	@SuppressWarnings("unused")
	public Rectangle headerRegion() {
		Bounds tBounds = ((TabPaneSkin)dockArea.skinProperty().<TabPaneSkin>get()).getSelectedTabContentRegion().getLayoutBounds();
		// The 29 is kind of a hack. I could not find a better way to find
		// the header width so I used scenic view and found that the value was
		// always 29.
		Rectangle retVal = new Rectangle(tBounds.getMinX(), tBounds.getMinY(), tBounds.getWidth(), 29);
		return retVal;
	}
	
	@Override
	public void onChanged(Change<? extends DockTab> c) {
		if (!c.next()) return;
		
		final List<? extends DockTab> removedItems = c.getRemoved();
		final List<? extends DockTab> addedItems = c.getList();
		synchronized(tabLock) {
			if (removedItems.size() > 0) {
				LogRunner.logger().info(String.format("Removing children from %s\n", dockPanelModel.id()));
				dockArea.getTabs().removeAll(removedItems);
			}
			if (addedItems.size() > 0) {
				for (int i = 0; i < addedItems.size(); i++) {
					Tab item = addedItems.get(i);
					if (dockArea.getTabs().size() - 1 < i) {
						if (dockArea.getTabs().contains(item)) {
							dockArea.getTabs().remove(item);
						}
						
						LogRunner.logger().info(String.format("Adding children to %s\n", dockPanelModel.id()));
						dockArea.getTabs().add(item);
						continue;
					}
					
					if (dockArea.getTabs().get(i) != item) {
						if (dockArea.getTabs().contains(item)) {
							dockArea.getTabs().remove(item);
						}
						
						LogRunner.logger().info(String.format("Adding children to %s\n", dockPanelModel.id()));
						dockArea.getTabs().add(i, item);
					}
				}
			}
		}	
	}
	
	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}
}
