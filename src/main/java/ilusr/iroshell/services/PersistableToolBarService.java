package ilusr.iroshell.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.core.data.Tuple;
import ilusr.core.datamanager.xml.XmlGenerator;
import ilusr.core.datamanager.xml.XmlInputReader;
import ilusr.core.datamanager.xml.XmlManager;
import ilusr.core.io.XMLDocumentUtilities;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.persistence.ToolBarAreaPersistence;
import ilusr.iroshell.persistence.ToolBarPersistence;
import ilusr.iroshell.toolbar.DraggableToolBar;
import ilusr.logrunner.LogRunner;
import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationManager;
import ilusr.persistencelib.configuration.XmlConfigurationObject;
import javafx.collections.ObservableList;
import javafx.scene.control.ToolBar;

//TODO test. Consider locking mechanism.
/**
 * 
 * @author Jeff Riggle
 *
 */
public class PersistableToolBarService implements IPersistableToolBarService{

	private final IToolBarService service;
	private final Map<String, IToolBarBluePrint> bluePrintMap;
	private final Map<String, Tuple<String, ToolBar>> persistableToolBars;
	private final List<Tuple<DockPosition, ToolBarPersistence>> deferredToolBars;
	private final Object bluePrintLock = new Object();
	private final Object layoutLock = new Object();
	
	private XmlManager manager;
	private XmlConfigurationManager configurationManager;
	private ToolBarAreaPersistence persistenceObject;
	
	/**
	 * 
	 * @param service A @see IToolBarService
	 */
	public PersistableToolBarService(IToolBarService service) {
		this(service, new HashMap<String, IToolBarBluePrint>(), new HashMap<String, Tuple<String, ToolBar>>(), new ArrayList<Tuple<DockPosition, ToolBarPersistence>>());
	}
	
	/**
	 * 
	 * @param service A @see IToolBarService
	 * @param bluePrintMap A Map of blueprint id to @see IToolBarBluePrint.
	 * @param persistableToolBars A Map of persisted id to toolbars.
	 * @param deferredToolBars A list of deferred toolbars.
	 */
	public PersistableToolBarService(IToolBarService service, 
									 Map<String, IToolBarBluePrint> bluePrintMap,
									 Map<String, Tuple<String, ToolBar>> persistableToolBars,
									 List<Tuple<DockPosition, ToolBarPersistence>> deferredToolBars) {
		this.service = service;
		
		this.bluePrintMap = bluePrintMap;
		this.persistableToolBars = persistableToolBars;
		this.deferredToolBars = deferredToolBars;
		
		try {
			persistenceObject = new ToolBarAreaPersistence();
			manager = new XmlManager(new String());
			configurationManager = new XmlConfigurationManager(manager, new ArrayList<PersistXml>());
		} catch (Exception e) {
			LogRunner.logger().log(Level.INFO, String.format("%s", e));
		}
	}
	
	@Override
	public void registerBluePrint(String bluePrintName, IToolBarBluePrint bluePrint) {
		synchronized(bluePrintLock) {
			LogRunner.logger().log(Level.INFO, String.format("Registering blue print: %s", bluePrintName));
			bluePrintMap.put(bluePrintName, bluePrint);
		}
		
		synchronized(layoutLock) {
			for (Tuple<DockPosition, ToolBarPersistence> entry : deferredToolBars) {
				persistOrDeferToolBar(entry.value(), entry.key());
			}
		}
	}
	
	@Override
	public void unregisterBluePrint(String bluePrintName) {
		synchronized(bluePrintLock) {
			LogRunner.logger().log(Level.INFO, String.format("Un-registering blue print: %s", bluePrintName));
			bluePrintMap.remove(bluePrintName);
		}
	}
	
	@Override
	public String addToolBar(String bluePrintName, LocationParameters parameters, DockPosition position) {
		return addToolBar(bluePrintName, parameters, position, true);
	}

	@Override
	public String addToolBar(String bluePrintName, LocationParameters parameters, DockPosition position, boolean canDrag) {
		return addToolBarImpl(bluePrintName, parameters, position, canDrag, UUID.randomUUID().toString());
	}

	private String addToolBarImpl(String bluePrintName, LocationParameters parameters, DockPosition position, boolean canDrag, String id) {
		IToolBarBluePrint bluePrint;
		synchronized(bluePrintLock) {
			bluePrint = bluePrintMap.get(bluePrintName);
		}
		
		if (bluePrint == null) {
			throw new IllegalArgumentException(String.format("Invalid BluePrintName: %s", bluePrintName));
		}
		
		LogRunner.logger().log(Level.INFO, String.format("Creating toolbar from blue print: %s", bluePrintName));
		ToolBar tb = bluePrint.create();
		synchronized(layoutLock) {
			service.addToolBar(tb, parameters, position, canDrag);
			persistableToolBars.put(id, new Tuple<String, ToolBar>(bluePrintName, tb));
			return id;
		}
	}
	
	@Override
	public ToolBarAreaPersistence getPersistence() {
		return persistenceObject;
	}
	
	@Override
	public String getLayout() {
		String retVal = new String();
		
		try {
			savePersistence();
			retVal = XMLDocumentUtilities.convertDocumentToString(manager.document());
		} catch (Exception e) {
			e.printStackTrace();
		}

		return retVal;
	}
	
	@Override
	public void savePersistence() {
		try {
			synchronized(layoutLock) {
				LogRunner.logger().log(Level.INFO, "Saving Persistence");
				configurationManager.clearConfigurationObjects();
				persistToolArea(service.leftToolBars(), DockPosition.Left);
				persistToolArea(service.topToolBars(), DockPosition.Top);
				persistToolArea(service.rightToolBars(), DockPosition.Right);
				persistToolArea(service.bottomToolBars(), DockPosition.Bottom);
				configurationManager.addConfigurationObject(persistenceObject);
				configurationManager.prepare();
			}
		} catch (Exception e) {
			LogRunner.logger().log(Level.INFO, String.format("%s", e));
		}
	}
	
	@Override
	public void loadLayout(ToolBarAreaPersistence persistence) {
		synchronized(layoutLock) {
			LogRunner.logger().log(Level.INFO, "Loading toolbar layout.");
			persistToolArea(persistence, DockPosition.Top);
			persistToolArea(persistence, DockPosition.Left);
			persistToolArea(persistence, DockPosition.Right);
			persistToolArea(persistence, DockPosition.Bottom);
		}
	}

	@Override
	public void loadLayout(String layout) {
		try {
			InputStream stream = new ByteArrayInputStream(layout.getBytes(StandardCharsets.UTF_8));
			XmlInputReader reader = new XmlInputReader(stream);
			manager = new XmlManager(new String(), new XmlGenerator(), reader);
			configurationManager = new XmlConfigurationManager(manager, new ArrayList<PersistXml>());
			configurationManager.load();
			
			ToolBarAreaPersistence persistence = new ToolBarAreaPersistence((XmlConfigurationObject)configurationManager.configurationObjects().get(0));
			loadLayout(persistence);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void persistToolArea(ToolBarAreaPersistence persistence, DockPosition pos) {
		for (PersistXml config : persistence.getToolBars(pos).children()) {
			ToolBarPersistence tbp = (ToolBarPersistence)config;
			
			persistOrDeferToolBar(tbp, pos);
		}
	}
	
	private void persistOrDeferToolBar(ToolBarPersistence tbp, DockPosition pos) {
		try {
			addToolBarImpl(tbp.getBluePrint(), LocationProvider.last(), pos, tbp.getDraggable(), tbp.getID());
		} catch (IllegalArgumentException e) {
			LogRunner.logger().log(Level.INFO, String.format("Adding: %s, to defered toolbars", tbp));
			// In the case we persist before the blue print has registered this will happen.
			deferredToolBars.add(new Tuple<DockPosition, ToolBarPersistence>(pos, tbp));
		}
	}
	
	private void persistToolArea(ObservableList<DraggableToolBar> toolBars, DockPosition pos) throws TransformerConfigurationException, ParserConfigurationException {
		persistenceObject.clearToolBars(pos);
		
		for (DraggableToolBar dtb : toolBars) {
			if (!toolBarPersisted(dtb.toolBar())) {
				continue;
			}
			
			Tuple<String, String> bPID = getBluePrintAndID(dtb.toolBar());
			ToolBarPersistence toolBarPersistence = new ToolBarPersistence();
			toolBarPersistence.setID(bPID.key());
			toolBarPersistence.setBluePrint(bPID.value());
			toolBarPersistence.setDraggable(dtb.draggableProperty().get());
			persistenceObject.addToolBar(toolBarPersistence, pos);
		}
	}
	
	private boolean toolBarPersisted(ToolBar toolBar) {
		for (Tuple<String, ToolBar> entry : persistableToolBars.values()) {
			if (entry.value() == toolBar) {
				return true;
			}
		}
		
		return false;
	}
	
	private Tuple<String, String> getBluePrintAndID(ToolBar toolBar) {
		String id = new String();
		String bluePrint = new String();
		for (Entry<String, Tuple<String, ToolBar>> entry : persistableToolBars.entrySet()) {
			if (entry.getValue().value() != toolBar) {
				continue;
			}
			
			id = entry.getKey();
			bluePrint = entry.getValue().key();
			break;
		}
		
		return new Tuple<String, String>(id, bluePrint);
	}
	
	@Override
	public void removeToolBar(String id) {
		synchronized(layoutLock) {
			ToolBar tb = persistableToolBars.get(id).value();
		
			service.removeToolBar(tb);
		
			persistableToolBars.remove(id);
		}
	}
	
	@Override
	public void clearToolBars() {
		synchronized(layoutLock) {
			LogRunner.logger().log(Level.INFO, "Clearing toolbars.");
			for (Tuple<String, ToolBar> entry : persistableToolBars.values()) {
				service.removeToolBar(entry.value());
			}
		
			persistableToolBars.clear();
			deferredToolBars.clear();
		}
	}
}
