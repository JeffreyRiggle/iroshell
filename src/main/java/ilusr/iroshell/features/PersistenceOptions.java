package ilusr.iroshell.features;

/**
 * 
 * @author Jeff Riggle
 *
 */
public class PersistenceOptions {
	
	private String saveLocation;
	
	/**
	 * Base ctor.
	 */
	public PersistenceOptions() {
		saveLocation = new String();
	}
	
	/**
	 * 
	 * @return The location that persistence will be saved to.
	 */
	public String getSaveLocation() {
		return saveLocation;
	}
	
	/**
	 * 
	 * @param location The new location that persistence will be saved to.
	 */
	public void setSaveLocation(String location) {
		saveLocation = location;
	}
}
