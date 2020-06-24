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
public class TabPanelPersistence extends XmlConfigurationObject {

	private static final String TAB_PANEL_NODE = "TabPanel";
	private static final String TABS = "Tabs";
	private static final String SELECTED_TAB = "SelectedTab";
	
	private XmlConfigurationObject tabs;
	private XmlConfigurationObject selectedTab;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public TabPanelPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public TabPanelPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(TAB_PANEL_NODE);

		selectedTab = new XmlConfigurationObject(SELECTED_TAB, 0);
		tabs = new XmlConfigurationObject(TABS);

		super.addChild(selectedTab);
		super.addChild(tabs);
	}
	
	/**
	 * 
	 * @param tab A @see TabContentPersistence to add to this tab panel.
	 */
	public void addTab(TabContentPersistence tab) {
		tabs.addChild(tab);
	}
	
	/**
	 * 
	 * @param tab A @see TabContentPersistence to remove from this tab panel.
	 */
	public void removeTab(TabContentPersistence tab) {
		tabs.removeChild(tab);
	}
	
	/**
	 * 
	 * @return The tabs associated with this tab panel.
	 */
	public XmlConfigurationObject getTabs() {
		return tabs;
	}
	
	/**
	 * 
	 * @return The index of the selected tab.
	 */
	public int selectedTab() {
		return selectedTab.value();
	}
	
	/**
	 * 
	 * @param index The index of the selected tab.
	 */
	public void selectedTab(int index) {
		selectedTab.value(index);
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case TABS:
					convertTabs(cChild);
					break;
				case SELECTED_TAB:
					selectedTab(cChild.<Integer>value());
					break;
				default:
					break;
			}
		}
	}
	
	private void convertTabs(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			try {
				XmlConfigurationObject cChild = (XmlConfigurationObject)child;
				TabContentPersistence tabContent = new TabContentPersistence(cChild);
				addTab(tabContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
