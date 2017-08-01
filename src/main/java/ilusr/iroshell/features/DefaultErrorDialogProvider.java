package ilusr.iroshell.features;

import java.io.PrintWriter;
import java.io.StringWriter;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class DefaultErrorDialogProvider implements ErrorDialogProvider {

	@Override
	public Stage create(Throwable e) {
		Stage s = new Stage();
		ErrorDialogModel model = new ErrorDialogModel(String.format("Message: %s\nStack Trace\n%s", e.getMessage(), getStackString(e)));
		s.setScene(new Scene(new ErrorDialogView(model)));
		s.setTitle("Application Error");
		return s;
	}
	
	private String getStackString(Throwable e) {
		StringWriter writer = new StringWriter();
		PrintWriter printer = new PrintWriter(writer);
		e.printStackTrace(printer);
		return writer.toString();
	}
}
