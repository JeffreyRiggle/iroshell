package ilusr.iroshell.toolbar;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

import ilusr.core.io.FileUtilities;
import ilusr.core.javafx.LocalDragboard;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.StyleArea;
import ilusr.iroshell.core.StyleUpdater;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.RegistrationType;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.scene.control.ToolBar;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
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
public class DraggableToolBar extends AnchorPane implements Initializable, IStyleWatcher {

	private final InternalURLProvider urlProvider;
	private final IStyleContainerService styleService;
	private StyleUpdater styleUpdater;
	
	private final ToolBar toolBar;
	private Rectangle dragArea;
	private Pane box;
	private SimpleBooleanProperty draggable;
	
	/**
	 * 
	 * @param toolBar A @see ToolBar.
	 * @param canDrag If the toolbar can be dragged.
	 * @param urlProvider A @see InternalURLProvider.
	 * @param styleService A @see IStyleContainerService.
	 */
	public DraggableToolBar(ToolBar toolBar, boolean canDrag, InternalURLProvider urlProvider, IStyleContainerService styleService) {
		draggable = new SimpleBooleanProperty(canDrag);
		this.toolBar = toolBar;
		this.urlProvider = urlProvider;
		this.styleService = styleService;

		FXMLLoader loader = new FXMLLoader(getClass().getResource("DraggableToolBar.fxml"));
		
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return If this toolbar can be dragged.
	 */
	public SimpleBooleanProperty draggableProperty() {
		return draggable;
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		draggable.addListener((l, o, n) -> {
			if (o == n) {
				return;
			}
			
			if (n) {
				setupDrag();
				return;
			}
			
			tearDownDrag();
		});
		
		dragArea = new Rectangle();
		dragArea.setId("dragArea");
		
		if (toolBar.getOrientation() == Orientation.VERTICAL) {
			box = new VBox();
		} else {
			box = new HBox();
		}

		AnchorPane.setBottomAnchor(box, 0.0);
		AnchorPane.setTopAnchor(box, 0.0);
		AnchorPane.setLeftAnchor(box, 0.0);
		AnchorPane.setRightAnchor(box, 0.0);
		
		super.getChildren().add(box);
		box.getChildren().add(toolBar);
		
		if (!draggable.get()) {
			return;
		}
		
		setupDrag();
		setupStyling();
	}
	
	private void setupDrag() {
		if (toolBar.getOrientation() == Orientation.VERTICAL) {
			dragArea.setHeight(5);
			dragArea.widthProperty().bind(box.widthProperty());
		} else {
			dragArea.setWidth(5);
			dragArea.heightProperty().bind(box.heightProperty());
		}
		
		box.getChildren().add(0, dragArea);
		
		dragArea.setOnDragDetected((event) -> {
			Dragboard db = dragArea.startDragAndDrop(TransferMode.ANY);
	        
			//TODO Remove for hack?
	        ClipboardContent clipboardContent = new ClipboardContent();
	        clipboardContent.putString("Testing");
	        db.setContent(clipboardContent);
	        LocalDragboard.getInstance().putValue(DraggableToolBar.class, this);
	        
	        event.consume();
		});
	}
	
	private void setupStyling() {
		styleUpdater = new StyleUpdater(urlProvider, "dtbstylesheet.css", this);
		styleService.startWatchStyle(Arrays.asList(StyleArea.DRAGGABLETOOLBAR), this, false);
		
		try {
			File cssFile = FileUtilities.getResourceFile(this.getClass(), "dtb.css");
			styleService.register(StyleArea.DRAGGABLETOOLBAR, cssFile, RegistrationType.AvoidConflict);	
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void tearDownDrag() {
		box.getChildren().remove(dragArea);
		dragArea.setOnDragDetected(null);
	}
	
	/**
	 * 
	 * @return The @see ToolBar.
	 */
	public ToolBar toolBar() {
		return toolBar;
	}

	@Override
	public void update(String style, String css) {
		styleUpdater.update(css);
	}
}
