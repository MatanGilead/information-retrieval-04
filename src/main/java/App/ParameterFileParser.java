package App;

import App.Model.ParsedDocument;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.InvalidPropertiesFormatException;
import java.util.List;

public class ParameterFileParser {

    private FileDataAccess _fileDataAccess;
    private String _trainFile;
    private String _testFile;
    private String _outputFile;
    private int _k = 0;

    private final String _trainFilePrefix = "trainFile=";
    private final String _testFilePrefix = "testFile=";
    private final String _outputFilePrefix = "outputFile=";
    private final String _kPrefix = "k=";

    public ParameterFileParser(FileDataAccess fileDataAccess) {
        _fileDataAccess = fileDataAccess;
    }

    //region Properties' Getters
    public String getTrainFile() {
        return _trainFile;
    }

    public String getTestFile() {
        return _testFile;
    }

    public String getOutputFile() {
        return _outputFile;
    }

    public int getKValue() {
        return _k;
    }


    public void LoadContent(String fileName) throws IOException {
        List<String> fileContent = _fileDataAccess.readFileLines(fileName);

        for (String line : fileContent) {
            if (line.startsWith(_trainFilePrefix)) {
                _trainFile = line.split(_trainFilePrefix)[1];
            } else if (line.startsWith(_testFilePrefix)) {
                _testFile = line.split(_testFilePrefix)[1];
            } else if (line.startsWith(_outputFilePrefix)) {
                _outputFile = line.split(_outputFilePrefix)[1];
            } else if (line.startsWith(_kPrefix)) {
                _k = Integer.parseInt(line.split(_kPrefix)[1].trim());
            }
        }

        validateParameters();
    }

    private void validateParameters() throws InvalidPropertiesFormatException {
        if (_trainFile == null || _trainFile.equals("")) {
            throw new InvalidPropertiesFormatException("trainFile field was not defined in parameters file.");
        } else if (_testFile == null || _testFile.equals("")) {
            throw new InvalidPropertiesFormatException("testFile field was not defined in parameters file.");
        } else if (_outputFile == null || _outputFile.equals("")) {
            throw new InvalidPropertiesFormatException("OutputFile field was not defined in parameters file.");
        } else if (_k == 0) {
            throw new InvalidPropertiesFormatException("K is not defined well, need to be integer greater than 1.");
        }
    }
}
