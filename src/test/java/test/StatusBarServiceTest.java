package test;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import ilusr.core.test.JavaFXRule;
import ilusr.iroshell.core.LocationProvider;
import ilusr.iroshell.statusbar.StatusBarService;
import javafx.scene.control.Label;

public class StatusBarServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	private Label _testLabel;
	private Label _testLabel2;
	private Label _testLabel3;
	
	@Before
	public void labelSetup() {
		_testLabel = new Label("Test");
		_testLabel2 = new Label("Test2");
		_testLabel3 = new Label("Test3");
	}
	
	@Test
	public void testCreate() {
		StatusBarService service = new StatusBarService();
		assertNotNull(service);
		assertNotNull(service.statusBars());
	}

	@Test
	public void testAddStatusBarAtBeginning() {
		StatusBarService service = new StatusBarService();
		service.addStatusBar(_testLabel, LocationProvider.first());
		assertEquals(_testLabel, service.statusBars().get(0));
		service.addStatusBar(_testLabel2, LocationProvider.first());
		assertEquals(_testLabel2, service.statusBars().get(0));
	}
	
	@Test
	public void testAddStatusBarToEnd() {
		StatusBarService service = new StatusBarService();
		service.addStatusBar(_testLabel, LocationProvider.last());
		assertEquals(_testLabel, service.statusBars().get(0));
		service.addStatusBar(_testLabel2, LocationProvider.last());
		assertEquals(_testLabel2, service.statusBars().get(1));
	}
	
	@Test
	public void testAddStatusBarToIndex() {
		StatusBarService service = new StatusBarService();
		service.addStatusBar(_testLabel, LocationProvider.last());
		service.addStatusBar(_testLabel2, LocationProvider.last());
		service.addStatusBar(_testLabel3, LocationProvider.index(1));
		assertEquals(_testLabel3, service.statusBars().get(1));
	}
	
	@Test
	public void testAddStatusBarBeforeName() {
		boolean threw = false;
		StatusBarService service = new StatusBarService();
		
		try {
			service.addStatusBar(_testLabel, LocationProvider.before("Test"));
		} catch (Exception e) {
			threw = true;
		}
		
		assertTrue(threw);
	}
	
	@Test
	public void testAddStatusBarAfterName() {
		boolean threw = false;
		StatusBarService service = new StatusBarService();
		
		try {
			service.addStatusBar(_testLabel, LocationProvider.after("Test"));
		} catch (Exception e) {
			threw = true;
		}
		
		assertTrue(threw);
	}
	
	@Test
	public void testRemoveStatusBar() {
		StatusBarService service = new StatusBarService();
		service.addStatusBar(_testLabel, LocationProvider.last());
		service.addStatusBar(_testLabel2, LocationProvider.last());
		service.addStatusBar(_testLabel3, LocationProvider.index(1));
		
		service.removeStatusBar(_testLabel);
		assertFalse(service.statusBars().contains(_testLabel));
		service.removeStatusBar(_testLabel2);
		assertFalse(service.statusBars().contains(_testLabel2));
		service.removeStatusBar(_testLabel3);
		assertFalse(service.statusBars().contains(_testLabel3));
	}
}
