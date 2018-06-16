package App;

import App.Model.ParsedDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDataAccess {
    public List<String> readFileLines(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException();

        return Files.readAllLines(Paths.get(filePath));
    }

    public List<ParsedDocument> parseTestFile(String filePath) throws IOException {
        return new ArrayList<>(parseDoc(filePath).values());
    }

    public Map<String, ParsedDocument> parseTrainFile(String filePath) throws IOException {
        return parseDoc(filePath);
    }

    private Map<String, ParsedDocument> parseDoc(String filePath) throws IOException {
        Map<String, ParsedDocument> documents = new HashMap<>();
        Pattern p = Pattern.compile("([^,]+)*,([^,]+),([^,]+),(.+)");
        List<String> lines = readFileLines(filePath);
        for (String line : lines) {
            Matcher m = p.matcher(line);
            if (!m.find()) {
                continue;
            }
            String doc_id = m.group(1);
            Integer class_id = Integer.parseInt(m.group(2));
            String title = m.group(3);
            String body = m.group(4);
            ParsedDocument doc = new ParsedDocument(doc_id, class_id, title, body);
            documents.put(doc.getDocId(),doc);
        }
        return documents;
    }
}
