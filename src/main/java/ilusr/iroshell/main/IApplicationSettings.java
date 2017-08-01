package ilusr.iroshell.main;

import java.util.List;

import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.features.ExceptionOptions;
import ilusr.iroshell.features.IPreApplicationScreen;
import ilusr.iroshell.features.PersistenceOptions;
import ilusr.iroshell.features.SplashScreenOptions;
import javafx.scene.image.Image;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface IApplicationSettings {
	/**
	 * 
	 * @param name The new name of the application.
	 */
	void applicationName(String name);
	/**
	 * 
	 * @return The name of the application.
	 */
	String applicationName();
	/**
	 * 
	 * @return The version associated with the application.
	 */
	String version();
	/**
	 * 
	 * @param version The version associated with the application.
	 */
	void version(String version);
	/**
	 * 
	 * @param icon A @see Image to display in the title bar for this application.
	 */
	void applicationIcon(Image icon);
	/**
	 * 
	 * @return A @see Image to display in the title bar for this application.
	 */
	Image applicationIcon();
	/**
	 * 
	 * @param features The features this application is using.
	 */
	void applicationFeatures(int features);
	/**
	 * 
	 * @return The features this application is using.
	 */
	int applicationFeatures();
	/**
	 * 
	 * @return The @see DocumentType to use for this application.
	 */
	DocumentType getDocumentType();
	/**
	 * 
	 * @param type The @see DocumentType to use for this application.
	 */
	void setDocumentType(DocumentType type);
	/**
	 * 
	 * @return A list of @see IComponent to run in this application.
	 */
	List<IComponent> components();
	/**
	 * 
	 * @return A list of @see IPreApplicationScreen to run before the application is loaded.
	 */
	List<IPreApplicationScreen> preApplicationScreens();
	/**
	 * 
	 * @return The @see SplashScreenOptions associated with this application.
	 */
	SplashScreenOptions splashOptions();
	/**
	 * 
	 * @return The @see ExceptionOptions associated with this application.
	 */
	ExceptionOptions exceptionOptions();
	/**
	 * 
	 * @return The @see PersistenceOptions associated with this application.
	 */
	PersistenceOptions persistenceOptions();
}
