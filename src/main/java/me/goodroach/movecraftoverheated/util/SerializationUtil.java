package me.goodroach.movecraftoverheated.util;

import java.util.ArrayList;
import java.util.List;

public class SerializationUtil {

    public static Object serialize2dByteArray(final byte[][] array) {
        List<String> result = new ArrayList<>();

        for(int i = 0; i < array.length; i++) {
            byte[] rowArray = array[i];
            String row = "";
            for (int j = 0; j < rowArray.length; j++) {
                byte entry = rowArray[j];
                if (j > 0) {
                    row = row + ",";
                }
                row = row + entry;
            }
        }

        return result;
    }

    public static byte[][] deserialize2dByteArray(final Object arg) {
        if (arg == null) {
            throw new IllegalArgumentException("Given object can not be null!");
        }
        if (!(arg instanceof List)) {
            throw new IllegalArgumentException("Argument must be a list instance!");
        }
        List<String> list = (List<String>) arg;
        byte[][] result = new byte[list.size()][];

        for (int i = 0; i < list.size(); i++) {
            String s = list.get(i);
            try {
                String[] parts = s.split(",\\s*"); // Split by comma and optional spaces
                if (parts.length != 3) {
                    throw new IllegalArgumentException("Invalid vector format: " + s);
                }
                byte x = Byte.parseByte(parts[0]);
                byte y = Byte.parseByte(parts[1]);
                byte z = Byte.parseByte(parts[2]);
                byte[] newRow = new byte[] {x, y, z};
                result[i] = newRow;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number in vector: " + s, e);
            }
        }

        return result;
    }
}
