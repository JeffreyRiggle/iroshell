package ilusr.iroshell.documentinterfaces.sdi;

import java.net.URL;
import java.util.ResourceBundle;

import ilusr.logrunner.LogRunner;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class SingleDocumentInterface extends AnchorPane implements Initializable, ChangeListener<Node> {

	@FXML
	private SplitPane rootPane;
	
	@FXML
	private AnchorPane selector;
	
	@FXML
	private AnchorPane documentArea;
	
	private SDIModel model;
	private SelectorNode selectorPane;
	
	/**
	 * Base ctor.
	 */
	public SingleDocumentInterface() {
		model = new SDIModel();
		FXMLLoader loader = new FXMLLoader(getClass().getResource("SingleDocumentInterface.fxml"));
		loader.setRoot(this);
		loader.setController(this);
		
		try {
			loader.load();
		} catch (Exception exception) {
			LogRunner.logger().severe(exception);
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {		
		initializeListeners();
	}	
	
	private void initializeListeners() {
		if (model == null) return;

		//TODO: Fix this. Right now there is no way to disable the divider
		rootPane.lookupAll(".split-pane-divider").stream().forEach(div ->  div.setMouseTransparent(!model.canResize().getValue()));

		model.canResize().addListener((c, o, n) -> {
			Platform.runLater(() -> {
				rootPane.lookupAll(".split-pane-divider").stream().forEach(div ->  div.setMouseTransparent(!n));
			});
		});
		
		model.currentView().addListener(this);
	}

	private void uninitializeListeners() {
		if (model == null) return;
		
		model.canResize().removeListener((c) -> { });
		model.currentView().removeListener(this);
	}
	
	@Override
	public void changed(ObservableValue<? extends Node> observable, Node oldValue, Node newValue) {
		final Node newVal = newValue;
		Platform.runLater(() -> {
			//TODO: Thread safety?
			anchorNode(newValue);
			
			documentArea.getChildren().clear();
			documentArea.getChildren().add(newVal);
		});
	}
	
	/**
	 * 
	 * @return A @see SplitPane
	 */
	public SplitPane getSplitter() {
		return rootPane;
	}
	
	/**
	 * 
	 * @param selector The new @see SelectorNode to use.
	 */
	public void setSelectorPane(SelectorNode selector) {
		this.selector.getChildren().clear();
		anchorNode(selector);
		setModel(selector.getModel());
		selectorPane = selector;
		this.selector.getChildren().add(selector);
	}
	
	private void anchorNode(Node node) {
		AnchorPane.setBottomAnchor(node, 0.0);
		AnchorPane.setTopAnchor(node, 0.0);
		AnchorPane.setLeftAnchor(node, 0.0);
		AnchorPane.setRightAnchor(node, 0.0);
	}
	
	/**
	 * 
	 * @return The current @see SelectorNode.
	 */
	public SelectorNode getSelectorPane() {
		return selectorPane;
	}
	
	/**
	 * 
	 * @param model A new @see SDIModel to use.
	 */
	public void setModel(SDIModel model) {
		uninitializeListeners();
		this.model = model;
		initializeListeners();
	}
	
	/**
	 * 
	 * @return The current @see SDIModel.
	 */
	public SDIModel getModel() {
		return model;
	}

	//TODO: Fix this. Right now there is no way to disable the divider
	/**
	 * 
	 * @param canResize If the sizer can be resized.
	 */
	public void setCanResize(boolean canResize) {
		model.canResize().setValue(canResize);
	}
	
	/**
	 * 
	 * @return If the sizer can be resized.
	 */
	public boolean getCanResize() {
		return model.canResize().getValue();
	}
}
