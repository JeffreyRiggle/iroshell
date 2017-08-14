package ilusr.iroshell.features;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.Thread.UncaughtExceptionHandler;

import ilusr.iroshell.services.IDialogService;
import ilusr.logrunner.LogRunner;
import javafx.application.Platform;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ExceptionHandler implements UncaughtExceptionHandler{

	private final IDialogService dialogService;
	private final ErrorDialogProvider dialogProvider;
	private final boolean shouldLog;
	
	/**
	 * 
	 * @param service A @see IDialogService used to show error dialogs.
	 * @param provider A @see ErrorDialogProvider used to create error dialogs.
	 * @param shouldLog If the error should be logged out or not.
	 */
	public ExceptionHandler(IDialogService service, ErrorDialogProvider provider, boolean shouldLog) {
		dialogService = service;
		dialogProvider = provider;
		this.shouldLog = shouldLog;
	}
	
	@Override
	public void uncaughtException(Thread t, Throwable e) {
		if (shouldLog) {
			LogRunner.logger().severe(String.format("%s\n%s", e.getMessage(), getStackString(e)));
		}

		if (dialogProvider != null) {
			Platform.runLater(() -> {
				dialogService.displayModal(dialogProvider.create(e));
			});
		}
	}
	
	private String getStackString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		e.printStackTrace(printer);
		return writer.toString();
	}
}
