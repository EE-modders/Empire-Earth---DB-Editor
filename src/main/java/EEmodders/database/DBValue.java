package EEmodders.database;

import EEmodders.Utils.Util;
import com.google.common.io.LittleEndianDataInputStream;

import java.text.ParseException;

public class DBValue<T> {
    private final T value;
    private final Type type;
    private final String name;
    private final String description;

    public static DBValue<?> readFrom(LittleEndianDataInputStream inStream, Type type, String name, String description) throws Exception {
        return switch (type) {
            case STRING -> {
                byte[] chars = new byte[100];
                int n = inStream.read(chars);
                if (n != 100) {
                    throw new ParseException("String shorter than expected length", n);
                }

                yield new DBValue<>(Util.fromCString(chars), DBValue.Type.STRING, name, description);
            }
            case INTEGER -> new DBValue<>(inStream.readInt(), DBValue.Type.INTEGER, name, description);
            case FLOAT -> new DBValue<>(inStream.readFloat(), DBValue.Type.FLOAT, name, description);
            case BOOL -> {
                // 4 byte boolean
                boolean bool = inStream.readInt() > 0;
                yield new DBValue<>(bool, DBValue.Type.BOOL, name, description);
            }
            case BOOL8 -> /* 1 byte boolean */ new DBValue<>(inStream.readBoolean(), Type.BOOL8, name, description);
        };
    }

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
        FLOAT,
        BOOL,
        BOOL8,
    }
}
