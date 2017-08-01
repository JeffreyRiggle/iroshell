package ilusr.iroshell.services;

/**
 * 
 * @author Jeff Riggle
 *
 */
public interface ITabContentBluePrint {
	/**
	 * Creates a tab for reuse.
	 * @return A @see ITabContent.
	 */
	ITabContent create();
	/**
	 * 
	 * @param customData The custom data to use to create this tab.
	 * @return A @see ITabContent.
	 */
	ITabContent create(String customData);
}
