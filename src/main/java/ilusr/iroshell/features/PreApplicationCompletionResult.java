package ilusr.iroshell.features;

import javafx.event.Event;
import javafx.event.EventType;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class PreApplicationCompletionResult extends Event{

	private boolean terminate;
	private static final EventType<PreApplicationCompletionResult> eventType = new EventType<PreApplicationCompletionResult>("OPTIONS_ALL");
	
	/**
	 * 
	 * @param terminate If the result should cause the application to exit or not.
	 */
	public PreApplicationCompletionResult(boolean terminate) {
		super(eventType);
		this.terminate = terminate;
	}
	
	/**
	 * 
	 * @return If the application should exit.
	 */
	public boolean shouldTerminate() {
		return terminate;
	}
	
	/**
	 * 
	 * @return The associated @see EventType for this event.
	 */
	public EventType<PreApplicationCompletionResult> eventType() {
		return eventType;
	}
}
