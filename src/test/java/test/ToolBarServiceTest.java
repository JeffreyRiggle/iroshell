package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.*;

import ilusr.core.test.JavaFXRule;
import ilusr.core.url.InternalURLProvider;
import ilusr.iroshell.core.DockPosition;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.services.IToolBarService;
import ilusr.iroshell.services.StyleContainerService;
import ilusr.iroshell.services.ToolBarService;
import ilusr.iroshell.toolbar.DraggableToolBar;
import javafx.collections.ObservableList;
import javafx.scene.control.ToolBar;

public class ToolBarServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	
	private StyleContainerService styleService;
	private InternalURLProvider urlProvider;
	
	private ToolBar toolbar1;
	private ToolBar toolbar2;
	private ToolBar toolbar3;
	private ToolBar toolbar4;
	
	@Before
	public void setupStyle() {
		styleService = Mockito.mock(StyleContainerService.class);
		urlProvider = Mockito.mock(InternalURLProvider.class);
		Mockito.when(urlProvider.prepareURL(new String(), "dtbstylesheet.css")).thenReturn("");
	}
	
	@Before
	public void setupToolBars() {
		toolbar1 = new ToolBar();
		toolbar1.idProperty().set("Tool1");
		
		toolbar2 = new ToolBar();
		toolbar2.idProperty().set("Tool2");
		
		toolbar3 = new ToolBar();
		toolbar3.idProperty().set("Tool3");
		
		toolbar4 = new ToolBar();
		toolbar4.idProperty().set("Tool4");
	}
	
	@Test
	public void testCreate() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		assertNotNull(service);
		assertNotNull(service.topToolBars());
		assertNotNull(service.bottomToolBars());
		assertNotNull(service.leftToolBars());
		assertNotNull(service.rightToolBars());
	}
	
	@Test
	public void testAddToolBarTop() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.first(), DockPosition.Top);
		service.addToolBar(toolbar2, LocationProvider.after("Tool1"), DockPosition.Top);
		service.addToolBar(toolbar3, LocationProvider.before("Tool2"), DockPosition.Top);
		service.addToolBar(toolbar4, LocationProvider.index(0), DockPosition.Top);
		
		assertEquals(service.topToolBars().get(0).toolBar(), toolbar4);
		assertEquals(service.topToolBars().get(1).toolBar(), toolbar1);
		assertEquals(service.topToolBars().get(2).toolBar(), toolbar3);
		assertEquals(service.topToolBars().get(3).toolBar(), toolbar2);
	}
	
	@Test
	public void testAddToolBarBottom() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.first(), DockPosition.Bottom);
		service.addToolBar(toolbar2, LocationProvider.after("Tool1"), DockPosition.Bottom);
		service.addToolBar(toolbar3, LocationProvider.before("Tool2"), DockPosition.Bottom);
		service.addToolBar(toolbar4, LocationProvider.index(0), DockPosition.Bottom);
		
		assertEquals(service.bottomToolBars().get(0).toolBar(), toolbar4);
		assertEquals(service.bottomToolBars().get(1).toolBar(), toolbar1);
		assertEquals(service.bottomToolBars().get(2).toolBar(), toolbar3);
		assertEquals(service.bottomToolBars().get(3).toolBar(), toolbar2);
	}
	
	@Test
	public void testAddToolBarLeft() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.first(), DockPosition.Left);
		service.addToolBar(toolbar2, LocationProvider.after("Tool1"), DockPosition.Left);
		service.addToolBar(toolbar3, LocationProvider.before("Tool2"), DockPosition.Left);
		service.addToolBar(toolbar4, LocationProvider.index(0), DockPosition.Left);
		
		assertEquals(service.leftToolBars().get(0).toolBar(), toolbar4);
		assertEquals(service.leftToolBars().get(1).toolBar(), toolbar1);
		assertEquals(service.leftToolBars().get(2).toolBar(), toolbar3);
		assertEquals(service.leftToolBars().get(3).toolBar(), toolbar2);
	}
	
	@Test
	public void testAddToolBarRight() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.first(), DockPosition.Right);
		service.addToolBar(toolbar2, LocationProvider.after("Tool1"), DockPosition.Right);
		service.addToolBar(toolbar3, LocationProvider.before("Tool2"), DockPosition.Right);
		service.addToolBar(toolbar4, LocationProvider.index(0), DockPosition.Right);
		
		assertEquals(service.rightToolBars().get(0).toolBar(), toolbar4);
		assertEquals(service.rightToolBars().get(1).toolBar(), toolbar1);
		assertEquals(service.rightToolBars().get(2).toolBar(), toolbar3);
		assertEquals(service.rightToolBars().get(3).toolBar(), toolbar2);
	}
	
	@Test
	public void testRemoveToolBarTop() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.last(), DockPosition.Top);
		service.addToolBar(toolbar2, LocationProvider.last(), DockPosition.Top);
		service.addToolBar(toolbar3, LocationProvider.last(), DockPosition.Top);
		service.addToolBar(toolbar4, LocationProvider.last(), DockPosition.Top);
		
		assertTrue(containsToolBar(service.topToolBars(), toolbar1));
		assertTrue(containsToolBar(service.topToolBars(), toolbar2));
		assertTrue(containsToolBar(service.topToolBars(), toolbar3));
		assertTrue(containsToolBar(service.topToolBars(), toolbar4));
		
		service.removeToolBar(toolbar1);
		assertTrue(!containsToolBar(service.topToolBars(), toolbar1));
		
		service.removeToolBar(toolbar2);
		assertTrue(!containsToolBar(service.topToolBars(), toolbar2));
		
		service.removeToolBar(toolbar3);
		assertTrue(!containsToolBar(service.topToolBars(), toolbar3));
		
		service.removeToolBar(toolbar4);
		assertTrue(!containsToolBar(service.topToolBars(), toolbar4));
	}
	
	@Test
	public void testRemoveToolBarBottom() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.last(), DockPosition.Bottom);
		service.addToolBar(toolbar2, LocationProvider.last(), DockPosition.Bottom);
		service.addToolBar(toolbar3, LocationProvider.last(), DockPosition.Bottom);
		service.addToolBar(toolbar4, LocationProvider.last(), DockPosition.Bottom);
		
		assertTrue(containsToolBar(service.bottomToolBars(), toolbar1));
		assertTrue(containsToolBar(service.bottomToolBars(), toolbar2));
		assertTrue(containsToolBar(service.bottomToolBars(), toolbar3));
		assertTrue(containsToolBar(service.bottomToolBars(), toolbar4));
		
		service.removeToolBar(toolbar1);
		assertTrue(!containsToolBar(service.bottomToolBars(), toolbar1));
		
		service.removeToolBar(toolbar2);
		assertTrue(!containsToolBar(service.bottomToolBars(), toolbar2));
		
		service.removeToolBar(toolbar3);
		assertTrue(!containsToolBar(service.bottomToolBars(), toolbar3));
		
		service.removeToolBar(toolbar4);
		assertTrue(!containsToolBar(service.bottomToolBars(), toolbar4));
	}
	
	@Test
	public void testRemoveToolBarLeft() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.last(), DockPosition.Left);
		service.addToolBar(toolbar2, LocationProvider.last(), DockPosition.Left);
		service.addToolBar(toolbar3, LocationProvider.last(), DockPosition.Left);
		service.addToolBar(toolbar4, LocationProvider.last(), DockPosition.Left);
		
		assertTrue(containsToolBar(service.leftToolBars(), toolbar1));
		assertTrue(containsToolBar(service.leftToolBars(), toolbar2));
		assertTrue(containsToolBar(service.leftToolBars(), toolbar3));
		assertTrue(containsToolBar(service.leftToolBars(), toolbar4));
		
		service.removeToolBar(toolbar1);
		assertTrue(!containsToolBar(service.leftToolBars(), toolbar1));
		
		service.removeToolBar(toolbar2);
		assertTrue(!containsToolBar(service.leftToolBars(), toolbar2));
		
		service.removeToolBar(toolbar3);
		assertTrue(!containsToolBar(service.leftToolBars(), toolbar3));
		
		service.removeToolBar(toolbar4);
		assertTrue(!containsToolBar(service.leftToolBars(), toolbar4));
	}
	
	@Test
	public void testRemoveToolBarRight() {
		IToolBarService service = new ToolBarService(urlProvider, styleService);
		
		service.addToolBar(toolbar1, LocationProvider.last(), DockPosition.Right);
		service.addToolBar(toolbar2, LocationProvider.last(), DockPosition.Right);
		service.addToolBar(toolbar3, LocationProvider.last(), DockPosition.Right);
		service.addToolBar(toolbar4, LocationProvider.last(), DockPosition.Right);
		
		assertTrue(containsToolBar(service.rightToolBars(), toolbar1));
		assertTrue(containsToolBar(service.rightToolBars(), toolbar2));
		assertTrue(containsToolBar(service.rightToolBars(), toolbar3));
		assertTrue(containsToolBar(service.rightToolBars(), toolbar4));
		
		service.removeToolBar(toolbar1);
		assertTrue(!containsToolBar(service.rightToolBars(), toolbar1));
		
		service.removeToolBar(toolbar2);
		assertTrue(!containsToolBar(service.rightToolBars(), toolbar2));
		
		service.removeToolBar(toolbar3);
		assertTrue(!containsToolBar(service.rightToolBars(), toolbar3));
		
		service.removeToolBar(toolbar4);
		assertTrue(!containsToolBar(service.rightToolBars(), toolbar4));
	}
	
	private boolean containsToolBar(ObservableList<DraggableToolBar> list, ToolBar tool) {
		boolean retVal = false;
		
		for (DraggableToolBar dtb : list) {
			if (dtb.toolBar().equals(tool)) {
				retVal = true;
				break;
			}
		}
		
		return retVal;
	}
}
