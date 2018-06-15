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



    public void run(String parametersFileName, Integer batchSize) throws Exception {
        // Get all relevant parameters
        _parameterFileParser.LoadContent(parametersFileName);

        // Parse the documents
        List<ParsedDocument> trainData = _fileDataAccess.parseTrainFile(_parameterFileParser.getTrainFile());
        List<ParsedDocument> testData = _fileDataAccess.parseTestFile(_parameterFileParser.getTestFile());
        Collections.shuffle(testData);
        Integer k_value = _parameterFileParser.getKValue();
        Classifier classifier = new Classifier(trainData);
        DBModule db = new DBModule();
        db.indexDocs(trainData);
        for (ParsedDocument doc : testData) {
            System.out.println("Predicting class for doc " + doc.getDocId());
            ScoreDoc[] hits = db.queryDocument(doc, k_value);
            Integer class_id = classifier.classify(hits);
            doc.setPredictedClass(class_id);
            System.out.println(".......");
            System.out.println("Found class id " + class_id);
            System.out.println("Real class id " + doc.getClassId());
            System.out.println(".......");
        }
        Measurement measurement = new Measurement(testData);
        System.out.println(measurement.getMicroAveraging());
        System.out.println(measurement.getMacroAveraging());
    }
}