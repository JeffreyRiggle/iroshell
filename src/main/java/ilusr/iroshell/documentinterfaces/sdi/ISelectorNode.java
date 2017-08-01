package ilusr.iroshell.documentinterfaces.sdi;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ISelectorNode {
	/**
	 * 
	 * @param model The @see SDIModel to use.
	 */
	void setModel(SDIModel model);
	/**
	 * 
	 * @return The current @see SDIModel.
	 */
	SDIModel getModel();
}
