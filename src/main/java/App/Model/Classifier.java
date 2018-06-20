package App.Model;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Classifier {
    Map<String, ParsedDocument> _train_data;

    public Classifier(Map<String, ParsedDocument> train_data) {
        _train_data = train_data;
    }

    public Integer classify(ScoreDoc[] hits, IndexSearcher searcher) throws IOException {
        HashMap<Integer, Double> class_probability = new HashMap<>();
        for (ScoreDoc hit : hits) {
            Document rawDoc = searcher.doc(hit.doc);
            ParsedDocument doc = _train_data.getOrDefault(rawDoc.get("doc_id"), null);
            Integer class_id = doc.getClassId();
            class_probability.put(class_id, class_probability.getOrDefault(class_id, 0.0) + hit.score);
        }

        searcher.getIndexReader().close();
        //System.out.println(class_probability);
        return Collections.max(class_probability.entrySet(),Map.Entry.comparingByValue()).getKey();
    }


}