package EEmodders.database;

public class DBValue<T> {
    private final T value;
    private final Type type;
    private final String name;
    private final String description;

    public DBValue(T value, Type type, String name, String description) {
        this.value = value;
        this.type = type;
        this.name = name;
        this.description = description;
    }

    public T getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return type + " | " + name + " | " + value + " | " + description;
    }

    public enum Type {
        STRING,
        INTEGER,
    }
}
