package App.Logic;

import App.*;
import App.Model.*;
import App.Modules.DBModule;
import App.Modules.QueryManager;
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


    public void run(String parametersFileName) throws Exception {
        // Get all relevant parameters
        _parameterFileParser.LoadContent(parametersFileName);

        LogHandler.info("Parsing train data");
        // Parse the documents
        List<ParsedDocument> trainData = _fileDataAccess.parseTrainFile(_parameterFileParser.getTrainFile());
        LogHandler.info("Parsing test data");
        List<ParsedDocument> testData = _fileDataAccess.parseTestFile(_parameterFileParser.getTestFile());

        String output_path = _parameterFileParser.getOutputFile();
        Integer k_value = _parameterFileParser.getKValue();

        DBModule db = new DBModule();
        Classifier classifier = new Classifier(trainData);

        LogHandler.info("Started training");
        db.indexDocs(trainData);

        int threads_amount = _parameterFileParser.getThreadsNum();
        LogHandler.info("Started testing");
        QueryManager queryManager = new QueryManager(db, classifier, threads_amount);
        queryManager.test(testData, k_value);

        LogHandler.info("Saving results to CSV");
        Collections.sort(testData, Comparator.comparing(ParsedDocument::getDocId));
        exportData(output_path, testData);

        LogHandler.info("Measuring results");
        Measurement measurement = new Measurement(testData);
        Double microF = measurement.getMicroAveraging();
        Double macroF = measurement.getMacroAveraging();
        LogHandler.info(String.format("MicroF=%4.3f, MacroF=%4.3f, K=%d", microF, macroF, k_value));
        }
    }