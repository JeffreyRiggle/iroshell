package ilusr.iroshell.services;

import java.io.File;
import java.io.IOException;
import java.util.List;

import ilusr.core.interfaces.IDispose;


public interface IStyleContainerService extends IDispose{
	
	/**
	 * 
	 * @param id The name for the style.
	 * @param css The value for the style.
	 * @param type The @see RegistrationType that lets the service know how to handle conflicting styles.
	 */
	void register(String id, String css, RegistrationType type);
	
	/**
	 * 
	 * @param id The name for the style.
	 * @param css A @see File containing css to use.
	 * @param type The @see RegistrationType that lets the service know how to handle conflicting styles.
	 */
	void register(String id, File css, RegistrationType type) throws IllegalArgumentException, IOException;
	
	/**
	 * 
	 * @param ids The style names to watch.
	 * @param watcher The @see IStyleWatcher that will listen for these changes.
	 * @param merge If the style names should be merged or overriden.
	 */
	void startWatchStyle(List<String> ids, IStyleWatcher watcher, boolean merge);
	
	/**
	 * 
	 * @param watcher The @see IStyleWatcher that no longer wants style updates.
	 */
	void stopWatchingStyle(IStyleWatcher watcher);
	
	/**
	 * 
	 * @param id The style name.
	 * @return The css associated with the style name.
	 */
	String get(String id);
}
