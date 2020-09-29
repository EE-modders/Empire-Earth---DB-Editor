package datmanager;

/**
 * Settings of the editor
 *
 * @author MarcoForlini
 */
public class Settings {

	/** The editor version */
	public static final String VERSION = "2.1.5";
	
	/** The editor database version */
	public static final String base_DBVersion = "2020.09.29";
	public static final String AoC_DBVersion = "2020.06.06";
	
	/** Enable/disable the debug mode */
	public static final boolean DEBUG = true;

	/** Max time (milliseconds) it will wait for loading to complete. If time exceed this value, the load is considered failed. */
	public static final int LOAD_MAX_WAIT = 10000;

}
