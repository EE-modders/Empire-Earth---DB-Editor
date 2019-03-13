package datstructure.structures;

import java.io.IOException;
import java.util.List;

import datstructure.DatStructure;
import datstructure.Entry;


/**
 * Represents the file dbrandommap.dat
 *
 * @author MarcoForlini
 */
public class RandomMap extends DatStructure {

	/**
	 * Unique instance of this structure
	 */
	public static final RandomMap instance = new RandomMap();

	/**
	 * Creates a new {@link RandomMap}
	 * WARNING: This file has a structure like this: <num groups>, <num entries>, {list of entries...}
	 * Each entry specify the ID of the group it belongs to.
	 * When loading, <num groups> (the first 4 bytes) is discarded, as it has no use in the editor and must not be managed by the user anyway.
	 * When saving, <num groups> is recalculated as the number of distinct groups IDs.
	 */
	private RandomMap() {
		super("Random map", "dbrandommap.dat", true, 0, 0, 0, 2, -1, 0, -1, 2, 125, 175);
	}

	@Override
	public void customInit() throws IOException {
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
