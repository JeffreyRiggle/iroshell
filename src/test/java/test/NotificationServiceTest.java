import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import ilusr.core.test.JavaFXRule;
import ilusr.iroshell.services.INotification;
import ilusr.iroshell.services.NotificationService;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

public class NotificationServiceTest {

	@Rule public JavaFXRule javafxRule = new JavaFXRule();
	
	private Map<INotification, Stage> shown;
	private List<INotification> queued;
	
	private NotificationService service;
	
	private INotification notify1;
	private INotification notify2;
	
	@Before
	public void setup() {
		shown = new LinkedHashMap<INotification, Stage>();
		queued = new ArrayList<INotification>();
		service = new NotificationService(shown, queued);
		
		notify1 = Mockito.mock(INotification.class);
		Mockito.when(notify1.title()).thenReturn("TestNotification");
		Mockito.when(notify1.parent()).thenReturn(new AnchorPane());
		
		notify2 = Mockito.mock(INotification.class);
		Mockito.when(notify2.title()).thenReturn("TestNotification2");
		Mockito.when(notify2.parent()).thenReturn(new AnchorPane());
	}
	
	@Test
	public void testCreate() {
		assertNotNull(service);
	}
	
	@Test
	public void testAddNotification() {
		service.addNotification(notify1);
		assertTrue(shown.containsKey(notify1));
	}
	
	@Test
	public void testAddNotificationWhenFull() {
		service.addNotification(notify1);
		assertTrue(shown.containsKey(notify1));
		service.addNotification(notify2);
		assertFalse(shown.containsKey(notify2));
		assertTrue(queued.contains(notify2));
	}
	
	@Test
	public void testRemoveNotification() {
		service.addNotification(notify1);
		assertTrue(shown.containsKey(notify1));
		service.removeNotification(notify1);
		assertFalse(shown.containsKey(notify1));
	}
	
	@Test
	public void testRemoveNotificationFromQueueWhenFull() {
		service.addNotification(notify1);
		service.addNotification(notify2);
		assertTrue(queued.contains(notify2));
		service.removeNotification(notify2);
		assertFalse(queued.contains(notify2));
	}
	
	@Test
	public void testRemoveNotificationFromVisableWhenFull() {
		service.addNotification(notify1);
		service.addNotification(notify2);
		assertTrue(shown.containsKey(notify1));
		service.removeNotification(notify1);
		assertFalse(shown.containsKey(notify1));
		assertFalse(queued.contains(notify2));
		assertTrue(shown.containsKey(notify2));
	}
	
	@Test
	public void testSetMaxNotificationsWithOverflow() {
		service.addNotification(notify1);
		service.addNotification(notify2);
		assertFalse(shown.containsKey(notify2));
		assertTrue(queued.contains(notify2));
		service.setMaxNotifications(2);
		assertTrue(shown.containsKey(notify2));
		assertFalse(queued.contains(notify2));		
	}
}
