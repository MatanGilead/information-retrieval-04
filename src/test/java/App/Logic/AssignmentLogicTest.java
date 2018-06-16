package App.Logic;

import App.FileDataAccess;
import App.Model.Measurement;
import App.ParameterFileParser;
import App.TestHelper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class AssignmentLogicTest {
    @Test
    public void run_partialTrainingSet_returnValidResults() throws Exception {
        // Arrange
        FileDataAccess fileDataAccess = new FileDataAccess();
        ParameterFileParser parameterFileParser = Mockito.mock((ParameterFileParser.class));
        when(parameterFileParser.getTrainFile())
                .thenReturn(TestHelper.getFilePathFromResources("TestSet01/train.csv"));

        when(parameterFileParser.getTestFile())
                .thenReturn(TestHelper.getFilePathFromResources("TestSet01/test.csv"));

        when(parameterFileParser.getKValue()).thenReturn(20);

        when(parameterFileParser.getOutputFile()).thenReturn("out/output.txt");

        AssignmentLogic assignmentLogic = new AssignmentLogic(fileDataAccess, parameterFileParser);

        // Act
        Measurement result = assignmentLogic.run("dummy");


    }
}
