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
public class SDIPersistence extends XmlConfigurationObject {

	private static final String SDI_NODE = "SDI";
	private static final String SELECTED_ITEM = "Selected";
	private static final String SELECTOR_POSITION = "SelectorPosition";
	private static final String DOCUMENTS_WIDTH = "DocumentsWidth";
	
	private XmlConfigurationObject selectedItem;
	private XmlConfigurationObject selectorPosition;
	private XmlConfigurationObject documentWidth;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public SDIPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public SDIPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(SDI_NODE);
		
		selectedItem = new XmlConfigurationObject(SELECTED_ITEM, "");
		selectorPosition = new XmlConfigurationObject(SELECTOR_POSITION, 0.0);
		documentWidth = new XmlConfigurationObject(DOCUMENTS_WIDTH, 0.0);
		
		super.addChild(selectedItem);
		super.addChild(selectorPosition);
		super.addChild(documentWidth);
	}
	
	/**
	 * 
	 * @param item The selected items id.
	 */
	public void setSelectedItem(String item) {
		selectedItem.value(item);
	}
	
	/**
	 * 
	 * @return The selected items id.
	 */
	public String getSelectedItem() {
		return selectedItem.value();
	}
	
	/**
	 * 
	 * @param pos The position of the divider.
	 */
	public void setSelectorPosition(double pos) {
		selectorPosition.value(pos);
	}
	
	/**
	 * 
	 * @return The position of the divider.
	 */
	public double getSelectorPosition() {
		return selectorPosition.value();
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case SELECTED_ITEM:
					setSelectedItem(cChild.<String>value());
					break;
				case SELECTOR_POSITION:
					setSelectorPosition(cChild.<Double>value());
					break;
				default:
					break;
			}
		}
	}
}
