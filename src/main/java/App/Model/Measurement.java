package App.Model;

import java.util.HashMap;
import java.util.List;


public class Measurement {


    private HashMap<Integer, Integer> _false_positives;
    private HashMap<Integer, Integer> _false_negatives;
    private HashMap<Integer, Integer> _true_positives;


    public Measurement(List<ParsedDocument> docs) {
        _false_positives = new HashMap<>();
        _false_negatives = new HashMap<>();
        _true_positives = new HashMap<>();

        for (ParsedDocument doc : docs) {
            Integer true_class = doc.getClassId();
            Integer predicted_class = doc.getPredictedClass();
            if (true_class == predicted_class) {
                _true_positives.put(true_class, _true_positives.getOrDefault(true_class, 0) + 1);
            } else {
                _false_negatives.put(true_class, _false_negatives.getOrDefault(true_class, 0) + 1);
                _false_positives.put(predicted_class, _false_positives.getOrDefault(predicted_class, 0) + 1);
            }

        }
    }

    public Integer getFP(Integer class_id) {
        return _false_positives.getOrDefault(class_id, 0);
    }

    public Integer getFN(Integer class_id) {
        return _false_negatives.getOrDefault(class_id, 0);
    }

    public Integer getTP(Integer class_id) {
        return _true_positives.getOrDefault(class_id, 0);
    }

    public Integer getAggregateFP() {
        Integer result = 0;
        for (Integer value : _false_positives.values()) {
            result+=value;
        }
        return result;
    }

    public Integer getAggregateFN() {
        Integer result = 0;
        for (Integer value : _false_negatives.values()) {
            result+=value;
        }
        return result;
    }


    public Integer getAggregateTP() {
        Integer result = 0;
        for (Integer value : _true_positives.values()) {
            result+=value;
        }
        return result;
    }


    public Double getPrecision(Integer true_positives, Integer false_positives) {

        if ((true_positives + false_positives ) == 0) {
            return Double.MAX_VALUE;
        }
        return true_positives / new Double((true_positives + false_positives ));
    }

    public Double getRecall(Integer true_positives, Integer false_negatives) {
        if (true_positives + false_negatives == 0) {
            return Double.MAX_VALUE;
        }
        return true_positives / new Double((true_positives + false_negatives));
    }

    public Double getF(Integer true_positives, Integer false_positives, Integer false_negatives) {
        Double ALPHA = 0.5;
        Double f  = 1/(ALPHA* ((1/this.getPrecision(true_positives, false_positives)) + (1/this.getRecall(true_positives, false_negatives))));
        return f;
    }


    public double getMicroAveraging() {
        double result = 0.0;

        for (int i=1; i<14; i++) {
            Integer true_positives = this.getTP(i);
            Integer false_positives = this.getFP(i);
            Integer false_negatives = this.getFN(i);
            result += this.getF(true_positives, false_positives, false_negatives);
        }
        return result/14.0;
    }

    public double getMacroAveraging() {
        Integer true_positives = this.getAggregateTP();
        Integer false_positives = this.getAggregateFP();
        Integer false_negatives = this.getAggregateFN();
        return this.getF(true_positives, false_positives, false_negatives);
    }
}