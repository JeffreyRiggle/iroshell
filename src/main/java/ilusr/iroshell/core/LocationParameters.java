package ilusr.iroshell.core;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class LocationParameters {

	private int index;
	private String locationName;
	private LocationType type;
	
	/**
	 * 
	 * @param type The @see LocationType to use.
	 */
	public LocationParameters(LocationType type) {
		this.type = type;
	}
	
	/**
	 * 
	 * @param type The @see LocationType to use.
	 * @param index The index.
	 */
	public LocationParameters(LocationType type, int index) {
		this.type = type;
		this.index = index;
	}
	
	/**
	 * 
	 * @param type The @see LocationType to use.
	 * @param name The name to use.
	 */
	public LocationParameters(LocationType type, String name) {
		this.type = type;
		this.locationName = name;
	}
	
	/**
	 * 
	 * @return A @see LocationType associated with these parameters.
	 */
	public LocationType type() {
		return type;
	}
	
	/**
	 * 
	 * @return A name associated with these parameters.
	 */
	public String locationName() {
		return locationName;
	}
	
	/**
	 * 
	 * @return A index associated with these parameters.
	 */
	public int index() {
		return index;
	}
	
	@Override
	public String toString() {
		String n = "N/A";
		
		if (locationName != null) {
			n = locationName;
		}
		
		return String.format("Type: %s, Name: %s, Index: %s", type, n, index);
	}
}
