package App;

import App.Model.ParsedDocument;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileDataAccess {
    public String readFile(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException();

        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    public List<String> readFileLines(String filePath) throws IOException {
        File file = new File(filePath);

        if (!file.exists())
            throw new FileNotFoundException();

        return Files.readAllLines(Paths.get(filePath));
    }

    public List<ParsedDocument> parseTestFile(String filePath) throws IOException {
        return parseDoc(filePath);
    }

    public List<ParsedDocument> parseTrainFile(String filePath) throws IOException {
        return parseDoc(filePath);
    }

    private List<ParsedDocument> parseDoc(String filePath) throws IOException {
        List<ParsedDocument> documents = new ArrayList<>();
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
            documents.add(doc);
        }
        return documents;
    }
//
//    public void writeResults(String filePath, Map<String, List<String>> results) throws IOException {
//        File file = new File(filePath);
//        file.getParentFile().mkdirs();
//
//        if (file.exists())
//            file.delete();
//
//        file.createNewFile();
//
//        FileWriter writer = new FileWriter(file.getAbsolutePath());
//        String output = "";
//        for (String id : results.keySet()) {
//            List<Integer> intList = new ArrayList<>();
//
//            for(String s : results.get(id)) intList.add(Integer.valueOf(s));
//            Collections.sort(intList);
//
//            List<String> orderedStringList = new ArrayList<>(intList.size());
//            for (Integer i : intList) {
//                orderedStringList.add(i.toString());
//            }
//
//            output += id + " " + String.join(" ", orderedStringList) + "\n";
//        }
//
//        writer.write(output);
//        writer.flush();
//    }
}
