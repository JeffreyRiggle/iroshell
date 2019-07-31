package ilusr.iroshell.dockarea;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.Tooltip;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import ilusr.core.javafx.LocalDragboard;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.logrunner.LogRunner;

@SuppressWarnings("restriction")
public class DockTab extends Tab implements Initializable, ICloseable, ISelectable, IStyleWatcher {
	
	private TabModel model;

	//TODO: Should this be in the model?
	private final IStyleContainerService styleService;
	private String tabDragColor;

	public DockTab(TabModel model, IStyleContainerService styleService) {
		this.model = model;
		tabDragColor = "-fx-background-color:rgba(0, 224, 179, .3);";
		
		this.styleService = styleService;

		FXMLLoader tabLoader = new FXMLLoader(getClass().getResource("docktab.fxml"));
		tabLoader.setRoot(this);
		tabLoader.setController(this);
		
		try {
			tabLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		super.tooltipProperty().set(new Tooltip());
		
		setupBindings();
		setupDragAndDrop();
		setupContextMenu();
		styleService.startWatchStyle(Arrays.asList(StyleArea.TAB), this, false);
	}
	
	private void setupBindings() {
		super.graphicProperty().bind(model.title());
		
		super.graphicProperty().addListener((c) -> {
			setupDragAndDrop();
		});
		
		super.contentProperty().bind(model.content());
		super.closableProperty().bind(model.canClose());
		super.tooltipProperty().get().textProperty().bind(model.toolTip());
		super.idProperty().set(model.id().toString());
		model.setSelectionListener(this);
		
		//Hide the tooltip if there is no tooltip text.
		model.toolTip().addListener((c) -> {
			if (model.toolTip().get().isEmpty()) {
				super.tooltipProperty().get().setAutoHide(true);
				return;
			} 
			
			super.tooltipProperty().get().setAutoHide(false);
		});
		
		model.addCloseListener(this);
		super.onClosedProperty().set((event) -> {
			model.close();
		});
	}
	
	@Override
	public void close() {
		System.out.println("final close");
		if (tabPaneProperty().get() == null) {
			System.out.println("Tab pane is null");
			return;
		}
		
		Event.fireEvent(this, new Event(Tab.TAB_CLOSE_REQUEST_EVENT));
	}
	
	private void setupDragAndDrop() {
		super.graphicProperty().get().setOnDragDetected((event) -> {
	        startDrag(event);
	    });
		super.graphicProperty().get().setOnDragDone((event) -> {
			stopDrag(event);
		});
	}
	
	private void setupContextMenu() {
		super.contextMenuProperty().set(new ContextMenu());
		
		super.graphicProperty().get().setOnMousePressed((e) -> {
			if (e.isSecondaryButtonDown()) {
				super.contextMenuProperty().get().show(super.getTabPane(), e.getSceneX(), e.getSceneY());;
			}
		});
		
		super.contextMenuProperty().get().getItems().addAll(model.contextMenuItems());
		
		model.contextMenuItems().addListener((ListChangeListener.Change<? extends MenuItem>c) -> {
			super.contextMenuProperty().get().getItems().clear();
			super.contextMenuProperty().get().getItems().addAll(c.getList());
		});
	}
	
	private void startDrag(MouseEvent event) {
		Dragboard db = super.graphicProperty().get().startDragAndDrop(TransferMode.ANY);
        
		//TODO Remove for hack?
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(super.graphicProperty().getName());
        db.setContent(clipboardContent);
        LocalDragboard.getInstance().putValue(DockTab.class, this);
        
        //TODO: Make this an overrideable value.
        this.styleProperty().set(tabDragColor);
        event.consume();
	}
	
	private void stopDrag(DragEvent event) {
		this.styleProperty().set("");
		LocalDragboard.getInstance().clear(DockTab.class);
		//TODO: Remove from dragboard?
	}

	@Override
	public void select() {
		Platform.runLater(() -> {
			if (super.tabPaneProperty().get() == null) {
				return;
			}
			
			super.tabPaneProperty().get().selectionModelProperty().getValue().select(this);
		});
	}

	@Override
	public void deSelect() {
		//TODO: What do?
	}
	
	@Override
	public void update(String style, String css) {
		tabDragColor = css;
	}
}
