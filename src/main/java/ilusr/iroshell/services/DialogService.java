package ilusr.iroshell.services;

import java.util.logging.Level;

import ilusr.logrunner.LogRunner;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class DialogService implements IDialogService {

	private final Window window;
	private final IOverlayProvider overlayProvider;
	private final Image applicationIcon;
	
	/**
	 * 
	 * @param window The @see Window to parent dialogs to.
	 * @param overlayProvider A @see IOverlayProvider to use for embedded dialogs.
	 * @param applicationIcon A @see Image to display on created stages.
	 */
	public DialogService(Window window, IOverlayProvider overlayProvider, Image applicationIcon) {
		this.window = window;
		this.overlayProvider = overlayProvider;
		this.applicationIcon = applicationIcon;
	}

	@Override
	public void displayModal(Stage stage) {
		if (Platform.isFxApplicationThread()) {
			displayModalImpl(stage);
			return;
		}
		
		Platform.runLater(() -> {
			displayModalImpl(stage);
		});
	}
	
	private void displayModalImpl(Stage stage) {
		LogRunner.logger().log(Level.INFO, "Displaying a modal overlay");
		stage.initOwner(window);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	
	@Override
	public void displayModal(Scene scene) {
		displayModal(scene, new String());
	}

	@Override
	public void displayModal(Scene scene, String title) {
		if (Platform.isFxApplicationThread()) {
			displayModalImpl(scene, title);
			return;
		}
		
		Platform.runLater(() -> {
			displayModalImpl(scene, title);
		});
	}
	
	private void displayModalImpl(Scene scene, String title) {
		LogRunner.logger().log(Level.INFO, "Displaying a modal overlay");
		Stage stage = createStage(scene, title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	
	@Override
	public void displayModal(Parent root) {
		displayModal(root, new String());
	}
	
	@Override
	public void displayModal(Parent root, String title) {
		if (Platform.isFxApplicationThread()) {
			displayModalImpl(root, title);
			return;
		}
		
		Platform.runLater(() -> {
			displayModalImpl(root, title);
		});
	}
	
	private void displayModalImpl(Parent root, String title) {
		LogRunner.logger().log(Level.INFO, "Displaying a modal overlay");
		Stage stage = createStage(new Scene(root), title);
		stage.initModality(Modality.APPLICATION_MODAL);
		stage.showAndWait();
	}
	
	@Override
	public void displayModeless(Stage stage) {
		if (Platform.isFxApplicationThread()) {
			displayModelessImpl(stage);
			return;
		}
		
		Platform.runLater(() -> {
			displayModelessImpl(stage);
		});
	}
	
	private void displayModelessImpl(Stage stage) {
		LogRunner.logger().log(Level.INFO, "Displaying a modeless overlay");
		stage.initOwner(window);
		stage.show();
	}
	
	@Override
	public void displayModeless(Scene scene) {
		displayModeless(scene, new String());
	}

	@Override
	public void displayModeless(Scene scene, String title) {
		LogRunner.logger().log(Level.INFO, "Displaying a modeless overlay");
		if (Platform.isFxApplicationThread()) {
			createStage(scene, title).show();
			return;
		}

		Platform.runLater(() -> {
			createStage(scene, title).show();
		});
	}
	
	@Override
	public void displayModeless(Parent root) {
		displayModeless(root, new String());
	}
	
	@Override
	public void displayModeless(Parent root, String title) {
		LogRunner.logger().log(Level.INFO, "Displaying a modeless overlay");
		if (Platform.isFxApplicationThread()) {
			createStage(new Scene(root), title).show();
			return;
		}

		Platform.runLater(() -> {
			createStage(new Scene(root), title).show();
		});
	}
	
	@Override
	public void displayEmbedded(Node node) {
		if (Platform.isFxApplicationThread()) {
			overlayProvider.overlay(node);
			return;
		}

		Platform.runLater(() -> {
			overlayProvider.overlay(node);
		});
	}
	
	@Override
	public void clearEmbedded(Node node) {
		if (Platform.isFxApplicationThread()) {
			overlayProvider.clearOverlay(node);
			return;
		}

		Platform.runLater(() -> {
			overlayProvider.clearOverlay(node);
		});
	}
	
	private Stage createStage(Scene scene, String title) {
		Stage stage = new Stage();
		stage.setScene(scene);
		stage.setTitle(title);
		
		if (applicationIcon != null) {
			stage.getIcons().add(applicationIcon);
		}
		
		stage.initOwner(window);
		return stage;
	}
}
