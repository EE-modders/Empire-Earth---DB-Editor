package EEmodders.datmanager;

/**
 * Settings of the editor
 *
 * @author MarcoForlini
 */
public class Settings {

	public static final String NAME = "Empire Earth - DB Editor";
	/** The editor version */
	public static final String VERSION = "3.0-beta";
	public static final String VERSION_YEAR = "2016 - 2024";
	
	/** The editor database version */
	public static final String base_DBVersion = "2020.12.21";
	public static final String AoC_DBVersion = "2020.06.06";
	
	/** Enable/disable the debug mode */
	public static final boolean DEBUG = true;

	/** Max time (milliseconds) it will wait for loading to complete. If time exceed this value, the load is considered failed. */
	// FIXME: this is fucking stupid, we need proper error handling
	public static final int LOAD_MAX_WAIT = 10000;

	public static final String DB_ICON = "DBE_icon.png";
	public static final String DB_ICON_CREDIT = "Icon by Fortuking"; // (King rocks balls!!)
	public static final String EES_URL = "https://github.com/EE-modders/Empire-Earth-Studio-2";
}
