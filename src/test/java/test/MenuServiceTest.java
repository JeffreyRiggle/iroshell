import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ilusr.core.test.JavaFXRule;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.menus.IMenuService;
import ilusr.iroshell.menus.MenuService;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;

public class MenuServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	
	private static Menu MENU1 = new Menu("Menu1");
	private static Menu MENU2 = new Menu("Menu2");
	private static Menu MENU3 = new Menu("Menu3");
	
	private static MenuItem ITEM1 = new MenuItem("Item1");
	private static MenuItem ITEM2 = new MenuItem("Item2");
	private static MenuItem ITEM3 = new MenuItem("Item3");
	
	private Menu menu4;
	
	@Before
	public void setupMenu4() {
		menu4 = new Menu("Menu4");
	}
	
	@Test
	public void testCreate() {
		IMenuService service = new MenuService();
		assertNotNull(service);
		assertNotNull(service.menus());
	}

	@Test
	public void testAddMenu() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		assertTrue(service.menus().contains(MENU1));
	}
	
	@Test
	public void testAddMenuToFront() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		service.addMenu(MENU2, LocationProvider.first());
		assertEquals(MENU2, service.menus().get(0));
	}
	
	@Test
	public void testAddMenuToEnd() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		service.addMenu(MENU2, LocationProvider.last());
		assertEquals(MENU2, service.menus().get(1));
	}
	
	@Test
	public void testAddMenuToIndex() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		service.addMenu(MENU2, LocationProvider.last());
		service.addMenu(MENU3, LocationProvider.index(1));
		assertEquals(MENU3, service.menus().get(1));
	}
	
	@Test
	public void testAddMenuAfterName() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		service.addMenu(MENU2, LocationProvider.last());
		service.addMenu(MENU3, LocationProvider.after("Menu1"));
		assertEquals(MENU3, service.menus().get(1));
	}
	
	@Test
	public void testAddMenuBeforeName() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		service.addMenu(MENU2, LocationProvider.last());
		service.addMenu(MENU3, LocationProvider.before("Menu2"));
		assertEquals(MENU3, service.menus().get(1));
	}
	
	@Test
	public void testRemoveMenuBar() {
		IMenuService service = new MenuService();
		service.addMenu(MENU1, LocationProvider.first());
		assertEquals(MENU1, service.menus().get(0));
		service.removeMenu(MENU1);
		assertFalse(service.menus().contains(MENU1));
	}
	
	@Test
	public void testAddMenuItem() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		assertTrue(menu4.getItems().contains(ITEM1));
	}
	
	@Test
	public void testAddMenuItemToFront() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		service.addMenuItem(ITEM2, menu4.getText(), LocationProvider.first());
		assertEquals(ITEM2, menu4.getItems().get(0));
	}
	
	@Test
	public void testAddMenuItemToEnd() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		service.addMenuItem(ITEM2, menu4.getText(), LocationProvider.last());
		assertEquals(ITEM2, menu4.getItems().get(1));
	}
	
	@Test
	public void testAddMenuItemToIndex() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		service.addMenuItem(ITEM2, menu4.getText(), LocationProvider.last());
		service.addMenuItem(ITEM3, menu4.getText(), LocationProvider.index(1));
		assertEquals(ITEM3, menu4.getItems().get(1));
	}
	
	@Test
	public void testAddMenuItemAfterName() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		service.addMenuItem(ITEM2, menu4.getText(), LocationProvider.last());
		service.addMenuItem(ITEM3, menu4.getText(), LocationProvider.after(ITEM1.getText()));		
		assertEquals(ITEM3, menu4.getItems().get(1));
	}
	
	@Test
	public void testAddMenuItemBeforeName() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		service.addMenuItem(ITEM2, menu4.getText(), LocationProvider.last());
		service.addMenuItem(ITEM3, menu4.getText(), LocationProvider.before(ITEM2.getText()));
		assertEquals(ITEM3, menu4.getItems().get(1));
	}
	
	@Test
	public void testRemoveMenuItem() {
		IMenuService service = new MenuService();
		service.addMenu(menu4, LocationProvider.first());
		service.addMenuItem(ITEM1, menu4.getText(), LocationProvider.first());
		assertTrue(menu4.getItems().contains(ITEM1));
		service.removeMenuItem(ITEM1, menu4.getText());
		assertFalse(menu4.getItems().contains(ITEM1));
	}
}
