package App.Logic;

import App.*;
import App.Model.*;
import App.Modules.DBModule;
import org.apache.lucene.search.ScoreDoc;

import java.io.PrintWriter;
import java.util.*;

public class AssignmentLogic {
    protected FileDataAccess _fileDataAccess;
    protected ParameterFileParser _parameterFileParser;
    private PrintWriter _csvPrinter = null;

    public AssignmentLogic(FileDataAccess fileDataAccess, ParameterFileParser parameterFileParser) {
        _fileDataAccess = fileDataAccess;
        _parameterFileParser = parameterFileParser;
    }

    private ParsedDocument findDoc(String doc_id, List<ParsedDocument> docs) {
        for (ParsedDocument doc : docs) {
            if (doc_id.equals(doc.getDocId())) {
                return doc;
            }
        }
        return null;
    }

    private Integer findExpectedClassId(ScoreDoc[] hits, List<ParsedDocument> docs) {
        HashMap<Integer, Double> class_probability = new HashMap<>();
        for (ScoreDoc hit : hits) {
            ParsedDocument doc = this.findDoc(String.valueOf(hit.doc), docs);
            Integer class_id = doc.getClassId();
            class_probability.put(class_id, class_probability.getOrDefault(class_id, 0.0) + hit.score);
        }

        System.out.println(class_probability);
        return Collections.max(class_probability.entrySet(),Map.Entry.comparingByValue()).getKey();
    }

    public void run(String parametersFileName, Integer batchSize) throws Exception {
        // Get all relevant parameters
        _parameterFileParser.LoadContent(parametersFileName);

        // Parse the documents
        List<ParsedDocument> trainData = _fileDataAccess.parseTrainFile(_parameterFileParser.getTrainFile());
        List<ParsedDocument> testData = _fileDataAccess.parseTestFile(_parameterFileParser.getTestFile());
        Collections.shuffle(testData);
        Integer k_value = _parameterFileParser.getKValue();
        DBModule db = new DBModule();
        db.indexDocs(trainData);
        for (ParsedDocument doc : testData) {
            System.out.println("Predicting class for doc " + doc.getDocId());
            ScoreDoc[] hits = db.queryDocument(doc, k_value);
            Integer class_id = this.findExpectedClassId(hits, trainData);
            System.out.println(".......");
            System.out.println("Found class id " + class_id);
            System.out.println("Real class id " + doc.getClassId());
            System.out.println(".......");
        }
    }
}