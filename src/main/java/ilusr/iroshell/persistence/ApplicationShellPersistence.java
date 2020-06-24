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
public class ApplicationShellPersistence extends XmlConfigurationObject {

	private static final String APPLICATION_PERSISTENCE_NODE = "ApplicationShell";
	private static final String APPLICATION_X = "x";
	private static final String APPLICATION_Y = "y";
	private static final String APPLICATION_WIDTH = "width";
	private static final String APPLICATION_HEIGHT = "height";
	
	private XmlConfigurationObject x;
	private XmlConfigurationObject y;
	private XmlConfigurationObject width;
	private XmlConfigurationObject height;
	
	/**
	 * 
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public ApplicationShellPersistence() throws TransformerConfigurationException, ParserConfigurationException {
		this(0,0,0,0);
	}
	
	/**
	 * 
	 * @param x The x location of the application.
	 * @param y The y location of the application.
	 * @param width the width of the application.
	 * @param height the height of the application.
	 * @throws TransformerConfigurationException Throws a transformer exception when xml cannot be transformed.
	 * @throws ParserConfigurationException Throws a parser exception when xml is malformed.
	 */
	public ApplicationShellPersistence(double x, double y, double width, double height) throws TransformerConfigurationException, ParserConfigurationException {
		super(APPLICATION_PERSISTENCE_NODE);
		this.x = new XmlConfigurationObject(APPLICATION_X, x);
		this.y = new XmlConfigurationObject(APPLICATION_Y, y);
		this.width = new XmlConfigurationObject(APPLICATION_WIDTH, width);
		this.height = new XmlConfigurationObject(APPLICATION_HEIGHT, height);
		
		super.addChild(this.x);
		super.addChild(this.y);
		super.addChild(this.width);
		super.addChild(this.height);
	}
	
	/**
	 * 
	 * @param x The new x location for the application.
	 */
	public void setX(double x) {
		this.x.value(x);
	}
	
	/**
	 * 
	 * @return The x location for this application.
	 */
	public double getX() {
		return x.value();
	}
	
	/**
	 * 
	 * @param y The new y location for this application.
	 */
	public void setY(double y) {
		this.y.value(y);
	}
	
	/**
	 * 
	 * @return The y location for this application.
	 */
	public double getY() {
		return y.value();
	}
	
	/**
	 * 
	 * @param width The new width for this application.
	 */
	public void setWidth(double width) {
		this.width.value(width);
	}
	
	/**
	 * 
	 * @return The width of this application.
	 */
	public double getWidth() {
		return width.value();
	}
	
	/**
	 * 
	 * @param height The new height of this application.
	 */
	public void setHeight(double height) {
		this.height.value(height);
	}
	
	/**
	 * 
	 * @return The height of this application.
	 */
	public double getHeight() {
		return height.value();
	}
	
	/**
	 * 
	 * @param obj The @see XmlConfigurationObject to use to recreate this object.
	 */
	public void convertFromPersistence(XmlConfigurationObject obj) {
		for (PersistXml child : obj.children()) {
			XmlConfigurationObject cChild = (XmlConfigurationObject)child;
			
			switch (cChild.name()) {
				case APPLICATION_X:
					setX(cChild.<Double>value());
					break;
				case APPLICATION_Y:
					setY(cChild.<Double>value());
					break;
				case APPLICATION_WIDTH:
					setWidth(cChild.<Double>value());
					break;
				case APPLICATION_HEIGHT:
					setHeight(cChild.<Double>value());
					break;
				default:
					break;
			}
		}
	}
}
