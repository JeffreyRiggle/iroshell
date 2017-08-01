package ilusr.iroshell.services;

/**
 * 
 * @author Jeff Riggle
 *
 */
public enum RegistrationType {
	/**
	 * Override the registered value.
	 */
	Override,
	/**
	 * Merge preferring yours over the existing.
	 */
	MergeWithOverride,
	/**
	 * Merge preferring the existing over yours.
	 */
	MergeWithoutOverride,
	/**
	 * Only register if nothing has been registered.
	 */
	AvoidConflict
}
