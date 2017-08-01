package ilusr.iroshell.services;

import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;

import ilusr.iroshell.documentinterfaces.DocumentType;
import ilusr.iroshell.documentinterfaces.sdi.SelectorNode;
import ilusr.iroshell.persistence.MDIPersistence;
import ilusr.iroshell.persistence.SDIPersistence;
import javafx.beans.property.SimpleObjectProperty;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ILayoutService {
	/**
	 * Registers a blue print. This blue print can be used later with {@link ILayoutService#addTab(String)}
	 * 
	 * @param bluePrintName The unique identifier for this blue print.
	 * @param bluePrint A @see ITabContentBluePrint that will be used to create tabs.
	 */
	void registerBluePrint(String bluePrintName, ITabContentBluePrint bluePrint);
	/**
	 * Unregisters a blue print.
	 * 
	 * @param bluePrintName The unique identifier for this blue print.
	 */
	void unregisterBluePrint(String bluePrintName);
	/**
	 * This can be used to see what document state the application is in.
	 * 
	 * @return A SimpleObjectProperty of @see DocumentType.
	 */
	SimpleObjectProperty<DocumentType> documentType();
	/**
	 * 
	 * @param node A @see Node to set as the selector node.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#SDI}
	 */
	void setSelector(SelectorNode node) throws IllegalStateException;
	/**
	 * Adds a tab to the document area based off of a blueprint id.
	 * 
	 * @param bluePrintName The blueprint to use in order to create this tab.
	 * @return A unique identifier for the tab that was just created.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	String addTab(String bluePrintName) throws IllegalArgumentException;
	/**
	 * Adds a tab with custom data to the document area based off of a blueprint id.
	 * 
	 * @param bluePrintName The blueprint to use in order to create this tab.
	 * @param customData The custom data to create this tab with.
	 * @return A unique identifier for the tab that was just created.
	 * @throws IllegalArgumentException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	String addTab(String bluePrintName, String customData) throws IllegalArgumentException;
	/**
	 * Adds a tab to the document area.
	 * 
	 * @param tab A @see ITabContent to add to the document.
	 */
	void addTab(ITabContent tab);
	/***
	 * Gets the @see ITabContent for a tab id.
	 * @param id The id of the tab.
	 * @return The @see ITabContent for a tab id.
	 */
	ITabContent getTabContent(String id);
	/**
	 * Gets all tabs that are registered to a specific blue print name.
	 * 
	 * @param bluePrintName The blue print name to check for tab ids.
	 * @return a List of tab ids associated with the passed blue print name.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	List<String> getTabIds(String bluePrintName) throws IllegalStateException;
	/**
	 * Removes a tab from the MDI document.
	 * @param tabId The unique ID for the tab to remove.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	void removeTab(String tabId) throws IllegalStateException;
	/**
	 * Removes a tab from the MDI document.
	 * @param tabContent The @see ITabContent to remove.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	void removeTab(ITabContent tabContent) throws IllegalStateException;
	/**
	 * Removes all tabs from the MDI document.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	void removeAllTabs() throws IllegalStateException;
	/**
	 * 
	 * @return Gets a String representation of the document area. This can be used with {@link ILayoutService#loadLayout(String)} to restore a layout.
	 */
	String getLayout();
	/**
	 * 
	 * @return A @see MDIPersistence object representing the state of the document area.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	MDIPersistence getMDIPersistenceModel() throws IllegalStateException, TransformerConfigurationException, ParserConfigurationException;
	/**
	 * 
	 * @return A @see SDIPersistence object representing the state of the document area.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#SDI}
	 * @throws TransformerConfigurationException
	 * @throws ParserConfigurationException
	 */
	SDIPersistence getSDIPersistenceModel() throws IllegalStateException, TransformerConfigurationException, ParserConfigurationException;
	/**
	 * Saves the current document area.
	 */
	void savePersistence();
	/**
	 * Restores a document area to a previous state based off of a @see MDIPersistence object.
	 * 
	 * @param persistence The document state to restore.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#MDI}
	 */
	void loadPersistenceModel(MDIPersistence persistence) throws IllegalStateException;
	/**
	 * Restores a document area to a previous state based off of a @see SDIPersistence object.
	 * 
	 * @param persistence The document state to restore.
	 * @throws IllegalStateException This will throw an IllegalStateException when the application is not in {@link DocumentType#SDI}
	 */
	void loadPersistenceModel(SDIPersistence persistence) throws IllegalStateException;
	/**
	 * Restores a document area to a previous state based off of a String.
	 * 
	 * @param layoutData A layout string to restore.
	 */
	void loadLayout(String layoutData);
}
