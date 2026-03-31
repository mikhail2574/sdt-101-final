package teamworks.persistence;

import java.util.ArrayList;
import java.util.List;

public final class TextRecordCodec {
    private TextRecordCodec() {
    }

    public static String encode(List<String> fields) {
        List<String> encodedFields = new ArrayList<>();
        for (String field : fields) {
            encodedFields.add(escape(field));
        }
        return String.join("|", encodedFields);
    }

    public static List<String> decode(String line, int expectedFieldCount) {
        List<String> decodedFields = new ArrayList<>();
        StringBuilder currentField = new StringBuilder();
        boolean escaping = false;

        for (int i = 0; i < line.length(); i++) {
            char currentChar = line.charAt(i);

            if (escaping) {
                if (currentChar == 'n') {
                    currentField.append('\n');
                } else {
                    currentField.append(currentChar);
                }
                escaping = false;
                continue;
            }

            if (currentChar == '\\') {
                escaping = true;
                continue;
            }

            if (currentChar == '|') {
                decodedFields.add(currentField.toString());
                currentField.setLength(0);
                continue;
            }

            currentField.append(currentChar);
        }

        if (escaping) {
            currentField.append('\\');
        }

        decodedFields.add(currentField.toString());

        if (decodedFields.size() != expectedFieldCount) {
            throw new IllegalArgumentException("Invalid record: " + line);
        }

        return decodedFields;
    }

    private static String escape(String value) {
        String safeValue = value == null ? "" : value;
        return safeValue
                .replace("\\", "\\\\")
                .replace("|", "\\|")
                .replace("\n", "\\n");
    }
}
