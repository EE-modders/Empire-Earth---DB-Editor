package EEmodders.datstructure.structures;

import java.io.IOException;
import java.util.List;

import EEmodders.Main;
import EEmodders.datstructure.DatStructure;
import EEmodders.datstructure.Entry;

/**
 * Represents the file dbobjects.dat
 *
 * @author MarcoForlini
 */
public class Objects extends DatStructure {

	/**
	 * Unique instance of this structure
	 */
	public static final Objects instance = new Objects();

	/**
	 * Creates a new {@link Objects}
	 */
	private Objects() {
		super("Objects", "dbobjects.dat", true, 0, 1, 0, 0, 1, 5, 88, 4, 125, 175);
	}

	@Override
	public void customInit() throws IOException {
		if (!Main.isAOC()) { // File structure has been changed in AOC
			newEntryValues = new Object[] {
					"<New object>", 0, -1, -1, 0, -1, 0, 0,
					0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
					0f, 0f, 0, 0, 0f, 0f, 0, -1,
					0, 0, 0, 0, 0, 0, 0, -1,
					-1, 0, 0f, "<Object type>", 0, 0, -1, -1,
					0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
					-1, -1, -1, -1, -1, -1, 0, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, 0, 0, 0f, 0, -1, 0f, 0,
					0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0f, -1, 0f,
					0, 0, 0, 0, 0, 0, 0, 204,
					0, 0, 0f, 0f, 0, 0f, 0f, 0,
					1, 204, 204, 0f, 0f, 0, 0, 0,
					0, 0, -1, 0, 0, 0, 0, 0,
					0, 0, 0, 0f, 1, 0, 0, 0,
					0, 0, 204, 204, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, 0f, 0, 0, 0, 0,
					1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 0,
					0, 0, 204, 204, -1, -1, -1, -1,
					0, 0, 0, 0f, 0, 0, 0, 0,
					0, 204, 0, 0, 0, 0, 0, 0,
					0, 0, 0, -1, 0, 0, 0, 0,
					0f, 0f, 0f, 0f, 0f, 0, 0, 0,
					0, -1, 1, 0, 0, 204, 0, 0,
					0, 0, 0, 0, 0f, 0f, 0f, 0f,
					0f, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
			};
		} else {
			newEntryValues = new Object[] {
					"<New object>", 0, -1, -1, 0, -1, 0, 0,
					0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
					0f, 0f, 0, 0, 0f, 0f, 0, -1,
					0, 0, 0, 0, 0, 0, 0, -1,
					-1, 0, 0f, "<Object type>", 0, 0, -1, -1,
					0f, 0f, 0f, 0f, 0f, 0f, 0f, 0f,
					-1, -1, -1, -1, -1, -1, 0, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, 0, 0, 0f, 0, -1, 0f, 0,
					0, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0, 0f, -1, 0f,
					0, 0, 0, 0, 0, 0, 0, 204,
					0, 0, 0f, 0f, 0, 0f, 0f, 0,
					1, 204, 204, 0f, 0f, 0, 0, 0,
					0, 0, -1, 0, 0, 0, 0, 0,
					0, 0, 0, 0f, 1, 0, 0, 0,
					0, 0, 204, 204, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, 0f, 0, 0, 0, 0,
					1, 1, 1, 1, 1, 1, 1, 1,
					1, 1, 1, 1, 1, 1, 1, 0,
					0, 0, 204, 204, -1, -1, -1, -1,
					0, 0, 0, 0f, 0, 0, 0, 0,
					0, 204, 0, 0, 0, 0, 0, 0,
					0, 0, 0, -1, 0, 0, 0, 0,
					0f, 0f, 0f, 0f, 0f, 0, 0, 0,
					0, -1, 1, 0, 0, 204, 0, 0,
					0, 0, 0, 0, 0f, 0f, 0f, 0f,
					0f, 0, 0, 0, 0, 0, 0, 0,
					0, 0, 0, 0, 0,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					-1, -1, -1, -1, -1, -1, -1, -1,
					0
			};
		}
	}

	@Override
	public int indexExtraFields() {
		return -1;
	}

	@Override
	public boolean hasCustomEntryName() {
		return false;
	}

	@Override
	public String getCustomEntryName(int index, List<Object> values) {
		return null;
	}

	@Override
	public String getEntryDescription(Entry entry) {
		return null;
	}

}
