package ilusr.iroshell.main;

import ilusr.core.ioc.ServiceManager;
import ilusr.core.javafx.splashscreen.SplashScreenModel;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class LoadData {
	
	private ServiceManager serviceManager;
	private SplashScreenModel splashModel;
	
	/**
	 * 
	 * @param manager A @see ServiceManager used to find and register services.
	 * @param model A @see SplashScreenModel used to update the splash screen.
	 */
	public LoadData(ServiceManager manager, SplashScreenModel model) {
		serviceManager = manager;
		splashModel = model;
	}
	
	/**
	 * 
	 * @return A @see ServiceManager used to find and register services.
	 */
	public ServiceManager serviceManager() {
		return serviceManager;
	}
	
	/**
	 * 
	 * @return A @see SplashScreenModel used to update the splash screen.
	 */
	public SplashScreenModel splashModel() {
		return splashModel;
	}
}
