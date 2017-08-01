package ilusr.iroshell.documentinterfaces.mdi;

import ilusr.iroshell.core.IViewProvider;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class MDIProvider implements IViewProvider<MultipleDocumentInterface>{

	private final Object mdiLock;
	private MultipleDocumentInterface mdi;
	
	/**
	 * Base Ctor.
	 */
	public MDIProvider() {
		mdiLock = new Object();
	}
	
	@Override
	public MultipleDocumentInterface getView() {
		synchronized(mdiLock) {
			if (mdi == null) {
				mdi = new MultipleDocumentInterface();
			}
			
			return mdi;
		}
	}
}
