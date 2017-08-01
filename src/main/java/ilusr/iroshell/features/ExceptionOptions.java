package ilusr.iroshell.features;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ExceptionOptions {

	private boolean logException;
	private ExceptionDisplay displayType;
	private ErrorDialogProvider provider;
	
	/**
	 * 
	 * @param log If exceptions should be logged or not.
	 */
	public void logException(boolean log) {
		logException = log;
	}
	
	/**
	 * 
	 * @return If exceptions should be logged or not.
	 */
	public boolean logException() {
		return logException;
	}
	
	/**
	 * 
	 * @param type A @see ExceptionDisplay indicating what should be done visually when errors occur.
	 */
	public void displayType(ExceptionDisplay type) {
		displayType = type;
	}
	
	/**
	 * 
	 * @return A @see ExceptionDisplay indicating what should be done visually when errors occur.
	 */
	public ExceptionDisplay displayType() {
		return displayType;
	}
	
	/**
	 * 
	 * @param provider @see ErrorDialogProvider used to create error dialogs.
	 */
	public void errorDialogCreator(ErrorDialogProvider provider) {
		this.provider = provider;
	}
	
	/**
	 * 
	 * @return @see ErrorDialogProvider used to create error dialogs.
	 */
	public ErrorDialogProvider errorProvider() {
		return provider;
	}
}
