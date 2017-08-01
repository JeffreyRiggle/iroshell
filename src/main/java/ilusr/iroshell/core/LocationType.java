package ilusr.iroshell.core;

/**
 * 
 * @author Jeff Riggle
 *
 */
public enum LocationType {
	/**
	 * At a specific location.
	 */
	Index,
	/**
	 * Before a named location.
	 */
	BeforeName,
	/**
	 * After a named location.
	 */
	AfterName,
	/**
	 * The first location.
	 */
	First,
	/**
	 * The last location.
	 */
	Last
}
