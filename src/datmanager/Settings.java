package datmanager;

/**
 * Settings of the editor
 *
 * @author MarcoForlini
 */
public class Settings {

	/** The editor version */
	public static final String VERSION = "2.1c";
	
	/** The editor database version */
	public static final String DBVersion = "2020.01.14";
	
	/** Enable/disable the debug mode */
	public static final boolean DEBUG = true;

	/** Max time (milliseconds) it will wait for loading to complete. If time exceed this value, the load is considered failed. */
	public static final int LOAD_MAX_WAIT = 10000;

}
