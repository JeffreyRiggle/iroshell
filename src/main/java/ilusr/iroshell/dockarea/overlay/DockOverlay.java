package ilusr.iroshell.dockarea.overlay;

import java.net.URL;
import java.util.ResourceBundle;

import ilusr.core.interfaces.IDispose;
import ilusr.core.ioc.ServiceManager;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.logrunner.LogRunner;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;

public class DockOverlay extends AnchorPane implements Initializable, IDragListener, IDispose {

	@FXML
	private BorderPane dockBorder;

	@FXML
	private DockArrow topArrow;
	
	@FXML
	private DockArrow bottomArrow;
	
	@FXML
	private DockArrow leftArrow;
	
	@FXML
	private DockArrow rightArrow;
	
	@FXML
	private DockArrow centerArrow;
	
	@FXML
	private DockOverlayModel model;
	
	public DockOverlay() {
		this(new DockOverlayModel(ServiceManager.getInstance().<IStyleContainerService>get("IStyleContainerService")));
	}
	
	public DockOverlay(DockOverlayModel model) {
		this.model = model;
		
		FXMLLoader overlayLoader = new FXMLLoader(getClass().getResource("dockoverlay.fxml"));
		overlayLoader.setRoot(this);
		overlayLoader.setController(this);
		
		try {
			overlayLoader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		topArrow.model().setPosition(DockPosition.Top);
		bottomArrow.model().setPosition(DockPosition.Bottom);
		rightArrow.model().setPosition(DockPosition.Right);
		leftArrow.model().setPosition(DockPosition.Left);
		centerArrow.model().setPosition(DockPosition.Center);
		
		topArrow.setAlignment(Pos.TOP_CENTER);
		bottomArrow.setAlignment(Pos.BOTTOM_CENTER);
		leftArrow.setAlignment(Pos.CENTER_LEFT);
		rightArrow.setAlignment(Pos.CENTER_RIGHT);
		centerArrow.setAlignment(Pos.CENTER);
		
		registerEvents();
	}

	//TODO: I really don't like this.
	public DockOverlayModel model() {
		return model;
	}
	
	private void registerEvents() {
		rightArrow.model().addListener(this);
		leftArrow.model().addListener(this);
		topArrow.model().addListener(this);
		bottomArrow.model().addListener(this);
		centerArrow.model().addListener(this);
	}
	
	private void mouseEntered(DockPosition pos) {
		switch (pos) {
			case Left:
				leftArrow.setPrefWidth(dockBorder.getWidth()/2);
				applyEffect(leftArrow);
				break;
			case Right:
				rightArrow.setPrefWidth(dockBorder.getWidth()/2);
				applyEffect(rightArrow);
				break;
			case Top:
				topArrow.setPrefHeight(dockBorder.getHeight()/2);
				applyEffect(topArrow);
				break;
			case Bottom:
				bottomArrow.setPrefHeight(dockBorder.getHeight()/2);
				applyEffect(bottomArrow);
				break;
			case Center:
				centerArrow.setPrefHeight(dockBorder.getHeight());
				centerArrow.setPrefWidth(dockBorder.getWidth());
				applyEffect(centerArrow);
				break;
		}
	}
	
	private void mouseExited(DockPosition pos) {
		switch (pos) {
			case Left:
				leftArrow.setPrefWidth(25);
				removeEffect(leftArrow);
				break;
			case Right:
				rightArrow.setPrefWidth(25);
				removeEffect(rightArrow);
				break;
			case Top:
				topArrow.setPrefHeight(25);
				removeEffect(topArrow);
				break;
			case Bottom:
				bottomArrow.setPrefHeight(25);
				removeEffect(bottomArrow);
				break;
			case Center:
				centerArrow.setPrefHeight(25);
				centerArrow.setPrefWidth(25);
				removeEffect(centerArrow);
				break;
		}
	}
	
	private void applyEffect(DockArrow arrow) {
		arrow.styleProperty().set(model.previewStyle());
		arrow.applyCss();
	}
	
	private void removeEffect(DockArrow arrow) {
		arrow.styleProperty().set("");
		arrow.applyCss();
	}
	
	@Override
	public void dragEntered(DockPosition pos) {
		mouseEntered(pos);
	}
	
	@Override
	public void dragDropped(DockPosition pos) {
		model.invoke(pos);
	}

	@Override
	public void dragExited(DockPosition pos) {
		mouseExited(pos);
	}
	
	@Override
	public void dispose() {
		rightArrow.model().removeListener(this);
		leftArrow.model().removeListener(this);
		topArrow.model().removeListener(this);
		bottomArrow.model().removeListener(this);
		centerArrow.model().removeListener(this);
	}
}
