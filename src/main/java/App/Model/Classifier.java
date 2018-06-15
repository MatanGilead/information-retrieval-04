package App.Model;

import org.apache.lucene.search.ScoreDoc;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifier {
    List<ParsedDocument> _train_data;

    public Classifier(List<ParsedDocument> train_data) {
        _train_data = train_data;

    }
    private ParsedDocument findDoc(String doc_id, List<ParsedDocument> docs) {
        for (ParsedDocument doc : docs) {
            if (doc_id.equals(doc.getDocId())) {
                return doc;
            }
        }
        return null;
    }

    public Integer classify(ScoreDoc[] hits) {
        HashMap<Integer, Double> class_probability = new HashMap<>();
        for (ScoreDoc hit : hits) {
            ParsedDocument doc = this.findDoc(String.valueOf(hit.doc), _train_data);
            Integer class_id = doc.getClassId();
            class_probability.put(class_id, class_probability.getOrDefault(class_id, 0.0) + hit.score);
        }

        //System.out.println(class_probability);
        return Collections.max(class_probability.entrySet(),Map.Entry.comparingByValue()).getKey();
    }


}