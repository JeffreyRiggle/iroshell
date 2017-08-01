package ilusr.iroshell.core;

import javafx.scene.Parent;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class StyleHack {

	private static final Object styleLock = new Object();
	
	/**
	 * 
	 * @param res A CSS URL to apply
	 * @param node The @see Parent to apply the URL to.
	 */
	public static void applyStyle(String res, Parent node) {
		synchronized(styleLock) {
			node.getStylesheets().setAll(res);
		}
	}
}
