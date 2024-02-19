package EEmodders.database;

import java.util.List;

public class DBRow {
    private final List<DBValue<?>> dbValues;

    public DBRow(List<DBValue<?>> values) {
        this.dbValues = values;
    }

    public List<DBValue<?>> getDBValues() {
        return dbValues;
    }

    public String getName() {
        return dbValues.stream()
                .filter(dbValue -> dbValue.getType() == DBValue.Type.STRING)
                .filter(dbValue -> dbValue.getName().equals("Name"))
                .map(dbValue -> (String) dbValue.getValue())
                .findFirst()
                .orElse("<undefined>");
    }

    public int getID() {
        return dbValues.stream()
                .filter(dbValue -> dbValue.getType() == DBValue.Type.INTEGER)
                .filter(dbValue -> dbValue.getName().equals("ID"))
                .map(dbValue -> (int) dbValue.getValue())
                .findFirst()
                .orElse(-1);
    }

    public String getDescription_() {
        return dbValues.stream()
                .filter(dbValue -> dbValue.getType() == DBValue.Type.STRING)
                .map(dbValue -> (String) dbValue.getValue())
                .findFirst()
                .orElse("<undefined>");
    }

    public String toStringLong() {
        var sb = new StringBuilder("DBRow{\n");

        for (var value : dbValues) {
            sb.append("   ").append(value).append("\n");
        }

        return sb.append("}").toString();
    }

    @Override
    public String toString() {
        return "(%s) %s".formatted(getID(), getName());
    }
}
