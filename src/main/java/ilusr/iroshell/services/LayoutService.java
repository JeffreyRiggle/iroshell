package ilusr.iroshell.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.core.data.Tuple;
import ilusr.core.datamanager.xml.XmlGenerator;
import ilusr.core.datamanager.xml.XmlInputReader;
import ilusr.core.datamanager.xml.XmlManager;
import ilusr.core.io.XMLDocumentUtilities;
import ilusr.core.javafx.ObservableListBinder;
import ilusr.iroshell.dockarea.DockArea;
import ilusr.iroshell.dockarea.DockAreaModel;
import ilusr.iroshell.dockarea.DockPanelModel;
import ilusr.iroshell.dockarea.DockTab;
import ilusr.iroshell.dockarea.SelectionManager;
import ilusr.iroshell.dockarea.TabModel;
import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.documentinterfaces.mdi.MDIProvider;
import ilusr.iroshell.documentinterfaces.mdi.MultipleDocumentInterface;
import ilusr.iroshell.documentinterfaces.sdi.SDIProvider;
import ilusr.iroshell.documentinterfaces.sdi.SelectorNode;
import ilusr.iroshell.documentinterfaces.sdi.SingleDocumentInterface;
import ilusr.iroshell.persistence.DockAreaPersistence;
import ilusr.iroshell.persistence.MDIPersistence;
import ilusr.iroshell.persistence.SDIPersistence;
import ilusr.iroshell.persistence.TabContentPersistence;
import ilusr.iroshell.persistence.TabPanelPersistence;
import ilusr.logrunner.LogRunner;
import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationManager;
import ilusr.persistencelib.configuration.XmlConfigurationObject;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.MenuItem;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class LayoutService implements ILayoutService {

	private final IStyleContainerService styleService;
	private final SelectionManager selectionManager;
	private final SimpleObjectProperty<DocumentType> documentType;
	private final SingleDocumentInterface sdi;
	private final MultipleDocumentInterface mdi;
	private final Map<String, ITabContentBluePrint> bluePrintMap;
	private final Map<String, Tuple<String, ITabContent>> persistableTabs;
	private final Object bluePrintLock = new Object();
	private final Object layoutLock = new Object();
	
	private XmlManager manager;
	private XmlConfigurationManager configurationManager;
	
	/**
	 * 
	 * @param manager A @see SelectionManager.
	 * @param styleService A @See IStyleContainerService.
	 * @param sdiProvider A @see SDIProvider.
	 * @param mdiProvider A @see MDIProvider.
	 */
	public LayoutService(SelectionManager manager, 
						 IStyleContainerService styleService,
						 SDIProvider sdiProvider, 
						 MDIProvider mdiProvider) {
		this(manager, styleService, sdiProvider, mdiProvider, new HashMap<String, ITabContentBluePrint>(), new HashMap<String, Tuple<String, ITabContent>>());
	}
	
	/**
	 * 
	 * @param manager A @see SelectionManager
	 * @param styleService A @See IStyleContainerService.
	 * @param sdiProvider A @see SDIProvider.
	 * @param mdiProvider A @see MDIProvider.
	 * @param bluePrintMap A @ee Map of string to @see ITabContentBluePrint.
	 * @param persistableTabs A @see Map of string to @see Tuple of String to @See ITabContent.
	 */
	public LayoutService(SelectionManager manager, 
						 IStyleContainerService styleService,
						 SDIProvider sdiProvider, 
						 MDIProvider mdiProvider,
						 Map<String, ITabContentBluePrint> bluePrintMap,
						 Map<String, Tuple<String, ITabContent>> persistableTabs) {
		this.bluePrintMap = bluePrintMap;
		this.persistableTabs = persistableTabs;
		selectionManager = manager;
		this.styleService = styleService;
		
		sdi = sdiProvider.getView();
		mdi = mdiProvider.getView();
		documentType = new SimpleObjectProperty<DocumentType>();
		
		try {
			this.manager = new XmlManager(new String());
			configurationManager = new XmlConfigurationManager(this.manager, new ArrayList<PersistXml>());
		} catch (Exception e) {
			LogRunner.logger().severe(e);
		}
	}
	
	@Override
	public void registerBluePrint(String bluePrintName, ITabContentBluePrint bluePrint) {
		synchronized(bluePrintLock) {
			LogRunner.logger().info(String.format("Registering blue print: %s", bluePrintName));
			bluePrintMap.put(bluePrintName, bluePrint);
		}
	}
	
	@Override
	public void unregisterBluePrint(String bluePrintName) {
		synchronized(bluePrintLock) {
			LogRunner.logger().info(String.format("Un-registering blue print: %s", bluePrintName));
			bluePrintMap.remove(bluePrintName);
		}
	}
	
	@Override
	public SimpleObjectProperty<DocumentType> documentType() {
		return documentType;
	}
	
	@Override
	public void setSelector(SelectorNode node) throws IllegalStateException {
		if (documentType.getValue() != DocumentType.SDI) {
			throw new IllegalStateException("Must be in SDI for this operation");
		}
		
		LogRunner.logger().info("Adding selector pane to SDI");
		sdi.setSelectorPane(node);
	}
	
	@Override
	public String addTab(String bluePrintName) throws IllegalArgumentException, IllegalStateException {
		return addTab(bluePrintName, null);
	}
	
	@Override
	public String addTab(String bluePrintName, String customData) {
		if (!canAddTab()) {
			return null;
		}
		
		ITabContentBluePrint bluePrint;
		synchronized(bluePrintLock) {
			bluePrint = bluePrintMap.get(bluePrintName);
		}
		
		if (bluePrint == null) {
			throw new IllegalArgumentException(String.format("Invalid BluePrintName: %s", bluePrintName));
		}
		
		LogRunner.logger().info(String.format("Creating tab from blue print: %s", bluePrintName));
		
		ITabContent tab;
		
		if (customData != null) {
			tab = bluePrint.create(customData);
		} else {
			tab = bluePrint.create();
		}
		
		String id = tab.getId();
		
		synchronized(layoutLock) {
			addTab(tab);
			persistableTabs.put(id, new Tuple<String, ITabContent>(bluePrintName, tab));
			return id;
		}
	}
	
	@Override
	public void addTab(ITabContent tab) throws IllegalStateException {
		if (!canAddTab()) {
			return;
		}
		
		DockPanelModel model = (DockPanelModel)selectionManager.selected();
		
		DockTab dTab = convertToDockTab(tab);
		
		LogRunner.logger().info(String.format("Adding tab: %s", dTab.idProperty().get()));
		model.tabs().add(dTab);
		dTab.select();
	}
	
	@Override
	public ITabContent getTabContent(String id) {
		return persistableTabs.get(id).value();
	}
	
	private boolean canAddTab() throws IllegalStateException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		// TODO: :( Liskvo.
		if (!(selectionManager.selected() instanceof DockPanelModel)) {
			// TODO: What to do here.
			return false;
		}
		
		return true;
	}
	
	private DockTab convertToDockTab(ITabContent tab) {
		TabModel tabModel = new TabModel(tab.getId());
		
		tabModel.content().bind(tab.content());
		tabModel.canClose().bind(tab.canClose());
		tabModel.toolTip().bind(tab.toolTip());
		tabModel.title().bind(tab.titleGraphic());
		
		tabModel.contextMenuItems().addAll(getAdditionalMenuItems(tabModel));
		tabModel.contextMenuItems().addAll(tab.contextMenuItems());
		@SuppressWarnings("unused")
		ObservableListBinder<MenuItem> binder = new ObservableListBinder<MenuItem>(tab.contextMenuItems(), tabModel.contextMenuItems());
		
		tab.addCloseListener(tabModel);
		tabModel.addCloseListener(() -> {
			tab.close();
		});
		
		DockTab retVal = new DockTab(tabModel, styleService);
		
		return retVal;
	}
	
	//TODO: Should this be done here?
	private List<MenuItem> getAdditionalMenuItems(TabModel model) {
		List<MenuItem> retVal = new ArrayList<MenuItem>();
		MenuItem closeAll = new MenuItem("Close all.");
		closeAll.setOnAction((e) -> {
			removeAllTabs();
		});
		
		MenuItem close = new MenuItem("Close.");
		close.setOnAction((e) -> {
			model.close();
		});
		
		MenuItem closeAllButThis = new MenuItem("Close all but this.");
		closeAllButThis.setOnAction((e) -> {
			removeAllButThis(model.id(), mdi.getDock().model());
		});
		
		retVal.add(close);
		retVal.add(closeAll);
		retVal.add(closeAllButThis);
		return retVal;
	}
	
	private void removeAllButThis(String id, DockAreaModel model) {
		LogRunner.logger().info(String.format("Removing all tabs except: %s", id));
		
		// Clone the list to avoid concurrent modifications.
		final ObservableList<DockTab> tabs = FXCollections.observableArrayList(model.getDockPanel().tabs());
				
		tabs.forEach((t) -> {
			if (!t.idProperty().get().equals(id)) {
				t.close();
			}
		});
				
		if (model.hasChildren().get()) {
			removeAllTabsImpl(model.child().get());
		}
	}
	
	@Override
	public void removeTab(String tabId) throws IllegalStateException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		removeTabImpl(mdi.getDock().model(), tabId);
		persistableTabs.remove(tabId);
	}

	@Override
	public void removeTab(ITabContent tab) throws IllegalStateException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		removeTabImpl(mdi.getDock().model(), tab.getId());
	}
	
	@Override
	public void removeAllTabs() throws IllegalStateException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		removeAllTabsImpl(mdi.getDock().model());
	}
	
	private void removeTabImpl(DockAreaModel model, String id) {
		if (tryRemoveTab(model.getDockPanel(), id)) {
			return;
		}
		
		if (model.hasChildren().get()) {
			removeTabImpl(model.child().get(), id);
		}
	}
	
	private boolean tryRemoveTab(DockPanelModel model, String id) {
		for (DockTab tab : model.tabs()) {
			if (tab.idProperty().get().equals(id)) {
				LogRunner.logger().info(String.format("Removing tab: %s", id));
				model.tabs().remove(tab);
				return true;
			}
		}
		
		return false;
	}
	
	private void removeAllTabsImpl(DockAreaModel model) {
		LogRunner.logger().info("Removing all tabs");
		
		// Clone the list to avoid concurrent modifications.
		final ObservableList<DockTab> tabs = FXCollections.observableArrayList(model.getDockPanel().tabs());
		
		tabs.forEach((t) -> {
			t.close();
		});
		
		if (model.hasChildren().get()) {
			removeAllTabsImpl(model.child().get());
		}
	}
	
	@Override
	public String getLayout() {
		savePersistence();
		return XMLDocumentUtilities.convertDocumentToString(manager.document());
	}
	
	@Override
	public MDIPersistence getMDIPersistenceModel() throws IllegalStateException, TransformerConfigurationException, ParserConfigurationException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		DockArea dock = mdi.getDock();
		
		MDIPersistence persistence = new MDIPersistence();
		DockAreaPersistence dockPersistence = new DockAreaPersistence();
		
		dockPersistence.setWidth(dock.getDockWidth());
		dockPersistence.setHeight(dock.getDockHeight());
		addTabZones(dockPersistence, dock.model());
		
		persistence.setDockArea(dockPersistence);
		return persistence;
	}
	
	@Override
	public SDIPersistence getSDIPersistenceModel() throws IllegalStateException, TransformerConfigurationException, ParserConfigurationException {
		if (documentType.getValue() != DocumentType.SDI) {
			throw new IllegalStateException("Must be in SDI for this operation");
		}
		
		SDIPersistence persistence = new SDIPersistence();
		persistence.setSelectedItem(sdi.getModel().selectedViewId());
		persistence.setSelectorPosition(sdi.getSplitter().getDividerPositions()[0]);
		
		return persistence;
	}
	
	@Override
	public void savePersistence() {
		try {
			LogRunner.logger().info("Saving Persistence");
			configurationManager.clearConfigurationObjects();
			XmlConfigurationObject persistence;
			
			if (documentType.getValue() == DocumentType.MDI) {
				persistence = getMDIPersistenceModel();
			} else {
				persistence = getSDIPersistenceModel();
			}
			
			configurationManager.addConfigurationObject(persistence);
			configurationManager.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void addTabZones(DockAreaPersistence persistence, DockAreaModel model) throws TransformerConfigurationException, ParserConfigurationException {
		persistence.setWidth(model.width().get());
		persistence.setHeight(model.height().get());
		TabPanelPersistence panel = new TabPanelPersistence();
		panel.selectedTab(model.getDockPanel().selectedTab().get());
		addTabsToPanel(panel, model);
		persistence.setTabPanel(panel);
		
		if (model.hasChildren().get() && model.child().get().getDockPanel().tabs().size() != 0) {
			persistence.setOrientation(model.orientation().get());
			persistence.setIndex(model.index().get());
			
			DockAreaPersistence dockArea = new DockAreaPersistence();
			addTabZones(dockArea, model.child().get());
			persistence.setChildDock(dockArea);
		}
	}
	
	private void addTabsToPanel(TabPanelPersistence panel, DockAreaModel model) throws TransformerConfigurationException, ParserConfigurationException {
		for (DockTab tab : model.getDockPanel().tabs()) {
			Tuple<String, ITabContent> sTab = findPersistableTab(tab.getId());
			
			if (sTab == null) {
				continue;
			}
			
			TabContentPersistence pTab = new TabContentPersistence();
			pTab.setId(sTab.value().getId());
			pTab.setBluePrintName(sTab.key());
			pTab.setCustomData(encodeCustomData(sTab.value().customData().get()));
			panel.addTab(pTab);
		}
	}
	
	private String encodeCustomData(String customData) {
		if (customData == null) {
			return new String();
		}
		
		return Base64.getEncoder().encodeToString(customData.getBytes());
	}
	
	private String decodeCustomData(String customData) {
		if (customData == null) {
			return new String();
		}
		
		byte[] bytes = Base64.getDecoder().decode(customData);
		return new String(bytes);
	}
	
	private Tuple<String, ITabContent> findPersistableTab(String id) {
		Tuple<String, ITabContent> retVal = null;
		for (Entry<String, Tuple<String, ITabContent>> entry : persistableTabs.entrySet()) {
			if (!entry.getKey().equals(id)) {
				continue;
			}
			
			retVal = entry.getValue();
		}
		
		return retVal;
	}
	
	@Override
	public void loadPersistenceModel(MDIPersistence persistence) throws IllegalStateException {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		LogRunner.logger().info("Loading MDI Persistence");
		DockAreaModel dockModel = buildDockAreaModel(new DockAreaModel(), persistence.getDockArea());
		mdi.getDock().setModel(dockModel);
	}
	
	private DockAreaModel buildDockAreaModel(DockAreaModel model, DockAreaPersistence persistence) {
		//TODO: Should I have to do this
		model.setDockPanel(new DockPanelModel(styleService));
		model.orientation().set(persistence.getOrientation());
		model.index().set(persistence.getIndex());
		model.width().set(persistence.getWidth());
		model.height().set(persistence.getHeight());
		model.getDockPanel().selectedTab().set(persistence.getTabPanel().selectedTab());
		
		model.getDockPanel().tabs().addAll(restoreTabs(persistence.getTabPanel()));
		
		if (persistence.getChildDock() == null) {
			return model;
		}
		
		model.hasChildren().set(true);
		model.child().set(buildDockAreaModel(new DockAreaModel(), persistence.getChildDock()));
		return model;
	}
	
	private List<DockTab> restoreTabs(TabPanelPersistence persistence) {
		List<DockTab> retVal = new ArrayList<DockTab>();
		
		for (PersistXml config : persistence.getTabs().children()) {
			TabContentPersistence tabP = (TabContentPersistence)config;
			
			ITabContent tab = restoreTab(tabP);
			retVal.add(convertToDockTab(tab));
			persistableTabs.put(tabP.getId(), new Tuple<String, ITabContent>(tabP.getBluePrintName(), tab));
		}
		
		return retVal;
	}
	
	private ITabContent restoreTab(TabContentPersistence persistence) {
		ITabContentBluePrint bluePrint;
		synchronized(bluePrintLock) {
			bluePrint = bluePrintMap.get(persistence.getBluePrintName());
		}
		
		if (bluePrint == null) {
			throw new IllegalArgumentException(String.format("Invalid BluePrintName: %s", persistence.getBluePrintName()));
		}
		
		String data = decodeCustomData(persistence.getCustomData());
		ITabContent content;
		if (data == null || data.equals(new String())) {
			content = bluePrint.create();
		} else {
			content = bluePrint.create(data);
		}
		
		content.setId(persistence.getId());
		
		return content;
	}
	
	@Override
	public void loadPersistenceModel(SDIPersistence persistence) throws IllegalStateException {
		if (documentType.getValue() != DocumentType.SDI) {
			throw new IllegalStateException("Must be in SDI for this operation.");
		}
		
		LogRunner.logger().info("Loading SDI Persistence");
		sdi.getSplitter().setDividerPositions(persistence.getSelectorPosition());
		sdi.getModel().changeView(persistence.getSelectedItem());
	}

	@Override
	public void loadLayout(String layoutData) {
		try {
			InputStream stream = new ByteArrayInputStream(layoutData.getBytes(StandardCharsets.UTF_8));
			XmlInputReader reader = new XmlInputReader(stream);
			manager = new XmlManager(new String(), new XmlGenerator(), reader);
			configurationManager = new XmlConfigurationManager(manager, new ArrayList<PersistXml>());
			configurationManager.load();
			
			XmlConfigurationObject config = (XmlConfigurationObject)configurationManager.configurationObjects().get(0);
			
			if (config.name().equals("SDI")) {
				SDIPersistence persistence = new SDIPersistence(config);
				loadPersistenceModel(persistence);
				return;
			}
			
			MDIPersistence persistence = new MDIPersistence(config);
			loadPersistenceModel(persistence);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> getTabIds(String bluePrintName) {
		if (documentType.getValue() != DocumentType.MDI) {
			throw new IllegalStateException("Must be in MDI for this operation");
		}
		
		List<String> retVal = new ArrayList<String>();
		for (Entry<String, Tuple<String, ITabContent>> entry : persistableTabs.entrySet()) {
			if (entry.getValue().key().equals(bluePrintName)) {
				retVal.add(entry.getKey());
			}
		}
		
		return retVal;
	}
}