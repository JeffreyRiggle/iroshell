package ilusr.iroshell.toolbar;

import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import ilusr.core.io.StreamUtilities;
import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.LocalDragboard;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.IToolBarService;
import ilusr.iroshell.services.RegistrationType;
import ilusr.iroshell.services.StyleContainerService;
import ilusr.logrunner.LogRunner;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ToolingArea extends AnchorPane implements Initializable, ListChangeListener<DraggableToolBar>, IStyleWatcher {

	private final InternalURLProvider urlProvider;
	private final StyleContainerService styleService;
	private StyleUpdater styleUpdater;
	
	private ToolingAreaModel model;
	private IToolBarService toolBarService;
	
	private Pane pane;
	private Rectangle previewPane;
	private int dropIndex;
	
	/**
	 * Creates a tooling area.
	 */
	public ToolingArea() {
		this(new ToolingAreaModel());
	}
	
	/**
	 * 
	 * @param model A @see ToolingAreaModel.
	 */
	public ToolingArea(ToolingAreaModel model) {
		toolBarService = ServiceManager.getInstance().<IToolBarService>get("ToolBarService");
		urlProvider = ServiceManager.getInstance().<InternalURLProvider>get("InternalURLProvider");
		styleService = ServiceManager.getInstance().<StyleContainerService>get("IStyleContainerService");
		this.model = model;
		
		FXMLLoader toolingAreaLoader = new FXMLLoader(getClass().getResource("ToolingArea.fxml"));
		
		toolingAreaLoader.setRoot(this);
		toolingAreaLoader.setController(this);
		
		try {
			toolingAreaLoader.load();
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
	}

	/**
	 * 
	 * @return The @see ToolingAreaModel associated with this tooling area.
	 */
	public ToolingAreaModel model() {
		return model;
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		previewPane = new Rectangle();
		previewPane.setId("previewPane");
		
		changePosition(model.getPosition().get());
		
		model.getPosition().addListener((l, o , n) -> {
			changePosition(n);
		});
		
		model.toolBars().addListener(this);
		styleUpdater = new StyleUpdater(urlProvider, "toolareastylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.TOOLAREA), this, false);
		
		try {
			String content = StreamUtilities.getStreamContents(getClass().getResourceAsStream("toolarea.css"));
			styleService.register(StyleArea.TOOLAREA, content, RegistrationType.AvoidConflict);
			styleService.register(StyleArea.TOOLDRAGCOLOR, "-fx-background-color:rgba(0,255,0,0.5)", RegistrationType.AvoidConflict);
			styleService.register(StyleArea.TOOLRESTCOLOR, "", RegistrationType.AvoidConflict);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void changePosition(DockPosition position) {
		if (position == DockPosition.Left || position == DockPosition.Right) {
			pane = new HBox();
			pane.setMinWidth(5);
		} else {
			pane = new VBox();
			pane.setMinHeight(5);
		}
		
		AnchorPane.setBottomAnchor(pane, 0.0);
		AnchorPane.setTopAnchor(pane, 0.0);
		AnchorPane.setLeftAnchor(pane, 0.0);
		AnchorPane.setRightAnchor(pane, 0.0);
		
		super.getChildren().add(pane);
		setupDragListeners();
	}
	
	private void setupDragListeners() {
		pane.onDragOverProperty().set((e) -> {
			if (!LocalDragboard.getInstance().hasType(DraggableToolBar.class)) {
				return;
	        }
			
	        e.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	        
	        DockPosition position = model.getPosition().get();
	        DraggableToolBar dtb = LocalDragboard.getInstance().getValue(DraggableToolBar.class);
	        
	        if (pane.getChildren().contains(previewPane)) {
		        int newIndex = findToolingIndex(e, position);
		        
		        if (dropIndex != newIndex) {
		        	dropIndex = newIndex;
		        	pane.getChildren().remove(previewPane);
				    pane.getChildren().add(dropIndex, previewPane);
		        }
		        
	        	return;
	        }
	        
	        if (position == DockPosition.Left || position == DockPosition.Right) {
	        	previewPane.setWidth(dtb.toolBar().getWidth());
	        	previewPane.setHeight(pane.getHeight());
	        } else {
	        	previewPane.setHeight(dtb.toolBar().getHeight());
	        	previewPane.setWidth(pane.getWidth());
	        }
			
	        dropIndex = findToolingIndex(e, position);
		    pane.getChildren().add(dropIndex, previewPane);
	        
	        super.styleProperty().set(styleService.get(StyleArea.TOOLDRAGCOLOR));
	        super.applyCss();
		});
		
		pane.onDragExitedProperty().set((e) -> {
	        if (pane.getChildren().contains(previewPane)) {
				pane.getChildren().remove(previewPane);
	        }

			super.styleProperty().set(styleService.get(StyleArea.TOOLRESTCOLOR));
	        super.applyCss();
		});
		
		pane.onDragDroppedProperty().set((e) -> {
			if (!LocalDragboard.getInstance().hasType(DraggableToolBar.class)) {
				return;
	        }
			
			if (pane.getChildren().contains(previewPane)) {
				pane.getChildren().remove(previewPane);
	        }
			
			DraggableToolBar tBar = LocalDragboard.getInstance().getValue(DraggableToolBar.class);
			toolBarService.removeToolBar(tBar.toolBar());
			toolBarService.addToolBar(tBar.toolBar(), LocationProvider.index(dropIndex), model.getPosition().get());
			
			e.setDropCompleted(true);
		});
	}
	
	private int findToolingIndex(DragEvent e, DockPosition position) {
        int retVal = 0;
        double remaining = 0;
        
        switch (position) {
        	case Left:
        	case Right:
        		remaining = e.getX();
            	for (Node n : pane.getChildren()) {
            		if (remaining < n.boundsInParentProperty().get().getWidth()) {
            			break;
            		}
            		
            		remaining -= n.boundsInParentProperty().get().getWidth();
            		retVal++;
            	}
        		break;
        	case Top:
        	case Bottom:
        		remaining = e.getY();
            	for (Node n : pane.getChildren()) {
            		if (remaining < n.boundsInParentProperty().get().getHeight()) {
            			break;
            		}
            		
            		remaining -= n.boundsInParentProperty().get().getHeight();
            		retVal++;
            	}
        		break;
        }
        
		return retVal;
	}
	
	@Override
	public void onChanged(Change<? extends DraggableToolBar> c) {
		if (!c.next()) return;
		
		final List<? extends DraggableToolBar> removedItems = c.getRemoved();
		final List<? extends DraggableToolBar> addedItems = c.getList();
		synchronized(this) {
			if (removedItems.size() > 0) {
				pane.getChildren().removeAll(removedItems);
			}
			if (addedItems.size() > 0) {
				for (int i = 0; i < addedItems.size(); i++) {
					DraggableToolBar dtb = addedItems.get(i);
					if (pane.getChildren().size() - 1 < i) {
						if (pane.getChildren().contains(dtb)) {
							pane.getChildren().remove(dtb);
						}
						pane.getChildren().add(dtb);
						continue;
					}
					
					if (pane.getChildren().get(i) != dtb) {
						if (pane.getChildren().contains(dtb)) {
							pane.getChildren().remove(dtb);
						}
						
						pane.getChildren().add(i, dtb);
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
