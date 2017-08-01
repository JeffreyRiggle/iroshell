package ilusr.iroshell.documentinterfaces.sdi;

import javafx.scene.layout.AnchorPane;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class SelectorNode extends AnchorPane implements ISelectorNode {

	private SDIModel model;
	
	/**
	 * Base Ctor.
	 */
	public SelectorNode() {
		this(new SDIModel());
	}
	
	/**
	 * 
	 * @param model A @see SDIModel to use.
	 */
	public SelectorNode(SDIModel model) {
		this.model = model;
	}
	
	@Override
	public void setModel(SDIModel model) {
		this.model = model;
	}
	
	@Override	
	public SDIModel getModel() {
		return model;
	}
}
