package App.Logic;

import App.FileDataAccess;
import App.Model.Measurement;
import App.ParameterFileParser;
import App.TestHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.when;

public class AssignmentLogicTest {
    @Test
    public void run_partialTrainingSet_returnValidResults() throws Exception {
        // Arrange
        FileDataAccess fileDataAccess = new FileDataAccess();
        ParameterFileParser parameterFileParser = Mockito.mock((ParameterFileParser.class));

        // Removed from resources since git can't handle it
        when(parameterFileParser.getTrainFile())
                .thenReturn(TestHelper.getFilePathFromResources("train.csv"));

        when(parameterFileParser.getTestFile())
                .thenReturn(TestHelper.getFilePathFromResources("TestSet01/test.csv"));

        when(parameterFileParser.getKValue()).thenReturn(20);

        when(parameterFileParser.getOutputFile()).thenReturn("out/output.txt");

        AssignmentLogic assignmentLogic =
                new AssignmentLogic(fileDataAccess, parameterFileParser,"dummy");

        // Act
        assignmentLogic.train();
        Measurement result = assignmentLogic.run(null);
    }

    @Test
    public void run_testMultipleKValues_returnAllResults() throws Exception {
        // Arrange
        FileDataAccess fileDataAccess = new FileDataAccess();
        ParameterFileParser parameterFileParser = Mockito.mock((ParameterFileParser.class));

        // Removed from resources since git can't handle it
        when(parameterFileParser.getTrainFile()).thenReturn("train.csv");

        // Since we run a lot of tests, we don't want all queries
        when(parameterFileParser.getTestFile())
                .thenReturn(TestHelper.getFilePathFromResources("TestSet01/test.csv"));

        when(parameterFileParser.getKValue()).thenReturn(20);

        when(parameterFileParser.getOutputFile()).thenReturn("out/output.txt");

        AssignmentLogic assignmentLogic =
                new AssignmentLogic(fileDataAccess, parameterFileParser,"dummy");

        // Act
        assignmentLogic.train();

        // Get Results
        for (Integer i = 0; i < 1; i++){
            Measurement result = assignmentLogic.run(i);
            System.out.println("K="+i+
                    ",MicroAveraging="+result.getMicroAveraging()+
                    ",MacroAveraging="+result.getMacroAveraging());
        }
    }
}
