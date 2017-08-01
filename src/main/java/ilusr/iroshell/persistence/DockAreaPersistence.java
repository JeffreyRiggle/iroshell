package ilusr.iroshell.persistence;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.persistencelib.configuration.PersistXml;
import ilusr.persistencelib.configuration.XmlConfigurationObject;
import javafx.geometry.Orientation;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class DockAreaPersistence extends XmlConfigurationObject {
	
	private static final String DOCK_AREA = "DockArea";
	private static final String WIDTH = "Width";
	private static final String HEIGHT = "Height";
	private static final String INDEX = "Index";
	private static final String ORIENTATION = "Orientation";
	
	private XmlConfigurationObject width;
	private XmlConfigurationObject height;
	private XmlConfigurationObject index;
	private XmlConfigurationObject orientation;
	private TabPanelPersistence tab;
	private DockAreaPersistence childDock;
	
	/**
	 * 
	 * @param config A @see XmlConfigurationObject to restore.
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public DockAreaPersistence(XmlConfigurationObject config) throws TransformerConfigurationException, ParserConfigurationException {
		this();
		convertFromPersistence(config);
	}
	
	/**
	 * 
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	public DockAreaPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		super(DOCK_AREA);

		width = new XmlConfigurationObject(WIDTH, 0.0);
		height = new XmlConfigurationObject(HEIGHT, 0.0);
		index = new XmlConfigurationObject(INDEX, 0);
		orientation = new XmlConfigurationObject(ORIENTATION, Orientation.HORIZONTAL);
		tab = new TabPanelPersistence();
		
		super.addChild(width);
		super.addChild(height);
		super.addChild(index);
		super.addChild(orientation);
		super.addChild(tab);
	}
	
	/**
	 * 
	 * @return The width of the dock area.
	 */
	public double getWidth() {
		return width.value();
	}
	
	/**
	 * 
	 * @param width The width of the dock area.
	 */
	public void setWidth(double width) {
		this.width.value(width);
	}
	
	/**
	 * 
	 * @return The height of the dock area.
	 */
	public double getHeight() {
		return height.value();
	}
	
	/**
	 * 
	 * @param height The height of the dock area.
	 */
	public void setHeight(double height) {
		this.height.value(height);
	}
	
	/**
	 * 
	 * @return The index of the dock area relative to its children.
	 */
	public int getIndex() {
		return index.value();
	}
	
	/**
	 * 
	 * @param index The index of the dock area relative to its children.
	 */
	public void setIndex(int index) {
		this.index.value(index);
	}
	
	/**
	 * 
	 * @return The @see Orientation of this dock area and its child.
	 */
	public Orientation getOrientation() {
		return this.orientation.value();
	}
	
	/**
	 * 
	 * @param orientation The @see Orientation of this dock area and its child.
	 */
	public void setOrientation(Orientation orientation) {
		this.orientation.value(orientation);
	}
	
	/**
	 * 
	 * @return The @see TabPanelPersistence associated with this dock area.
	 */
	public TabPanelPersistence getTabPanel() {
		return tab;
	}
	
	/**
	 * 
	 * @param tabPanel The @see TabPanelPersistence associated with this dock area.
	 */
	public void setTabPanel(TabPanelPersistence tabPanel) {
		super.removeChild(tab);
		tab = tabPanel;
		super.addChild(tab);
	}
	
	/**
	 * 
	 * @param childDock The child dock area for this dock area.
	 */
	public void setChildDock(DockAreaPersistence childDock) {
		super.removeChild(this.childDock);
		this.childDock = childDock;
		super.addChild(this.childDock);
	}
	
	/**
	 * 
	 * @return The child dock area for this dock area.
	 */
	public DockAreaPersistence getChildDock() {
		return childDock;
	}
	
	/**
	 * 
	 * @param config The @see XmlConfigurationObject to restore.
	 */
	public void convertFromPersistence(XmlConfigurationObject config) {
		for (PersistXml child : config.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case WIDTH:
					setWidth(cChild.<Double>value());
					break;
				case HEIGHT:
					setHeight(cChild.<Double>value());
					break;
				case INDEX:
					setIndex(cChild.<Integer>value());
					break;
				case ORIENTATION:
					setOrientation(convertOrientation(cChild.<String>value()));
					break;
				case "TabPanel":
					convertTab(cChild);
					break;
				case DOCK_AREA:
					convertDockChild(cChild);
				default:
					break;
			}
		}
	}
	
	private Orientation convertOrientation(String value) {
		if (value.equals("HORIZONTAL")) {
			return Orientation.HORIZONTAL;
		}
		
		return Orientation.VERTICAL;
	}
	
	private void convertTab(XmlConfigurationObject config) {
		try {
			setTabPanel(new TabPanelPersistence(config));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void convertDockChild(XmlConfigurationObject config) {
		try {
			setChildDock(new DockAreaPersistence(config));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
