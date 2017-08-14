package ilusr.iroshell.dockarea.overlay;

import java.net.URL;
import java.util.ResourceBundle;

import ilusr.core.ioc.ServiceManager;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.logrunner.LogRunner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.input.DragEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class DockArrow extends VBox implements Initializable, IStyleListener {

	@FXML
	private Rectangle arrow;

	private DockAreaModel model;
	
	public DockArrow() {
		this(new DockAreaModel(ServiceManager.getInstance().<IStyleContainerService>get("IStyleContainerService")));
	}
	
	public DockArrow(DockAreaModel model) {
		this.model = model;
		
		FXMLLoader arrowLoader = new FXMLLoader(getClass().getResource("dockarrow.fxml"));
		arrowLoader.setRoot(this);
		arrowLoader.setController(this);
		
		try {
			arrowLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		model.addListener(this);
		applyStyle(model.arrowStyle());
		arrow.setOnDragOver((event) -> dragEntered(event));
		arrow.setOnDragExited((event) -> dragExited(event));
		arrow.setOnDragDropped((event -> dragDropped(event)));
	}

	@FXML
	private void dragEntered(DragEvent event) {
		applyStyle(model.hoverArrowStyle());
		model.invoke(DraggableEvent.Entered);
		event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
	}
	
	@FXML
	private void dragExited(DragEvent event) {
		applyStyle(model.arrowStyle());
		model.invoke(DraggableEvent.Exited);
	}
	
	@FXML
	private void dragDropped(DragEvent event) {
		applyStyle(model.arrowStyle());
		model.invoke(DraggableEvent.Dropped);
		event.setDropCompleted(true);
	}
	
	private void applyStyle(String style) {
		arrow.styleProperty().set(style);
		arrow.applyCss();
	}
	
	//TODO: I really don't like this.
	public DockAreaModel model() {
		return model;
	}

	@Override
	public void changed() {
		applyStyle(model.arrowStyle());
	}
}
