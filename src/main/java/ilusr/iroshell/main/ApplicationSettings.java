package ilusr.iroshell.main;

import java.util.ArrayList;
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
public class ApplicationSettings implements IApplicationSettings {
	
	private String applicationName;
	private String version;
	private Image applicationIcon;
	private List<IComponent> components;
	private List<IPreApplicationScreen> preApplicationScreens;
	private int applicationFeatures;
	private SplashScreenOptions splashScreenOptions;
	private ExceptionOptions exceptionOptions;
	private PersistenceOptions persistenceOptions;
	private DocumentType documentType;
	
	/**
	 * Base ctor.
	 */
	public ApplicationSettings() {
		applicationName = new String();
		version = new String();
		components = new ArrayList<IComponent>();
		preApplicationScreens = new ArrayList<IPreApplicationScreen>();
		splashScreenOptions = new SplashScreenOptions();
		exceptionOptions = new ExceptionOptions();
		persistenceOptions = new PersistenceOptions();
	}
	
	@Override
	public void applicationName(String name) {
		applicationName = name;
	}
	
	@Override
	public String applicationName() {
		return applicationName;
	}
	
	@Override
	public void version(String ver) {
		version = ver;
	}
	
	@Override
	public String version() {
		return version;
	}
	
	@Override
	public void applicationIcon(Image icon) {
		applicationIcon = icon;
	}
	
	@Override
	public Image applicationIcon() {
		return applicationIcon;
	}
	
	@Override
	public void applicationFeatures(int features) {
		applicationFeatures = features;
	}
	
	@Override
	public int applicationFeatures() {
		return applicationFeatures;
	}
	
	@Override
	public List<IComponent> components() {
		return components;
	}
	
	@Override
	public List<IPreApplicationScreen> preApplicationScreens() {
		return preApplicationScreens;
	}
	
	@Override
	public SplashScreenOptions splashOptions() {
		return splashScreenOptions;
	}
	
	@Override
	public ExceptionOptions exceptionOptions() {
		return exceptionOptions;
	}
	
	@Override
	public PersistenceOptions persistenceOptions() {
		return persistenceOptions;
	}
	
	@Override
	public DocumentType getDocumentType() {
		return documentType;
	}
	
	@Override
	public void setDocumentType(DocumentType type) {
		documentType = type;
	}
}