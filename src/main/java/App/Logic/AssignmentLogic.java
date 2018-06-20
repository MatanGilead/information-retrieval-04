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
    protected DBModule _db;
    protected Classifier _classifier;

    public AssignmentLogic(FileDataAccess fileDataAccess, ParameterFileParser parameterFileParser,
                           String parametersFileName) throws IOException {
        _fileDataAccess = fileDataAccess;
        _parameterFileParser = parameterFileParser;

        // Get all relevant parameters
        _parameterFileParser.LoadContent(parametersFileName);

        // Initialize the DB module
        _db = new DBModule();
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

    public void train() throws IOException {
        LogHandler.info("Parsing train data");
        // Parse the documents
        Map<String, ParsedDocument> trainData = _fileDataAccess.parseTrainFile(_parameterFileParser.getTrainFile());
        _classifier = new Classifier(trainData);

        LogHandler.info("Started training");
        _db.indexDocs(trainData.values());
    }

    public Measurement run(Integer overrideKValue) throws Exception {
        LogHandler.info("Parsing test data");
        List<ParsedDocument> testData = _fileDataAccess.parseTestFile(_parameterFileParser.getTestFile());

        String output_path = _parameterFileParser.getOutputFile();
        Integer k_value = _parameterFileParser.getKValue();

        if (overrideKValue != null)
            k_value = overrideKValue;

        int threads_amount = _parameterFileParser.getThreadsNum();
        LogHandler.info("Started testing");
        QueryManager queryManager = new QueryManager(_db, _classifier, threads_amount);
        queryManager.test(testData, k_value);

        LogHandler.info("Saving results to CSV");
        Collections.sort(testData, Comparator.comparing(ParsedDocument::getDocId));
        exportData(output_path, testData);

        LogHandler.info("Measuring results");
        Measurement measurement = new Measurement(testData);
        Double microF = measurement.getMicroAveraging();
        Double macroF = measurement.getMacroAveraging();
        LogHandler.info(String.format("MicroF=%4.3f, MacroF=%4.3f, K=%d", microF, macroF, k_value));
        return measurement;
    }
}