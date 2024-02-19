package EEmodders.database;

import EEmodders.Utils.Util;
import com.google.common.io.LittleEndianDataInputStream;

import javax.lang.model.type.NullType;
import java.text.ParseException;

public class DBValue<T> {
    private final T value;
    private final Type type;
    private final String name;
    private final String description;

    public static DBValue<?> readFrom(LittleEndianDataInputStream inStream, String[] configEntry) throws Exception {
        if (configEntry.length < 1) {
            throw new ParseException("config entry is empty", configEntry.length);
        }

        Type type = Type.valueOf(configEntry[0].strip());
        String name = configEntry.length >= 2 ? configEntry[1].strip() : "";
        String description = configEntry.length >= 3 ? configEntry[2].strip() : "";

        return switch (type) {
            // value types
            case STRING -> {
                byte[] chars = new byte[100];
                int n = inStream.read(chars);
                if (n != 100) {
                    throw new ParseException("String shorter than expected length", n);
                }

                yield new DBValue<>(Util.fromCString(chars), Type.STRING, name, description);
            }
            case INTEGER -> new DBValue<>(inStream.readInt(), Type.INTEGER, name, description);
            case FLOAT -> new DBValue<>(inStream.readFloat(), Type.FLOAT, name, description);
            case BOOL -> new DBValue<>(inStream.readBoolean(), Type.BOOL, name, description);

            // padding types
            case PAD_8 -> {
                inStream.readByte();
                yield new Padding(type);
            }
            case PAD_16 -> {
                inStream.readNBytes(2);
                yield new Padding(type);
            }
            case PAD_24 -> {
                inStream.readNBytes(3);
                yield new Padding(type);
            }
            case PAD_32 -> {
                inStream.readNBytes(4);
                yield new Padding(type);
            }
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

    public String getValueAsString() {
        return switch (type) {
            case FLOAT -> Util.numberFormat.format((float) getValue());
            default -> getValue() == null ? "null" : getValue().toString();
        };
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

        PAD_8,
        PAD_16,
        PAD_24,
        PAD_32
    }

    public static class Padding extends DBValue<NullType> {
        private final int numBytes;

        public Padding(Type type) {
            super(null, type, "<padding>", "");
            this.numBytes = switch (type) {
                case PAD_8 -> 1;
                case PAD_16 -> 2;
                case PAD_24 -> 3;
                case PAD_32 -> 4;
                default -> throw new RuntimeException("Attempted to create Padding value from non PAD type");
            };
        }

        public int getNumBytes() {
            return numBytes;
        }
    }
}
