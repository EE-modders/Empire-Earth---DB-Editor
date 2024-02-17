package EEmodders.database;

import EEmodders.Utils.Util;
import com.google.common.io.LittleEndianDataInputStream;

import javax.naming.NameNotFoundException;
import java.io.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class DBTable {

    private final DBMapping dbMapping;
    private final String name;
    private final String filename;
    private final File dbFolder;

    private List<DBRow> dbRows = null;

    private boolean saved = true;
    private boolean loaded = false;
    private boolean loadFailed = false;
    private Exception loadException = null;

    public DBTable(String name, String filename, File dbFolder, DBMapping dbMapping) {
        this.name = name;
        this.filename = filename;
        this.dbFolder = dbFolder;
        this.dbMapping = dbMapping;
    }

    public void load() {
        var dbFile = new File(dbFolder, filename);
        if (!dbFile.exists() || !dbFile.isFile()) {
            loaded = true;
            loadFailed = true;
            loadException = new FileNotFoundException(dbFile.toString());
            return;
        }

        try (var inStream = new LittleEndianDataInputStream(new BufferedInputStream(new FileInputStream(dbFile)))) {
            int numRows = inStream.readInt();

            List<DBRow> rows = new ArrayList<>(numRows);
            // NOTE: here is some potential for performance optimisation
            List<String[]> entries = getDBStructure();

            for (int i = 0; i < numRows; i++) {
                List<DBValue<?>> values = new ArrayList<>(entries.size());

                for (var entry : entries) {
                    DBValue<?> value = DBValue.readFrom(inStream, entry);
                    values.add(value);

                    System.out.printf("Read value: %s%n", value);
                }

                var r = new DBRow(values);

                System.out.printf("%d/%d: %s%n", i+1, numRows, r);
                rows.add(r);
            }

            dbRows = rows;
        } catch (Exception e) {
            loadFailed = true;
            loadException = e;

            e.printStackTrace();
        } finally {
            loaded = true;
        }
    }

    public void save() {
        // TODO
    }

    private List<String[]> getDBStructure() throws NameNotFoundException, ParseException {
        List<String> structure = dbMapping.getDBStructure(filename);
        List<String[]> parsedStructure = new ArrayList<>(structure.size());

        for (var s : structure) {
            var fields = s.split(";");
            if (fields.length < 1) {
                throw new ParseException("%s structure has no entries".formatted(filename), fields.length);
            }
            parsedStructure.add(fields);
        }

        return parsedStructure;
    }

    public String getName() {
        return name;
    }

    public String getFilename() {
        return filename;
    }

    public boolean isLoaded() {
        return loaded;
    }

    public boolean hasFailed() {
        return loadFailed;
    }

    public Exception getLoadException() {
        return loadException;
    }

    public List<DBRow> getDBRows() {
        return dbRows;
    }
}
