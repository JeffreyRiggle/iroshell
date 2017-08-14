package ilusr.iroshell.services;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ilusr.logrunner.LogRunner;


/**
 * 
 * @author Jeff Riggle
 *
 */
public class StyleContainerService implements IStyleContainerService {

	private Map<String, String> styleMap;
	private Map<IStyleWatcher, List<String>> styleWatchers;
	private final Object watcherLock = new Object();
	private final Object styleLock = new Object();
	private final CSSInterperter interperter;
	
	/**
	 * Base ctor
	 */
	public StyleContainerService() {
		interperter = new CSSInterperter();
		styleMap = new HashMap<String, String>();
		styleWatchers = new HashMap<IStyleWatcher, List<String>>();
	}
	
	@Override
	public void register(String id, String css, RegistrationType type) {
		registerImpl(id, css, type);
	}
	
	@Override
	public void register(String id, File css, RegistrationType type) throws IllegalArgumentException, IOException {
		synchronized(styleLock) {
			interperter.setCSS(css);
			interperter.interpertCSS();
			String cssData = interperter.getCSSString();
			registerImpl(id, cssData, type);
		}
	}
	
	private void registerImpl(String id, String css, RegistrationType type) {
		LogRunner.logger().info(String.format("Registering style: %s, with type %s", id, type));
		switch (type) {
			case Override:
				override(id, css);
				break;
			case MergeWithOverride:
				mergeAndOverride(id, css);
				break;
			case MergeWithoutOverride:
				mergeNoOverride(id, css);
				break;
			case AvoidConflict:
				tryAdd(id, css);
				break;
			default:
				break;
		}
	}
	
	private void override(String id, String css) {
		synchronized(styleLock) {
			if (tryAdd(id, css)) return;
		
			styleMap.replace(id, css);
		}
		
		notifyWatchers(id, css);
	}
	
	private void mergeAndOverride(String id, String css) {
		synchronized(styleLock) {
			if (tryAdd(id, css)) return;
		
			CSSInterperter originalCss = new CSSInterperter(styleMap.get(id));
			originalCss.interpertCSS();
			interperter.setCSS(css);
			interperter.interpertCSS();
			
			mergeWithOverrideProperties(originalCss.unparentedMap(), interperter.unparentedMap());
			
			for (Entry<String, Map<String, String>> ids : interperter.idMap().entrySet()) {
				if (originalCss.idMap().containsKey(ids.getKey())) {
					mergeWithOverrideProperties(originalCss.idMap().get(ids.getKey()), ids.getValue());
					originalCss.idMap().put(ids.getKey(), originalCss.idMap().get(ids.getKey()));
					continue;
				}
				
				originalCss.idMap().put(ids.getKey(), ids.getValue());
			}
			
			for (Entry<String, Map<String, String>> classes : interperter.classMap().entrySet()) {
				if (originalCss.classMap().containsKey(classes.getKey())) {
					mergeWithOverrideProperties(originalCss.classMap().get(classes.getKey()), classes.getValue());
					originalCss.classMap().put(classes.getKey(), originalCss.classMap().get(classes.getKey()));
					continue;
				}
				
				originalCss.classMap().put(classes.getKey(), classes.getValue());
			}
			
			styleMap.put(id, originalCss.getCSSString());
		}
		
		notifyWatchers(id, css);
	}
	
	private void mergeNoOverride(String id, String css) {
		synchronized(styleLock) {
			if (tryAdd(id, css)) return;
		
			CSSInterperter originalCss = new CSSInterperter(styleMap.get(id));
			originalCss.interpertCSS();
			interperter.setCSS(css);
			interperter.interpertCSS();
			
			mergeNoOverrideProperties(originalCss.unparentedMap(), interperter.unparentedMap());
			
			for (Entry<String, Map<String, String>> ids : interperter.idMap().entrySet()) {
				if (originalCss.idMap().containsKey(ids.getKey())) {
					mergeNoOverrideProperties(originalCss.idMap().get(ids.getKey()), ids.getValue());
					originalCss.idMap().put(ids.getKey(), originalCss.idMap().get(ids.getKey()));
					continue;
				}
				
				originalCss.idMap().put(ids.getKey(), ids.getValue());
			}
			
			for (Entry<String, Map<String, String>> classes : interperter.classMap().entrySet()) {
				if (originalCss.classMap().containsKey(classes.getKey())) {
					mergeNoOverrideProperties(originalCss.classMap().get(classes.getKey()), classes.getValue());
					originalCss.classMap().put(classes.getKey(), originalCss.classMap().get(classes.getKey()));
					continue;
				}
				
				originalCss.classMap().put(classes.getKey(), classes.getValue());
			}
			
			styleMap.put(id, originalCss.getCSSString());
		}
		
		notifyWatchers(id, css);
	}
	
	private void mergeNoOverrideProperties(Map<String, String> original, Map<String, String> merger) {
		for (Entry<String, String> props : merger.entrySet()) {
			if (original.containsKey(props.getKey())) continue;
			
			original.put(props.getKey(), props.getValue());
		}
	}
	
	private void mergeWithOverrideProperties(Map<String, String> original, Map<String, String> merger) {
		for (Entry<String, String> props : merger.entrySet()) {
			original.put(props.getKey(), props.getValue());
		}
	}
	
	private boolean tryAdd(String id, String css) {
		synchronized(styleLock) {
			if (styleMap.containsKey(id)) {
				LogRunner.logger().info(String.format("Not adding style: %s, since it already exits.", id));
				return false;
			}
		
			styleMap.put(id, css);
		}
		
		notifyWatchers(id, css);
		return true;
	}
	
	private void notifyWatchers(String id, String css) {
		List<IStyleWatcher> interestedParties = new ArrayList<IStyleWatcher>();
		synchronized (styleWatchers) {
			for (Entry<IStyleWatcher, List<String>> entry : styleWatchers.entrySet()) {
				if (!entry.getValue().contains(id)) continue;
				
				interestedParties.add(entry.getKey());
			}
		}
		
		LogRunner.logger().info(String.format("Notifying watcher about update to: %s", id));
		for (IStyleWatcher watcher : interestedParties) {
			watcher.update(id, css);
		}
	}
	
	@Override
	public void startWatchStyle(List<String> ids, IStyleWatcher watcher, boolean merge) {
		synchronized(watcherLock) {
			if (!styleWatchers.containsKey(watcher) || !merge) {
				styleWatchers.put(watcher, ids);
				startWatchNotifications(ids, watcher);
				return;
			}
		
			List<String> oldStyles = new ArrayList<String>(styleWatchers.get(watcher));
			oldStyles.addAll(ids);
			styleWatchers.replace(watcher, oldStyles);
			startWatchNotifications(ids, watcher);
		}
	}
	
	private void startWatchNotifications(List<String> ids, IStyleWatcher watcher) {
		Map<String, String> initalValues = new HashMap<String, String>();
		synchronized (styleLock) {
			for(Entry<String, String> entry : styleMap.entrySet()) {
				if (!ids.contains(entry.getKey())) continue;
				
				initalValues.put(entry.getKey(), entry.getValue());
			}
		}
		
		for(Entry<String, String> entry : initalValues.entrySet()) {
			watcher.update(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public void stopWatchingStyle(IStyleWatcher watcher) {
		synchronized(watcherLock) {
			styleWatchers.remove(watcher);
		}
	}
	
	@Override
	public String get(String id) {
		synchronized(styleLock) {
			return styleMap.get(id);
		}
	}

	@Override
	public void dispose() {
		synchronized(styleLock) {
			styleMap.clear();
		}
		
		synchronized(watcherLock) {
			styleWatchers.clear();
		}
	}
}
