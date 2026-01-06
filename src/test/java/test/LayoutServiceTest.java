import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import ilusr.core.data.Tuple;
import ilusr.core.test.JavaFXRule;
import ilusr.iroshell.dockarea.DockArea;
import ilusr.iroshell.dockarea.DockAreaModel;
import ilusr.iroshell.dockarea.DockPanelModel;
import ilusr.iroshell.dockarea.DockTab;
import ilusr.iroshell.dockarea.SelectionManager;
import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.documentinterfaces.mdi.MDIProvider;
import ilusr.iroshell.documentinterfaces.mdi.MultipleDocumentInterface;
import ilusr.iroshell.documentinterfaces.sdi.SDIModel;
import ilusr.iroshell.documentinterfaces.sdi.SDIProvider;
import ilusr.iroshell.documentinterfaces.sdi.SelectorNode;
import ilusr.iroshell.documentinterfaces.sdi.SingleDocumentInterface;
import ilusr.iroshell.persistence.MDIPersistence;
import ilusr.iroshell.persistence.SDIPersistence;
import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.ITabContent;
import ilusr.iroshell.services.ITabContentBluePrint;
import ilusr.iroshell.services.LayoutService;
import ilusr.iroshell.services.StyleContainerService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;

public class LayoutServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	
	private final String MDI_PERSISTENCE = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><MDI><DockArea><Width ValueType=\"double\">100.0</Width><Height ValueType=\"double\">100.0</Height><Index ValueType=\"int\">0</Index><Orientation ValueType=\"object\">HORIZONTAL</Orientation><TabPanel><SelectedTab ValueType=\"int\">0</SelectedTab><Tabs><ContentTab><ID ValueType=\"string\">TABID1</ID><CustomData ValueType=\"string\"/><BluePrintName ValueType=\"string\">BluePrint1</BluePrintName></ContentTab></Tabs></TabPanel></DockArea></MDI>";
	private final String SELECTED_VIEW_ID = "TestView";
	
	private final String BLUEPRINT1 = "BluePrint1";
	private final String BLUEPRINT2 = "BluePrint2";
	private final String BLUEPRINT3 = "BluePrint3";
	
	private ITabContentBluePrint bluePrint1;
	private ITabContentBluePrint bluePrint2;
	private ITabContentBluePrint bluePrint3;
	
	private ITabContent tabContent1;
	private ITabContent tabContent2;
	private ITabContent tabContent3;
	
	private ObservableList<DockTab> dockTabs;
	
	private SelectorNode selectorNode;
	private SingleDocumentInterface sdi;
	private MultipleDocumentInterface mdi;
	
	private SelectionManager selectionManager;
	private IStyleContainerService styleService;
	private SDIProvider sdiProvider; 
	private MDIProvider mdiProvider;
	private Map<String, ITabContentBluePrint> bluePrintMap;
	private Map<String, Tuple<String, ITabContent>> persistableTabs;
	
	private LayoutService service;
	
	@Before
	public void setup() {
		tabContent1 = Mockito.mock(ITabContent.class);
		Mockito.when(tabContent1.canClose()).thenReturn(new SimpleBooleanProperty(true));
		Mockito.when(tabContent1.content()).thenReturn(new SimpleObjectProperty<Node>(new Label("Test")));
		Mockito.when(tabContent1.customData()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent1.getId()).thenReturn("TABID1");
		Mockito.when(tabContent1.titleGraphic()).thenReturn(new SimpleObjectProperty<Node>(new Label("Header")));
		Mockito.when(tabContent1.toolTip()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent1.contextMenuItems()).thenReturn(FXCollections.observableArrayList());

		tabContent2 = Mockito.mock(ITabContent.class);
		Mockito.when(tabContent2.canClose()).thenReturn(new SimpleBooleanProperty(true));
		Mockito.when(tabContent2.content()).thenReturn(new SimpleObjectProperty<Node>(new Label("Test2")));
		Mockito.when(tabContent2.customData()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent2.getId()).thenReturn("TABID2");
		Mockito.when(tabContent2.titleGraphic()).thenReturn(new SimpleObjectProperty<Node>(new Label("Header2")));
		Mockito.when(tabContent2.toolTip()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent2.contextMenuItems()).thenReturn(FXCollections.observableArrayList());

		tabContent3 = Mockito.mock(ITabContent.class);
		Mockito.when(tabContent3.canClose()).thenReturn(new SimpleBooleanProperty(true));
		Mockito.when(tabContent3.content()).thenReturn(new SimpleObjectProperty<Node>(new Label("Test3")));
		Mockito.when(tabContent3.customData()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent3.getId()).thenReturn("TABID3");
		Mockito.when(tabContent3.titleGraphic()).thenReturn(new SimpleObjectProperty<Node>(new Label("Header3")));
		Mockito.when(tabContent3.toolTip()).thenReturn(new SimpleStringProperty(new String()));
		Mockito.when(tabContent3.contextMenuItems()).thenReturn(FXCollections.observableArrayList());

		bluePrint1 = Mockito.mock(ITabContentBluePrint.class);
		Mockito.when(bluePrint1.create()).thenReturn(tabContent1);
		bluePrint2 = Mockito.mock(ITabContentBluePrint.class);
		Mockito.when(bluePrint2.create()).thenReturn(tabContent2);
		bluePrint3 = Mockito.mock(ITabContentBluePrint.class);
		Mockito.when(bluePrint3.create()).thenReturn(tabContent3);
		
		styleService = Mockito.mock(StyleContainerService.class);
		
		selectorNode = Mockito.mock(SelectorNode.class);
		sdi = Mockito.mock(SingleDocumentInterface.class);
		mdi = Mockito.mock(MultipleDocumentInterface.class);
		
		SDIModel sdm = Mockito.mock(SDIModel.class);
		Mockito.when(sdm.selectedViewId()).thenReturn(SELECTED_VIEW_ID);
		
		SplitPane sp = Mockito.mock(SplitPane.class);
		Mockito.when(sp.getDividerPositions()).thenReturn(new double[] { 1.0 });
		
		Mockito.when(sdi.getModel()).thenReturn(sdm);
		Mockito.when(sdi.getSplitter()).thenReturn(sp);
		
		DockPanelModel dpm = Mockito.mock(DockPanelModel.class);
		dockTabs = FXCollections.observableArrayList();
		
		Mockito.when(dpm.tabs()).thenReturn(dockTabs);
		Mockito.when(dpm.selectedTab()).thenReturn(new SimpleObjectProperty<Integer>(0));
		
		DockAreaModel dam = Mockito.mock(DockAreaModel.class);
		Mockito.when(dam.child()).thenReturn(new SimpleObjectProperty<DockAreaModel>());
		Mockito.when(dam.getDockPanel()).thenReturn(dpm);
		Mockito.when(dam.hasChildren()).thenReturn(new SimpleBooleanProperty());
		Mockito.when(dam.index()).thenReturn(new SimpleObjectProperty<Integer>(0));
		Mockito.when(dam.orientation()).thenReturn(new SimpleObjectProperty<Orientation>(Orientation.HORIZONTAL));
		Mockito.when(dam.width()).thenReturn(new SimpleDoubleProperty(100.0));
		Mockito.when(dam.height()).thenReturn(new SimpleDoubleProperty(100.0));
		
		DockArea da = Mockito.mock(DockArea.class);
		Mockito.when(da.model()).thenReturn(dam);
		Mockito.when(da.getDockHeight()).thenReturn(100.0);
		Mockito.when(da.getDockWidth()).thenReturn(100.0);
		
		selectionManager = Mockito.mock(SelectionManager.class);
		Mockito.when(selectionManager.selected()).thenReturn(dpm);
		sdiProvider = Mockito.mock(SDIProvider.class);
		Mockito.when(sdiProvider.getView()).thenReturn(sdi);
		
		mdiProvider = Mockito.mock(MDIProvider.class);
		Mockito.when(mdiProvider.getView()).thenReturn(mdi);
		Mockito.when(mdi.getDock()).thenReturn(da);
		
		bluePrintMap = new HashMap<String, ITabContentBluePrint>();
		persistableTabs = new HashMap<String, Tuple<String, ITabContent>>();
		
		service = new LayoutService(selectionManager, styleService, sdiProvider, mdiProvider, bluePrintMap, persistableTabs);
	}
	
	@Test
	public void testCreate() {
		assertNotNull(service);
	}
	
	@Test
	public void testRegisterBluePrint() {
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		assertTrue(bluePrintMap.containsKey(BLUEPRINT1));
		service.registerBluePrint(BLUEPRINT2, bluePrint2);
		assertTrue(bluePrintMap.containsKey(BLUEPRINT2));
		service.registerBluePrint(BLUEPRINT3, bluePrint3);
		assertTrue(bluePrintMap.containsKey(BLUEPRINT3));
	}
	
	@Test
	public void testUnRegisterBluePrint() {
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		service.registerBluePrint(BLUEPRINT2, bluePrint2);
		service.registerBluePrint(BLUEPRINT3, bluePrint3);
		
		service.unregisterBluePrint(BLUEPRINT1);
		assertEquals(bluePrintMap.size(), 2);
		service.unregisterBluePrint(BLUEPRINT2);
		assertEquals(bluePrintMap.size(), 1);
		service.unregisterBluePrint(BLUEPRINT3);
		assertEquals(bluePrintMap.size(), 0);
	}
	
	@Test
	public void testDocumentType() {
		service.documentType().set(DocumentType.MDI);
		assertEquals(service.documentType().get(), DocumentType.MDI);
		
		service.documentType().set(DocumentType.SDI);
		assertEquals(service.documentType().get(), DocumentType.SDI);		
	}
	
	@Test
	public void testSetSelectorWhenInSDI() {
		service.documentType().set(DocumentType.SDI);
		
		try {
			service.setSelector(selectorNode);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		Mockito.verify(sdi, Mockito.times(1)).setSelectorPane(selectorNode);
	}
	
	@Test
	public void testSetSelectionWhenInMDI() {
		boolean passed = false;
		service.documentType().set(DocumentType.MDI);
		
		try {
			service.setSelector(selectorNode);
		} catch (IllegalStateException e) {
			passed = true;
		}
		
		assertTrue(passed);
		Mockito.verify(sdi, Mockito.times(0)).setSelectorPane(selectorNode);
	}
	
	@Test
	public void testAddTabFromBluePrintWithoutRegistration() {
		boolean passed = false;
		service.documentType().set(DocumentType.MDI);
		
		try {
			service.addTab(BLUEPRINT1);
		} catch (IllegalArgumentException e) {
			passed = true;
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void testAddTabFromBluePrintWithRegistration() {
		service.documentType().set(DocumentType.MDI);
		
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		
		try {
			service.addTab(BLUEPRINT1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		Mockito.verify(bluePrint1, Mockito.times(1)).create();
		assertTrue(persistableTabs.containsKey("TABID1"));
	}
	
	@Test
	public void testAddTabFromBluePrintWhenInSDI() {
		boolean passed = false;
		service.documentType().set(DocumentType.SDI);
		
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		
		try {
			service.addTab(BLUEPRINT1);
		} catch (IllegalStateException e) {
			passed = true;
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void testAddContentTab() {
		service.documentType().set(DocumentType.MDI);
		
		try {
			service.addTab(tabContent1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testAddContentTabInSDI() {
		boolean passed = false;
		service.documentType().set(DocumentType.SDI);
		
		try {
			service.addTab(tabContent1);
		} catch (IllegalStateException e) {
			passed = true;
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void testGetLayout() {
		//TODO Actually persist tab.
		service.documentType().set(DocumentType.MDI);
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		service.addTab(BLUEPRINT1);
		
		assertEquals(service.getLayout(), MDI_PERSISTENCE);
	}
	
	@Test
	public void testGetMDIPersistenceInMDI() {
		service.documentType().set(DocumentType.MDI);
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		service.addTab(BLUEPRINT1);
		
		MDIPersistence persistence = null;
		
		try {
			service.savePersistence();
			persistence = service.getMDIPersistenceModel();
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertEquals(persistence.getDockArea().getWidth(), 100.0, 1);
		assertEquals(persistence.getDockArea().getHeight(), 100.0, 1);
		assertEquals(persistence.getDockArea().getOrientation(), Orientation.HORIZONTAL);
	}
	
	@Test
	public void testGetMDIPersistenceInSDI() {
		boolean passed = false;
		service.documentType().set(DocumentType.SDI);
		
		try {
			service.savePersistence();
			service.getMDIPersistenceModel();
		} catch (IllegalStateException e) {
			passed = true;
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void testGetSDIPersistenceInMDI() {
		boolean passed = false;
		service.documentType().set(DocumentType.MDI);
		
		try {
			service.savePersistence();
			service.getSDIPersistenceModel();
		} catch (IllegalStateException e) {
			passed = true;
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		
		assertTrue(passed);
	}
	
	@Test
	public void testGetSDIPersistenceInSDI() {
		service.documentType().set(DocumentType.SDI);
		
		SDIPersistence persistence = null;
		
		try {
			service.savePersistence();
			persistence = service.getSDIPersistenceModel();
		} catch (Exception ex) {
			fail(ex.getMessage());
		}
		
		assertEquals(persistence.getSelectedItem(), SELECTED_VIEW_ID);
		assertEquals(persistence.getSelectorPosition(), 1.0, 1);
	}
	
	@Test
	public void testRemoveTabFromId() {
		service.documentType().set(DocumentType.MDI);
		
		service.registerBluePrint(BLUEPRINT1, bluePrint1);
		
		try {
			service.addTab(BLUEPRINT1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertTrue(persistableTabs.containsKey("TABID1"));
		
		service.removeTab("TABID1");
		assertFalse(persistableTabs.containsKey("TABID1"));
	}
	
	@Test
	public void testRemoveTabFromContent() {
		service.documentType().set(DocumentType.MDI);
		
		try {
			service.addTab(tabContent1);
		} catch (Exception e) {
			fail(e.getMessage());
		}
		
		assertTrue(foundTab("TABID1"));
		service.removeTab(tabContent1);
		assertFalse(foundTab("TABID1"));
	}
	
	private boolean foundTab(String id) {
		for (DockTab tab : dockTabs) {
			if (tab.idProperty().get().equals(id)) {
				return true;
			}
		}
		
		return false;
	}
}
