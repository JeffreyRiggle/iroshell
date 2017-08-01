package ilusr.iroshell.persistence;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.iroshell.core.DockPosition;
import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationObject;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class ToolBarAreaPersistence extends XmlConfigurationObject {

	private static final String TOOLBARS_PERSISTENCE_NODE = "ToolBars";
	private static final String LEFT_TOOLBARS = "Left";
	private static final String TOP_TOOLBARS = "Top";
	private static final String RIGHT_TOOLBARS = "Right";
	private static final String BOTTOM_TOOLBARS = "Bottom";
	
	private XmlConfigurationObject leftToolBars;
	private XmlConfigurationObject topToolBars;
	private XmlConfigurationObject rightToolBars;
	private XmlConfigurationObject bottomToolBars;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public ToolBarAreaPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public ToolBarAreaPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(TOOLBARS_PERSISTENCE_NODE);
		
		leftToolBars = new XmlConfigurationObject(LEFT_TOOLBARS);
		topToolBars = new XmlConfigurationObject(TOP_TOOLBARS);
		rightToolBars = new XmlConfigurationObject(RIGHT_TOOLBARS);
		bottomToolBars = new XmlConfigurationObject(BOTTOM_TOOLBARS);
		
		super.addChild(leftToolBars);
		super.addChild(topToolBars);
		super.addChild(rightToolBars);
		super.addChild(bottomToolBars);
	}
	
	/**
	 * 
	 * @param toolBar A @see ToolBarPersistence to add to a tooling area.
	 * @param pos A @see DockPosition to associate this toolbar with.
	 */
	public void addToolBar(ToolBarPersistence toolBar, DockPosition pos) {
		switch (pos)
		{
			case Left:
				addToolBarImpl(toolBar, leftToolBars);
				break;
			case Top:
				addToolBarImpl(toolBar, topToolBars);
				break;
			case Right:
				addToolBarImpl(toolBar, rightToolBars);
				break;
			case Bottom:
				addToolBarImpl(toolBar, bottomToolBars);
				break;
		}
	}
	
	private void addToolBarImpl(ToolBarPersistence toolBar, XmlConfigurationObject config) {
		config.addChild(toolBar);
	}
	
	/**
	 * 
	 * @param toolBar A @see ToolBarPersistence object to remove from a tooling area.
	 * @param pos A @see DockPosition to remove this toolbar from.
	 */
	public void removeToolBar(ToolBarPersistence toolBar, DockPosition pos) {
		switch (pos)
		{
			case Left:
				removeToolBarImpl(toolBar, leftToolBars);
				break;
			case Top:
				removeToolBarImpl(toolBar, topToolBars);
				break;
			case Right:
				removeToolBarImpl(toolBar, rightToolBars);
				break;
			case Bottom:
				removeToolBarImpl(toolBar, bottomToolBars);
				break;
		}
	}
	
	/**
	 * 
	 * @param pos A @see DockPosition to get toolbars from.
	 * @return All toolbars associated with the provided DockPosition.
	 */
	public XmlConfigurationObject getToolBars(DockPosition pos) {
		switch (pos)
		{
			case Left:
				return leftToolBars;
			case Top:
				return topToolBars;
			case Right:
				return rightToolBars;
			case Bottom:
				return bottomToolBars;
		}
		
		return null;
	}
	
	private void removeToolBarImpl(ToolBarPersistence toolBar, XmlConfigurationObject config) {
		config.removeChild(toolBar);
	}
	
	/**
	 * 
	 * @param pos A @see DockPosition to clear toolbars from.
	 */
	public void clearToolBars(DockPosition pos) {
		switch (pos)
		{
			case Left:
				clearToolBarImpl(leftToolBars);
				break;
			case Top:
				clearToolBarImpl(topToolBars);
				break;
			case Right:
				clearToolBarImpl(rightToolBars);
				break;
			case Bottom:
				clearToolBarImpl(bottomToolBars);
				break;
		}
	}
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case LEFT_TOOLBARS:
					convertToolArea(cChild, leftToolBars);
					break;
				case RIGHT_TOOLBARS:
					convertToolArea(cChild, rightToolBars);
					break;
				case TOP_TOOLBARS:
					convertToolArea(cChild, topToolBars);
					break;
				case BOTTOM_TOOLBARS:
					convertToolArea(cChild, bottomToolBars);
					break;
				default:
					break;
			}
		}
	}
	
	private void convertToolArea(XmlConfigurationObject config, XmlConfigurationObject area) {
		for (PersistXml child : config.children()) {
			try {
				XmlConfigurationObject cChild = (XmlConfigurationObject)child;
				area.addChild(new ToolBarPersistence(cChild));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void clearToolBarImpl(XmlConfigurationObject config) {
		config.clearChildren();
	}
}
