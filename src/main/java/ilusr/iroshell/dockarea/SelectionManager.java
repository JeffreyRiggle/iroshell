package ilusr.iroshell.dockarea;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import ilusr.core.interfaces.Callback;
import ilusr.logrunner.LogRunner;

//TODO: SelectionManager<Requester extends ISelectable>
// Question is how will Spring work with this.
public class SelectionManager {

	private final Object requesterLock;
	private List<ISelectable> requesters;
	private List<Callback<ISelectable>> listeners;
	private ISelectable selected;
	
	public SelectionManager() {
		requesterLock = new Object();
		requesters = new ArrayList<ISelectable>();
		listeners = new ArrayList<Callback<ISelectable>>();
	}
	
	public void addSelectionListener(Callback<ISelectable> listener) {
		synchronized (requesterLock) {
			listeners.add(listener);
		}
	}
	
	public void removeSelectionListener(Callback<ISelectable> listener) {
		synchronized (requesterLock) {
			listeners.remove(listener);
		}
	}
	
	public void addSelectionRequester(ISelectable requester) {
		synchronized(requesterLock) {
			LogRunner.logger().log(Level.INFO, "Adding Selection Requester %s", requester);
			requesters.add(requester);
		}
	}
	
	public void removeSelectionRequester(ISelectable requester) {
		synchronized(requesterLock) {
			LogRunner.logger().log(Level.INFO, "Removing Selection Requester %s", requester);
			requesters.remove(requester);
		}
	}
	
	public void RequestSelection(ISelectable requester) {
		synchronized(requesterLock) {
			LogRunner.logger().log(Level.INFO, String.format("Requesting selection for: %s", requester));
			for (ISelectable request : requesters) {
				if (request == requester) {
					request.select();
					selected = request;
					continue;
				}
				
				request.deSelect();
			}
		}
		
		notifyListeners();
	}
	
	private void notifyListeners() {
		for (Callback<ISelectable> listener : listeners) {
			listener.execute(selected);
		}
	}
	
	public ISelectable selected() {
		return selected;
	}
}
