package ilusr.iroshell.dockarea.overlay;

import java.util.ArrayList;
import java.util.List;

import ilusr.iroshell.services.IStyleContainerService;

public class DockOverlayModel {

	private List<IDropListener> listeners;
	private final IStyleContainerService styleService;
	
	public DockOverlayModel(IStyleContainerService styleService) {
		this.styleService = styleService;
		listeners = new ArrayList<IDropListener>();
	}
	
	public void addListener(IDropListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(IDropListener listener) {
		listeners.remove(listener);
	}
	
	public void invoke(DockPosition pos) {
		for (IDropListener listener : listeners) {
			listener.dragDropped(pos);
		}
	}
	
	public String previewStyle() {
		return styleService.get(OverlayStyleNames.PREVIEW_DROP);
	}
}
