package test;

import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Test;

import ilusr.iroshell.services.IStyleWatcher;
import ilusr.iroshell.services.RegistrationType;
import ilusr.iroshell.services.StyleContainerService;

public class StyleContainerServiceTest {

	private final String ID1 = "SomeObject";
	private final String ID2 = "SomeOtherObject";
	private final String ID3 = "LastObject";
	
	private final String ID1V1 = "-fx-backgroundcolor:green;";
	private final String ID1V2 = "-fx-backgroundcolor:blue;";
	
	private final String ID2V1 = "-fx-padding: 10px;";
	private final String ID2V2 = "-fx-padding: 20px";
	
	private final String ID3V3 = ".class{width:90;-fx-backgroundcolor:red;-fx-padding: 10px;}#id{width:80px;font-size:87;}-fx-backgroundcolor:blue;height:87;";
	private final String ID3V4 = ".class{-fx-backgroundcolor:blue;font-size:12;}#id{-fx-padding: 40px;}width:85px;-fx-backgroundcolor:green;";
	
	@Test
	public void testCreate() {
		StyleContainerService service = new StyleContainerService();
		assertNotNull(service);
	}

	@Test
	public void testRegisterStyle() {
		StyleContainerService service = new StyleContainerService();
		
		service.register(ID1, ID1V1, RegistrationType.AvoidConflict);
		assertEquals(ID1V1, service.get(ID1));
		
		service.register(ID1, ID1V2, RegistrationType.AvoidConflict);
		assertEquals(ID1V1, service.get(ID1));
	}
	
	@Test
	public void testOverrideStyle() {
		StyleContainerService service = new StyleContainerService();
		
		service.register(ID1, ID1V1, RegistrationType.Override);
		assertEquals(ID1V1, service.get(ID1));
		
		service.register(ID1, ID1V2, RegistrationType.Override);
		assertEquals(ID1V2, service.get(ID1));
	}
	
	@Test
	public void testMergeNoOverrideStyle() {
		StyleContainerService service = new StyleContainerService();
		
		service.register(ID3, ID3V3, RegistrationType.MergeWithoutOverride);
		assertEquals(ID3V3, service.get(ID3));
		
		service.register(ID3, ID3V4, RegistrationType.MergeWithoutOverride);
		//verify merge. The hard part is the maps are hash maps
	}
	
	@Test
	public void testMergeWithOverrideStyle() {
		StyleContainerService service = new StyleContainerService();
		
		service.register(ID3, ID3V3, RegistrationType.MergeWithOverride);
		assertEquals(ID3V3, service.get(ID3));
		
		service.register(ID3, ID3V4, RegistrationType.MergeWithOverride);
		//verify merge. The hard part is the maps are hash maps
	}
	
	@Test
	public void testStartWatch() {
		StyleContainerService service = new StyleContainerService();
		TestWatcher watcher = new TestWatcher();
		
		service.register(ID1, ID1V1, RegistrationType.AvoidConflict);
		service.startWatchStyle(Arrays.asList(ID1), watcher, true);
		assertEquals(1, watcher.count());
		
		service.register(ID1, ID1V2, RegistrationType.Override);
		assertEquals(2, watcher.count());
	}
	
	@Test
	public void testChangeWatchWithMerge() {
		StyleContainerService service = new StyleContainerService();
		TestWatcher watcher = new TestWatcher();
		
		service.register(ID1, ID1V1, RegistrationType.AvoidConflict);
		service.register(ID2, ID2V1, RegistrationType.AvoidConflict);
		
		service.startWatchStyle(Arrays.asList(ID1), watcher, true);
		assertEquals(1, watcher.count());
		service.startWatchStyle(Arrays.asList(ID2), watcher, true);
		assertEquals(2, watcher.count());
		
		service.register(ID1, ID1V2, RegistrationType.Override);
		assertEquals(3, watcher.count());
		
		service.register(ID2, ID2V2, RegistrationType.Override);
		assertEquals(4, watcher.count());
	}
	
	@Test
	public void testChangeWatchNoMerge() {
		StyleContainerService service = new StyleContainerService();
		TestWatcher watcher = new TestWatcher();
		
		service.register(ID1, ID1V1, RegistrationType.AvoidConflict);
		service.register(ID2, ID2V1, RegistrationType.AvoidConflict);
		
		service.startWatchStyle(Arrays.asList(ID1), watcher, false);
		assertEquals(1, watcher.count());
		service.startWatchStyle(Arrays.asList(ID2), watcher, false);
		assertEquals(2, watcher.count());
		
		service.register(ID1, ID1V2, RegistrationType.Override);
		assertEquals(2, watcher.count());
		
		service.register(ID2, ID2V2, RegistrationType.Override);
		assertEquals(3, watcher.count());
	}
	
	@Test
	public void testStopWatch() {
		StyleContainerService service = new StyleContainerService();
		TestWatcher watcher = new TestWatcher();
		
		service.register(ID1, ID1V1, RegistrationType.AvoidConflict);
		service.register(ID2, ID2V2, RegistrationType.AvoidConflict);
		
		service.startWatchStyle(Arrays.asList(ID1, ID2), watcher, false);
		assertEquals(2, watcher.count());
		
		service.stopWatchingStyle(watcher);
		
		service.register(ID1, ID1V2, RegistrationType.Override);
		assertEquals(2, watcher.count());
		
		service.register(ID2, ID2V2, RegistrationType.Override);
		assertEquals(2, watcher.count());
	}
	
	private class TestWatcher implements IStyleWatcher {
		
		private int _callCount;
		
		public TestWatcher() {
			_callCount = 0;
		}

		public int count() {
			return _callCount;
		}
		
		@Override
		public void update(String style, String css) {
			_callCount++;
		}
	}
}
