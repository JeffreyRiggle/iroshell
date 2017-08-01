package ilusr.iroshell.services;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IStyleWatcher {
	/**
	 * 
	 * @param style The name of the style to update.
	 * @param css The css to apply to the style.
	 */
	void update(String style, String css);
}
