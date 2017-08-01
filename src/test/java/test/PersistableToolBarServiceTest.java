package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import ilusr.core.data.Tuple;
import ilusr.core.test.JavaFXRule;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationParameters;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.persistence.ToolBarAreaPersistence;
import ilusr.iroshell.persistence.ToolBarPersistence;
import ilusr.iroshell.services.IPersistableToolBarService;
import ilusr.iroshell.services.IToolBarBluePrint;
import ilusr.iroshell.services.IToolBarService;
import ilusr.iroshell.services.PersistableToolBarService;
import ilusr.iroshell.toolbar.DraggableToolBar;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ToolBar;

public class PersistableToolBarServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	
	private final String BLUE_PRINT_ID_1 = "ID1";
	private final String BLUE_PRINT_ID_2 = "ID2";
	private final String BLUE_PRINT_ID_3 = "ID3";
	
	private final String EXPECTED_LAYOUT_STRING = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><ToolBars><Left><ToolBar><BluePrint ValueType=\"string\">ID3</BluePrint><id ValueType=\"string\">ID3</id><Draggable ValueType=\"bool\">true</Draggable></ToolBar></Left><Top><ToolBar><BluePrint ValueType=\"string\">ID1</BluePrint><id ValueType=\"string\">ID1</id><Draggable ValueType=\"bool\">true</Draggable></ToolBar></Top><Right><ToolBar><BluePrint ValueType=\"string\">ID1</BluePrint><id ValueType=\"string\">ID4</id><Draggable ValueType=\"bool\">true</Draggable></ToolBar></Right><Bottom><ToolBar><BluePrint ValueType=\"string\">ID2</BluePrint><id ValueType=\"string\">ID2</id><Draggable ValueType=\"bool\">true</Draggable></ToolBar></Bottom></ToolBars>";
	
	private IToolBarBluePrint bluePrint1;
	private IToolBarBluePrint bluePrint2;
	private IToolBarBluePrint bluePrint3;
	
	
	private IToolBarService toolBarService;
	private Map<String, IToolBarBluePrint> bluePrintMap;
	private Map<String, Tuple<String, ToolBar>> persistableToolBars;
	private List<Tuple<DockPosition, ToolBarPersistence>> deferredToolBars;
	
	private IPersistableToolBarService service;
	
	private ToolBar toolbar1;
	private ToolBar toolbar2;
	private ToolBar toolbar3;
	private ToolBar toolbar4;
	
	@Before
	public void setup() {
		toolBarService = Mockito.mock(IToolBarService.class);
		
		toolbar1 = new ToolBar();
		toolbar1.idProperty().set("Tool1");
		
		toolbar2 = new ToolBar();
		toolbar2.idProperty().set("Tool2");
		
		toolbar3 = new ToolBar();
		toolbar3.idProperty().set("Tool3");
		
		toolbar4 = new ToolBar();
		toolbar4.idProperty().set("Tool4");
		
		bluePrintMap = new HashMap<String, IToolBarBluePrint>();
		persistableToolBars = new HashMap<String, Tuple<String, ToolBar>>();
		deferredToolBars = new ArrayList<Tuple<DockPosition, ToolBarPersistence>>();
		
		bluePrint1 = Mockito.mock(IToolBarBluePrint.class);
		Mockito.when(bluePrint1.create()).thenReturn(toolbar1);
		bluePrint2 = Mockito.mock(IToolBarBluePrint.class);
		Mockito.when(bluePrint2.create()).thenReturn(toolbar2);
		bluePrint3 = Mockito.mock(IToolBarBluePrint.class);
		Mockito.when(bluePrint3.create()).thenReturn(toolbar3);
		
		service = new PersistableToolBarService(toolBarService, bluePrintMap, persistableToolBars, deferredToolBars);
	}

	@Test
	public void testCreate() {
		assertNotNull(service);
	}
	
	@Test
	public void testRegisterBluePrint() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		assertTrue(bluePrintMap.containsKey(BLUE_PRINT_ID_1));
		
		service.registerBluePrint(BLUE_PRINT_ID_2, bluePrint2);
		assertTrue(bluePrintMap.containsKey(BLUE_PRINT_ID_2));
		
		service.registerBluePrint(BLUE_PRINT_ID_3, bluePrint3);
		assertTrue(bluePrintMap.containsKey(BLUE_PRINT_ID_3));
	}
	
	@Test
	public void testUnRegisterBluePrint() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		service.registerBluePrint(BLUE_PRINT_ID_2, bluePrint2);
		service.registerBluePrint(BLUE_PRINT_ID_3, bluePrint3);
		
		assertEquals(3, bluePrintMap.size());
		service.unregisterBluePrint(BLUE_PRINT_ID_1);
		assertEquals(2, bluePrintMap.size());
		service.unregisterBluePrint(BLUE_PRINT_ID_2);
		assertEquals(1, bluePrintMap.size());
		service.unregisterBluePrint(BLUE_PRINT_ID_3);
		assertEquals(0, bluePrintMap.size());
	}
	
	@Test
	public void testAddDraggableToolBar() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		LocationParameters params = LocationProvider.first();
		String id = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Top);
		
		assertTrue(persistableToolBars.containsKey(id));
		Mockito.verify(toolBarService, Mockito.times(1)).addToolBar(toolbar1, params, DockPosition.Top, true);
	}
	
	@Test
	public void testAddNonDraggableToolBar() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		LocationParameters params = LocationProvider.first();
		String id = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Top, false);
		
		assertTrue(persistableToolBars.containsKey(id));
		Mockito.verify(toolBarService, Mockito.times(1)).addToolBar(toolbar1, params, DockPosition.Top, false);
	}
	
	@Test
	public void testAddToolBarBeforeRegistration() {
		boolean passed = false;
		LocationParameters params = LocationProvider.first();
		String id = new String();
		
		try {
			id = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Top, false);
		} catch (Exception e) {
			passed = true;
		}
		
		assertTrue(passed);
		assertTrue(!persistableToolBars.containsKey(id));
		Mockito.verify(toolBarService, Mockito.times(0)).addToolBar(toolbar1, params, DockPosition.Top, false);
	}
	
	@Test
	public void testGetPersistence() {
		DraggableToolBar dtb1 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb1.toolBar()).thenReturn(toolbar1);
		Mockito.when(dtb1.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		DraggableToolBar dtb2 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb2.toolBar()).thenReturn(toolbar2);
		Mockito.when(dtb2.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		DraggableToolBar dtb3 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb3.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		Mockito.when(dtb3.toolBar()).thenReturn(toolbar3);
		DraggableToolBar dtb4 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb4.toolBar()).thenReturn(toolbar4);
		Mockito.when(dtb4.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		
		ObservableList<DraggableToolBar> topToolBars = FXCollections.observableArrayList(dtb1);
		ObservableList<DraggableToolBar> bottomToolBars = FXCollections.observableArrayList(dtb2);
		ObservableList<DraggableToolBar> leftToolBars = FXCollections.observableArrayList(dtb3);
		ObservableList<DraggableToolBar> rightToolBars = FXCollections.observableArrayList(dtb4);
		
		Mockito.when(toolBarService.topToolBars()).thenReturn(topToolBars);
		Mockito.when(toolBarService.bottomToolBars()).thenReturn(bottomToolBars);
		Mockito.when(toolBarService.leftToolBars()).thenReturn(leftToolBars);
		Mockito.when(toolBarService.rightToolBars()).thenReturn(rightToolBars);
		
		persistableToolBars.put("ID1", new Tuple<String, ToolBar>(BLUE_PRINT_ID_1, toolbar1));
		persistableToolBars.put("ID2", new Tuple<String, ToolBar>(BLUE_PRINT_ID_2, toolbar2));
		persistableToolBars.put("ID3", new Tuple<String, ToolBar>(BLUE_PRINT_ID_3, toolbar3));
		persistableToolBars.put("ID4", new Tuple<String, ToolBar>(BLUE_PRINT_ID_1, toolbar4));
		
		service.savePersistence();
		ToolBarAreaPersistence persistence = service.getPersistence();
		
		assertEquals(persistence.getToolBars(DockPosition.Top).children().size(), 1);
		assertEquals(persistence.getToolBars(DockPosition.Bottom).children().size(), 1);
		assertEquals(persistence.getToolBars(DockPosition.Left).children().size(), 1);
		assertEquals(persistence.getToolBars(DockPosition.Right).children().size(), 1);
	}
	
	@Test
	public void testGetLayout() {
		DraggableToolBar dtb1 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb1.toolBar()).thenReturn(toolbar1);
		Mockito.when(dtb1.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		DraggableToolBar dtb2 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb2.toolBar()).thenReturn(toolbar2);
		Mockito.when(dtb2.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		DraggableToolBar dtb3 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb3.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		Mockito.when(dtb3.toolBar()).thenReturn(toolbar3);
		DraggableToolBar dtb4 = Mockito.mock(DraggableToolBar.class);
		Mockito.when(dtb4.toolBar()).thenReturn(toolbar4);
		Mockito.when(dtb4.draggableProperty()).thenReturn(new SimpleBooleanProperty(true));
		
		ObservableList<DraggableToolBar> topToolBars = FXCollections.observableArrayList(dtb1);
		ObservableList<DraggableToolBar> bottomToolBars = FXCollections.observableArrayList(dtb2);
		ObservableList<DraggableToolBar> leftToolBars = FXCollections.observableArrayList(dtb3);
		ObservableList<DraggableToolBar> rightToolBars = FXCollections.observableArrayList(dtb4);
		
		Mockito.when(toolBarService.topToolBars()).thenReturn(topToolBars);
		Mockito.when(toolBarService.bottomToolBars()).thenReturn(bottomToolBars);
		Mockito.when(toolBarService.leftToolBars()).thenReturn(leftToolBars);
		Mockito.when(toolBarService.rightToolBars()).thenReturn(rightToolBars);
		
		persistableToolBars.put("ID1", new Tuple<String, ToolBar>(BLUE_PRINT_ID_1, toolbar1));
		persistableToolBars.put("ID2", new Tuple<String, ToolBar>(BLUE_PRINT_ID_2, toolbar2));
		persistableToolBars.put("ID3", new Tuple<String, ToolBar>(BLUE_PRINT_ID_3, toolbar3));
		persistableToolBars.put("ID4", new Tuple<String, ToolBar>(BLUE_PRINT_ID_1, toolbar4));
		
		String layout = service.getLayout();
		assertEquals(EXPECTED_LAYOUT_STRING, layout);
	}
	
	@Test
	public void testLoadPersistence() {
		try {
			service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
			ToolBarAreaPersistence persistence = new ToolBarAreaPersistence();
			ToolBarPersistence toolBar = new ToolBarPersistence();
			toolBar.setID("ID1");
			toolBar.setDraggable(true);
			toolBar.setBluePrint(BLUE_PRINT_ID_1);
			persistence.addToolBar(toolBar, DockPosition.Top);
			
			service.loadLayout(persistence);
			assertTrue(persistableToolBars.containsKey("ID1"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testLoadLayout() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		service.registerBluePrint(BLUE_PRINT_ID_2, bluePrint2);
		service.registerBluePrint(BLUE_PRINT_ID_3, bluePrint3);
		
		service.loadLayout(EXPECTED_LAYOUT_STRING);
		
		assertTrue(persistableToolBars.containsKey("ID1"));
		assertTrue(persistableToolBars.containsKey("ID2"));
		assertTrue(persistableToolBars.containsKey("ID3"));
		assertTrue(persistableToolBars.containsKey("ID4"));
	}
	
	@Test
	public void testLoadLayoutWithoutRegisteredBluePrints() {
		try {
			ToolBarAreaPersistence persistence = new ToolBarAreaPersistence();
			ToolBarPersistence toolBar = new ToolBarPersistence();
			toolBar.setID("ID1");
			toolBar.setDraggable(true);
			toolBar.setBluePrint(BLUE_PRINT_ID_1);
			persistence.addToolBar(toolBar, DockPosition.Top);
			
			service.loadLayout(persistence);
			assertTrue(!persistableToolBars.containsKey("ID1"));
			assertTrue(toolBarDeferred(toolBar));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	private boolean toolBarDeferred(ToolBarPersistence toolBar) {
		for (Tuple<DockPosition, ToolBarPersistence> entry : deferredToolBars) {
			if (entry.value() == toolBar) {
				return true;
			}
		}
		
		return false;
	}
	
	@Test
	public void testRegisterBluePrintWithDeferredToolBars() {
		try {
			ToolBarAreaPersistence persistence = new ToolBarAreaPersistence();
			ToolBarPersistence toolBar = new ToolBarPersistence();
			toolBar.setID("ID1");
			toolBar.setDraggable(true);
			toolBar.setBluePrint(BLUE_PRINT_ID_1);
			persistence.addToolBar(toolBar, DockPosition.Top);
			
			service.loadLayout(persistence);
			service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
			assertTrue(persistableToolBars.containsKey("ID1"));
		} catch (Exception e) {
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testRemoveToolBar() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		LocationParameters params = LocationProvider.first();
		String id = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Top);

		service.removeToolBar(id);
		Mockito.verify(toolBarService, Mockito.times(1)).removeToolBar(toolbar1);
	}
	
	@Test
	public void testClearToolBars() {
		service.registerBluePrint(BLUE_PRINT_ID_1, bluePrint1);
		LocationParameters params = LocationProvider.first();
		String id1 = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Top);
		String id2 = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Left);
		String id3 = service.addToolBar(BLUE_PRINT_ID_1, params, DockPosition.Right);

		service.removeToolBar(id1);
		service.removeToolBar(id2);
		service.removeToolBar(id3);
		Mockito.verify(toolBarService, Mockito.times(3)).removeToolBar(toolbar1);
	}
}
