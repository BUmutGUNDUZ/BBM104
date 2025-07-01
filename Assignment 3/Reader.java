import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Reader {

    public static List<List<String>> readFileAndParse(String fileName) throws IOException {
        String content = new String(Files.readAllBytes(Paths.get(fileName)));
        List<List<String>> allData = new ArrayList<>();
        // Split content by newlines, supporting empty lines too
        String[] lines = content.split("\n+");
        for (String line : lines) {
            line = line.trim(); // Remove leading/trailing whitespace
            // Split by comma and convert to list
            allData.add(new ArrayList<>(Arrays.asList(line.split(","))));
        }
        return allData;
    }
}