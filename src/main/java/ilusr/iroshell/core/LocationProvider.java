package ilusr.iroshell.core;

//Helper class
/**
 * 
 * @author Jeff Riggle
 *
 */
public class LocationProvider {

	/**
	 * 
	 * @return A @see LocationParameters configured to the first element.
	 */
	public static LocationParameters first() {
		return new LocationParameters(LocationType.First);
	}
	
	/**
	 * 
	 * @return A @see LocationParameters configured to the last element.
	 */
	public static LocationParameters last() {
		return new LocationParameters(LocationType.Last);
	}
	
	/**
	 * 
	 * @param name The name of the object to follow.
	 * @return A @see LocationParameters that reprents following the name parameter.
	 */
	public static LocationParameters after(String name) {
		return new LocationParameters(LocationType.AfterName, name);
	}
	
	/**
	 * 
	 * @param name The name of the object to go before.
	 * @return A @see LocationParameters that reprents before the name parameter.
	 */
	public static LocationParameters before(String name) {
		return new LocationParameters(LocationType.BeforeName, name);
	}
	
	/**
	 * 
	 * @param index The index to use.
	 * @return A @see LocationParameters that has a specific index.
	 */
	public static LocationParameters index(int index) {
		return new LocationParameters(LocationType.Index, index);
	}
}
