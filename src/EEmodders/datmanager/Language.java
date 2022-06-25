package EEmodders.datmanager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import EEmodders.gui.GUI;


/**
 * Represent a single entry in the hardcoded language files
 *
 * @author MarcoForlini
 */
public class Language implements Comparable<Language> {

	/** Vector of language entries */
	private static Vector<Language> LIST = new Vector<>(0);

	/** Map every language code to the relative language entry */
	private static Map<Integer, Language> MAP = new HashMap<>();


	/** Language ID */
	private final int ID;

	/** Language text */
	private final String text;


	public static void updateLanguages() {
		var languageFile = new File(Core.getDataDirectory(), "language.txt");

		try (var input = new BufferedReader(new FileReader(languageFile))) {
			String line;
			Language l;
			LIST.clear();

			while ( (line = input.readLine()) != null ) {
				l = Language.parseNew(line);
				LIST.add(l);
				MAP.put(l.getID(), l);
			}

		} catch (IOException | ParseException e) {
			JOptionPane.showMessageDialog(null,
					"An error occurred while reading the language file", "Language file",
					JOptionPane.WARNING_MESSAGE, GUI.IMAGE_ICON);
			Util.printException(null, e, true);
		}
	}

	/**
	 * Creates a new {@link Language} class from a line String (usually from "language.txt") separated by a comma
	 *
	 * @param line single line from "language.txt"
	 * @return {@link Language}
	 * @throws ParseException
	 */

	private static Language parseNew(String line) throws ParseException {
		String[] parts = line.split(",");

		if (parts.length < 2)
			throw new ParseException("Invalid language entry: \""+line+"\"", parts.length);

		int id = Integer.parseInt(parts[0]);

		// NOTE: this is not perfect, because of '\"' in the string, but good enough
		String text = parts[1].replace("\"", "").strip();

		//System.out.println("ID: "+id+" Text: "+text);

		return new Language(id, text);
	}

	public static Vector<Language> getList() { return LIST; }

	public static Map<Integer, Language> getMap() { return MAP; }

	/**
	 * Create a new language entry with the given code and text
	 *
	 * @param ID   The ID
	 * @param text The text
	 */
	public Language(int ID, String text) {
		this.ID = ID;
		this.text = text;
	}

	/**
	 * Create a new language entry with the given entry (ID, text)
	 *
	 * @param entry The entry (ID, text)
	 */
	public Language(Entry<Integer, String> entry) {
		ID = entry.getKey();
		text = entry.getValue();
	}

	public int getID() {
		return ID;
	}

	public String getText() {
		return text;
	}

	@Override
	public int compareTo(Language l) {
		return Integer.compare(ID, l.ID);
	}

	@Override
	public String toString() {
		return "(" + ID + ") " + text;
	}

}
