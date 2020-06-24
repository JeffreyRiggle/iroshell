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
public class TabContentPersistence extends XmlConfigurationObject {
	private static final String CONTENT_TAB_NODE = "ContentTab";
	private static final String CONTENT_TAB_ID = "ID";
	private static final String CUSTOM_DATA = "CustomData";
	private static final String BLUE_PRINT_NAME = "BluePrintName";
	
	private XmlConfigurationObject id;
	private XmlConfigurationObject customData;
	private XmlConfigurationObject bluePrintName;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public TabContentPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public TabContentPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(CONTENT_TAB_NODE);
		
		id = new XmlConfigurationObject(CONTENT_TAB_ID, new String());
		customData = new XmlConfigurationObject(CUSTOM_DATA, new String());
		bluePrintName = new XmlConfigurationObject(BLUE_PRINT_NAME, new String());
		
		super.addChild(id);
		super.addChild(customData);
		super.addChild(bluePrintName);
	}
	
	/**
	 * 
	 * @return An identifier associated with this tab.
	 */
	public String getId() {
		return id.value();
	}
	
	/**
	 * 
	 * @param id An identifier to associated with this tab.
	 */
	public void setId(String id) {
		this.id.value(id);
	}
	
	/**
	 * 
	 * @return Custom data associated with this tab.
	 */
	public String getCustomData() {
		return customData.value();
	}
	
	/**
	 * 
	 * @param data Custom data to associate with this tab.
	 */
	public void setCustomData(String data) {
		customData.value(data);
	}
	
	/**
	 * 
	 * @return The blue print id associated with this tab.
	 */
	public String getBluePrintName() {
		return bluePrintName.value();
	}
	
	/**
	 * 
	 * @param bluePrint The blue print id to associate with this tab.
	 */
	public void setBluePrintName(String bluePrint) {
		bluePrintName.value(bluePrint);
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case CONTENT_TAB_ID:
					setId(cChild.<String>value());
					break;
				case CUSTOM_DATA:
					setCustomData(cChild.<String>value());
					break;
				case BLUE_PRINT_NAME:
					setBluePrintName(cChild.<String>value());
					break;
				default:
					break;
			}
		}
	}
}
