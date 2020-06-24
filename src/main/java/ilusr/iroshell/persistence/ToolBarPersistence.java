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
public class ToolBarPersistence extends XmlConfigurationObject {

	private static final String TOOLBAR_PERSISTENCE_NODE = "ToolBar";
	
	private static final String BLUE_PRINT_NAME = "BluePrint";
	private static final String ID = "id";
	private static final String DRAGGABLE = "Draggable";
	
	private XmlConfigurationObject bluePrint;
	private XmlConfigurationObject id;
	private XmlConfigurationObject draggable;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public ToolBarPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public ToolBarPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(TOOLBAR_PERSISTENCE_NODE);
		
		bluePrint = new XmlConfigurationObject(BLUE_PRINT_NAME, new String());
		id = new XmlConfigurationObject(ID, new String());
		draggable = new XmlConfigurationObject(DRAGGABLE, true);
		
		super.addChild(bluePrint);
		super.addChild(id);
		super.addChild(draggable);
	}
	
	/**
	 * 
	 * @param id The new id of the toolbar.
	 */
	public void setID(String id) {
		this.id.value(id);
	}
	
	/**
	 * 
	 * @return The id of the toolbar.
	 */
	public String getID() {
		return id.value();
	}
	
	/**
	 * 
	 * @param bluePrint A new blueprint id to associate with this toolbar.
	 */
	public void setBluePrint(String bluePrint) {
		this.bluePrint.value(bluePrint);
	}
	
	/**
	 * 
	 * @return A blueprint id to associate with this toolbar.
	 */
	public String getBluePrint() {
		return bluePrint.value();
	}
	
	/**
	 * 
	 * @param canDrag If the toolbar is draggable.
	 */
	public void setDraggable(boolean canDrag) {
		draggable.value(canDrag);
	}
	
	/**
	 * 
	 * @return If the toolbar is draggable.
	 */
	public boolean getDraggable() {
		return draggable.value();
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case BLUE_PRINT_NAME:
					setBluePrint(cChild.<String>value());
					break;
				case ID:
					setID(cChild.<String>value());
					break;
				case DRAGGABLE:
					setDraggable(cChild.<Boolean>value());
					break;
				default:
					break;
			}
		}
	}
}
