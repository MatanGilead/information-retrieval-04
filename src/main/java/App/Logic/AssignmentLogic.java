package App.Logic;

import App.*;
import App.Model.*;
import App.Modules.DBModule;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileOutputStream;
import java.io.File;
import java.util.*;

public class AssignmentLogic {
    protected FileDataAccess _fileDataAccess;
    protected ParameterFileParser _parameterFileParser;

    public AssignmentLogic(FileDataAccess fileDataAccess, ParameterFileParser parameterFileParser) {
        _fileDataAccess = fileDataAccess;
        _parameterFileParser = parameterFileParser;
    }

    private void exportData(String output_file, List<ParsedDocument> docs) throws IOException {
        File f = new File(output_file);
        if (!f.exists()) {
            f.createNewFile();
        }

        FileOutputStream fop = new FileOutputStream(f, true);
        PrintWriter writer = new PrintWriter(fop);
        for (ParsedDocument doc : docs) {
            writer.println(String.format("%s,%d,%d", doc.getDocId(), doc.getPredictedClass(), doc.getClassId()));
        }
        writer.close();
    }

    public void queryList(DBModule db, Classifier classifier, Integer k_value, List<ParsedDocument> docs) throws Exception{
        ListIterator<ParsedDocument> it = docs.listIterator();
        while (it.hasNext()) {
            ParsedDocument doc = it.next();
            Integer idx = it.nextIndex();
            ScoreDoc[] hits = db.queryDocument(doc, k_value);
            Integer class_id = classifier.classify(hits);
            doc.setPredictedClass(class_id);
            LogHandler.info(String.format("class for doc %s predicted to be %d, true class is %d:  - %d out of %d", doc.getDocId(), class_id, doc.getClassId(), idx, docs.size()));
        }
    }

    public void run(String parametersFileName) throws Exception {
        // Get all relevant parameters
        _parameterFileParser.LoadContent(parametersFileName);

        // Parse the documents
        List<ParsedDocument> trainData = _fileDataAccess.parseTrainFile(_parameterFileParser.getTrainFile());
        List<ParsedDocument> testData = _fileDataAccess.parseTestFile(_parameterFileParser.getTestFile());

        String output_path = _parameterFileParser.getOutputFile();
        Integer k_value = _parameterFileParser.getKValue();
        Classifier classifier = new Classifier(trainData);
        DBModule db = new DBModule();
        db.indexDocs(trainData);
        queryList(db, classifier, k_value, testData);
        Collections.sort(testData, Comparator.comparing(ParsedDocument::getDocId));
        exportData(output_path, testData);
        Measurement measurement = new Measurement(testData);
        Double microF = measurement.getMicroAveraging();
        Double macroF = measurement.getMacroAveraging();
        LogHandler.info(String.format("MicroF=%4.3f, MacroF=%4.3f, K=%d", microF, macroF, k_value));
    }
}