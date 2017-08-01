package ilusr.iroshell.core;

import java.util.logging.Level;

import ilusr.core.url.InternalURLProvider;
import ilusr.logrunner.LogRunner;
import javafx.scene.Parent;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class StyleUpdater {

	private final String sourceName;
	private final Parent parent;
	private final InternalURLProvider provider;
	private String resource;
	private int count;
	
	/**
	 * 
	 * @param provider A @see InternalURLProvider to use.
	 * @param sourceName The name of the source file.
	 * @param parent A @See Parent to apply styles to.
	 */
	public StyleUpdater(InternalURLProvider provider, String sourceName, Parent parent) {
		this.provider = provider;
		this.sourceName = sourceName;
		this.parent = parent;
		this.count = 0;
		
		resource = provider.prepareURL(new String(), sourceName);
	}
	
	/**
	 * 
	 * @param style The data to associate with the URL.
	 */
	public void update(String style) {
		provider.removeURL(resource);
		resource = provider.prepareURL(style, String.format("%s%s.css", sourceName, count++));
		
		LogRunner.logger().log(Level.INFO, String.format("Appling style to %s", resource));
		StyleHack.applyStyle(resource, parent);
	}
}
