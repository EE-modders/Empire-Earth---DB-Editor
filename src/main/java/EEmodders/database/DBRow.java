package EEmodders.database;

import java.util.List;

public class DBRow {
    private final List<DBValue<?>> dbValues;

    public DBRow(List<DBValue<?>> values) {
        this.dbValues = values;
    }

    public List<DBValue<?>> getDbValues() {
        return dbValues;
    }

    @Override
    public String toString() {
        var sb = new StringBuilder("DBRow{\n");

        for (var value : dbValues) {
            sb.append("   ").append(value).append("\n");
        }

        return sb.append("}").toString();
    }
}
