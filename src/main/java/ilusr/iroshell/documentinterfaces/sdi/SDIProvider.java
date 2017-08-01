package ilusr.iroshell.documentinterfaces.sdi;

import ilusr.iroshell.core.IViewProvider;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class SDIProvider implements IViewProvider<SingleDocumentInterface> {

	private final Object sdiLock;
	private SingleDocumentInterface sdi;
	
	/**
	 * Base Ctor.
	 */
	public SDIProvider() {
		sdiLock = new Object();
	}
	
	@Override
	public SingleDocumentInterface getView() {
		synchronized(sdiLock) {
			if (sdi == null) {
				sdi = new SingleDocumentInterface();
			}
			
			return sdi;
		}
	}
}
