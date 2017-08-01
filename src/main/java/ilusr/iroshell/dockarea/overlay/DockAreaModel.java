package ilusr.iroshell.dockarea.overlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ilusr.iroshell.services.IStyleContainerService;
import ilusr.iroshell.services.IStyleWatcher;

public class DockAreaModel implements IStyleWatcher {

	private List<IDragListener> dragListeners;
	private List<IStyleListener> styleListeners;
	private DockPosition position;
	private final IStyleContainerService styleService;
	
	public DockAreaModel(IStyleContainerService styleContainer) {
		styleService = styleContainer;
		dragListeners = new ArrayList<IDragListener>();
		styleListeners = new ArrayList<IStyleListener>();
		
		styleService.startWatchStyle(Arrays.asList(OverlayStyleNames.DEFAULT_ARROW, OverlayStyleNames.HOVER_ARROW), this, false);
	}
	
	public DockAreaModel(DockPosition pos, IStyleContainerService styleContainer) {
		position = pos;
		styleService = styleContainer;
		dragListeners = new ArrayList<IDragListener>();
		styleListeners = new ArrayList<IStyleListener>();
		
		styleService.startWatchStyle(Arrays.asList(OverlayStyleNames.DEFAULT_ARROW, OverlayStyleNames.HOVER_ARROW), this, false);
	}

	public void addListener(IDragListener listener) {
		dragListeners.add(listener);
	}
	
	public void addListener(IStyleListener listener) {
		styleListeners.add(listener);
	}
	
	public void removeListener(IDragListener listener) {
		dragListeners.remove(listener);
	}
	
	public void removeListener(IStyleListener listener) {
		styleListeners.remove(listener);
	}
	
	public void setPosition(DockPosition position) {
		this.position = position;
	}
	
	public DockPosition position() {
		return position;
	}
	
	public String arrowStyle() {
		return styleService.get(OverlayStyleNames.DEFAULT_ARROW);
	}
	
	public String hoverArrowStyle() {
		return styleService.get(OverlayStyleNames.HOVER_ARROW);
	}
	
	public void invoke(DraggableEvent event) {
		switch (event) {
			case Entered:
				invokeEntered();
				break;
			case Exited:
				invokeExited();
				break;
			case Dropped:
				invokeDropped();
				break;
		}
	}
	
	private void invokeEntered() {
		for (IDragListener listener : dragListeners) {
			listener.dragEntered(position);
		}
	}
	
	private void invokeExited() {
		for (IDragListener listener : dragListeners) {
			listener.dragExited(position);
		}
	}
	
	private void invokeDropped() {
		for (IDragListener listener : dragListeners) {
			listener.dragDropped(position);
		}
	}

	@Override
	public void update(String style, String css) {
		for (IStyleListener listener : styleListeners) {
			listener.changed();
		}
	}
}
