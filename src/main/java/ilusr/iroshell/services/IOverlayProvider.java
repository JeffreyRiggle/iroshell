package ilusr.iroshell.services;

import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IOverlayProvider {
	/**
	 * Overlays a @see Node on the provider.
	 * @param node A @see Node to display.
	 */
	void overlay(Node node);
	/**
	 * Clears a @see Node from the overlay.
	 * @param node The @see Node to clear.
	 */
	void clearOverlay(Node node);
}
