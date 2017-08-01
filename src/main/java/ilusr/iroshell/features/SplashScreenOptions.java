package ilusr.iroshell.features;

import javafx.scene.Parent;
import javafx.scene.image.Image;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class SplashScreenOptions {

	private String font;
	private Image backgroundImage;
	private String backgroundColor;
	private ISplashScreenCreator creator;
	
	/**
	 * Base ctor.
	 */
	public SplashScreenOptions() {
		
	}
	
	/**
	 * 
	 * @param font The name of the font to use.
	 */
	public void fontFamily(String font) {
		this.font = font;
	}
	
	/**
	 * 
	 * @return The name of the font to use.
	 */
	public String fontFamily() {
		return font;
	}
	
	/**
	 * 
	 * @param imageLocation A path to an image.
	 */
	public void backgroundImage(Image image) {
		backgroundImage = image;
	}
	
	/**
	 * 
	 * @param color A string representing a color to be used.
	 */
	public void backgroundColor(String color) {
		backgroundColor = color;
	}
	
	/**
	 * 
	 * @return A string representing a color to be used.
	 */
	public String backgroundColor() {
		return backgroundColor;
	}
	
	/**
	 * 
	 * @return A path to an image to be used.
	 */
	public Image backgroundImage() {
		return backgroundImage;
	}
	
	/**
	 * 
	 * @param creator A @see ISplashScreenCreator to be used to create splash screens.
	 */
	public void customSplashScreenCreator(ISplashScreenCreator creator) {
		this.creator = creator;
	}
	
	/**
	 * 
	 * @return The splash screen to show.
	 */
	public Parent customSplashScreen() {
		if (creator == null) return null;
		
		return creator.create();
	}
}
