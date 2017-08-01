package ilusr.iroshell.persistence;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationObject;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class MDIPersistence extends XmlConfigurationObject {

	private static final String MDI_NODE = "MDI";
	
	private DockAreaPersistence dockPersistence;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public MDIPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public MDIPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(MDI_NODE);
		dockPersistence = new DockAreaPersistence();
	}
	
	/**
	 * 
	 * @return The @see DockAreaPersistence associated with this document.
	 */
	public DockAreaPersistence getDockArea() {
		return dockPersistence;
	}
	
	/**
	 * 
	 * @param dockArea The @see DockAreaPersistence associated with this document.
	 */
	public void setDockArea(DockAreaPersistence dockArea) {
		this.children().clear();
		dockPersistence = dockArea;
		this.addChild(dockArea);
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			try {
				setDockArea(new DockAreaPersistence(cChild));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
