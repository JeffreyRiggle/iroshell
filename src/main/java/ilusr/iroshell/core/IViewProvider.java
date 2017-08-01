package ilusr.iroshell.core;

import javafx.scene.Node;

/**
 * 
 * @author Jeff Riggle
 *
 * @param <T> The type of view that is being provided.
 */
public interface IViewProvider<T extends Node> {
	/**
	 * 
	 * @return The view.
	 */
	T getView();
}
