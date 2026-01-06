import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import ilusr.iroshell.core.ApplicationClosingListener;
import ilusr.iroshell.services.ApplicationClosingManager;

public class ApplicationClosingManagerTest {

	private ApplicationClosingManager manager;
	private List<ApplicationClosingListener> listeners;
	
	private ApplicationClosingListener listener;
	
	@Before
	public void setupTest() {
		listeners = new ArrayList<ApplicationClosingListener>();
		manager = new ApplicationClosingManager(listeners);
		
		listener = Mockito.mock(ApplicationClosingListener.class);
	}
	
	@Test
	public void testCreate() {
		assertNotNull(manager);
	}
	
	@Test
	public void testAddListener() {
		manager.addApplicationClosingListener(listener);
		assertTrue(listeners.contains(listener));
	}
	
	@Test
	public void testRemoveListener() {
		manager.addApplicationClosingListener(listener);
		assertTrue(listeners.contains(listener));
		manager.removeApplicationClosingListener(listener);
		assertFalse(listeners.contains(listener));
	}
	
	@Test
	public void testClose() {
		manager.addApplicationClosingListener(listener);
		manager.onClose();
		Mockito.verify(listener, Mockito.times(1)).onClose();
	}
}
