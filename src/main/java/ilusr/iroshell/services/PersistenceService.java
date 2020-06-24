package ilusr.iroshell.services;

import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.core.datamanager.xml.XmlManager;
import ilusr.core.io.XMLDocumentUtilities;
import ilusr.iroshell.core.ApplicationClosingListener;
import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.persistence.ApplicationShellPersistence;
import ilusr.iroshell.persistence.MDIPersistence;
import ilusr.iroshell.persistence.SDIPersistence;
import ilusr.iroshell.persistence.ToolBarAreaPersistence;
import ilusr.logrunner.LogRunner;
import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationManager;
import ilusr.persistencelib.configuration.XmlConfigurationObject;
import javafx.stage.Stage;

//TODO: This needs an interface
/**
 * 
 * @author Jeff Riggle
 *
 */
public class PersistenceService implements ApplicationClosingListener{

	private final String SHELL_CONFIGURATION = "ShellConfiguration";
	private final Stage shellStage;
	private final PersistableToolBarService toolBarService;
	private final ILayoutService layoutService;
	private ApplicationShellPersistence shellPersistence;
	private XmlManager manager;
	private XmlConfigurationManager configurationManager;
	private XmlConfigurationObject shellConfiguration;
	
	/**
	 * 
	 * @param shellStage The Application @see Stage.
	 * @param saveLocation The location to save persistence to.
	 * @param closingManager A @see IApplicationClosingManager.
	 * @param toolBarService A @see PersistableToolBarService.
	 * @param layoutService A @see ILayoutService.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public PersistenceService(Stage shellStage, 
							  String saveLocation, 
							  IApplicationClosingManager closingManager,
							  PersistableToolBarService toolBarService,
							  ILayoutService layoutService) throws TransformerConfigurationException, ParserConfigurationException {
		this.shellStage = shellStage;
		this.toolBarService = toolBarService;
		this.layoutService = layoutService;
		shellPersistence = new ApplicationShellPersistence();
		shellConfiguration = new XmlConfigurationObject(SHELL_CONFIGURATION);
		
		initializeConfigurationManager(saveLocation);
		closingManager.addApplicationClosingListener(this);
	}
	
	private void initializeConfigurationManager(String saveLocation) {
		try {
			manager = new XmlManager(saveLocation);
			configurationManager = new XmlConfigurationManager(manager, new ArrayList<PersistXml>());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return The current save location.
	 */
	public String getSaveLocation() {
		return manager.saveLocation();
	}
	
	/**
	 * 
	 * @param location The new save location to use.
	 */
	public void setSaveLocation(String location) {
		manager.saveLocation(location);
	}
	
	/**
	 * Save the application state to the save location.
	 */
	public void savePersistence() {
		if (manager.saveLocation().equals(new String())) {
			LogRunner.logger().warning("Not saving persistence due to invalid save path.");
			return;
		}

		LogRunner.logger().info("Configuring application state.");
		saveShell();
		toolBarService.savePersistence();
		layoutService.savePersistence();
		
		configurationManager.clearConfigurationObjects();
		shellConfiguration.clearChildren();
		shellConfiguration.addChild(shellPersistence);
		shellConfiguration.addChild(toolBarService.getPersistence());
		
		try {
			if (layoutService.documentType().get() == DocumentType.MDI) {
				shellConfiguration.addChild(layoutService.getMDIPersistenceModel());
			} else {
				shellConfiguration.addChild(layoutService.getSDIPersistenceModel());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		configurationManager.addConfigurationObject(shellConfiguration);
		
		try {
			LogRunner.logger().info(String.format("Saving application state to: %s", manager.saveLocation()));
			configurationManager.save();
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
	}
	
	private void saveShell() {
		shellPersistence.setX(shellStage.getX());
		shellPersistence.setY(shellStage.getY());
		shellPersistence.setWidth(shellStage.getWidth());
		shellPersistence.setHeight(shellStage.getHeight());
	}
	
	private void loadShell() {
		shellStage.setX(shellPersistence.getX());
		shellStage.setY(shellPersistence.getY());
		shellStage.setWidth(shellPersistence.getWidth());
		shellStage.setHeight(shellPersistence.getHeight());
	}
	
	/**
	 * 
	 * @return The application state as a string.
	 */
	public String getLayoutString() {
		return XMLDocumentUtilities.convertDocumentToString(manager.document());
	}
	
	/**
	 * Loads the application state from the save location.
	 */
	public void loadPersistence() {
		if (manager.saveLocation().equals(new String())) {
			LogRunner.logger().warning("Not loading persistence due to invalid save path.");
			return;
		}
		
		try {
			LogRunner.logger().info(String.format("Loading persistence from: %s", manager.saveLocation()));
			configurationManager.load();
			shellConfiguration = (XmlConfigurationObject)configurationManager.configurationObjects().get(0);
			shellPersistence.convertFromPersistence((XmlConfigurationObject)shellConfiguration.children().get(0));
			loadShell();
			toolBarService.loadLayout(new ToolBarAreaPersistence((XmlConfigurationObject)shellConfiguration.children().get(1)));

			if (layoutService.documentType().get() == DocumentType.SDI) {
				layoutService.loadPersistenceModel(new SDIPersistence((XmlConfigurationObject)shellConfiguration.children().get(2)));
			} else {
				layoutService.loadPersistenceModel(new MDIPersistence((XmlConfigurationObject)shellConfiguration.children().get(2)));
			}
			
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
	}
	
	/**
	 * 
	 * @param layout The application state to load.
	 */
	public void loadPersistence(String layout) {
		//TODO: Do this.
	}
	
	/**
	 * 
	 * @return A @see ApplicationShellPersistence.
	 */
	public ApplicationShellPersistence getApplicationPersistence() {
		return shellPersistence;
	}

	@Override
	public void onClose() {
		savePersistence();
	}
}
