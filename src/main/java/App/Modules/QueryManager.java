package App.Modules;

import App.LogHandler;
import App.Model.Classifier;
import App.Model.ParsedDocument;
import org.apache.lucene.search.ScoreDoc;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class QueryManager {
    private int _threads_amount;
    private DBModule _db;
    private Classifier _classifier;

    public QueryManager(DBModule db,Classifier classifier, int threads_amount) {
        _db = db;
        _classifier = classifier;
        _threads_amount = threads_amount;
    }


    public void queryList(Integer k_value, List<ParsedDocument> docs) throws Exception {
        ListIterator<ParsedDocument> it = docs.listIterator();
        while (it.hasNext()) {
            ParsedDocument doc = it.next();
            Integer idx = it.nextIndex();
            ScoreDoc[] hits = _db.queryDocument(doc, k_value);
            Integer class_id = _classifier.classify(hits, _db.getSearcher());
            doc.setPredictedClass(class_id);
            LogHandler.info(String.format("%s: class for doc %s predicted to be %d, true class is %d:  - %d out of %d", Thread.currentThread(), doc.getDocId(), class_id, doc.getClassId(), idx, docs.size()));
        }
    }


    public void test(List<ParsedDocument> testData, int k_value) throws  InterruptedException{
        Integer tasks_per_thread = new Double(Math.ceil(testData.size() / new Double(_threads_amount))).intValue();
        ArrayList<Thread> arrThreads = new ArrayList<>();
        Integer i = 0;
        while (i < testData.size() - 1) {
            Integer j = Math.min(i + tasks_per_thread, testData.size());
            List<ParsedDocument> subData = testData.subList(i, j);
            i = j;
            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        queryList(k_value, subData);
                    } catch (Exception e) {
                        System.out.println(e.toString());
                    }

                }
            });
            arrThreads.add(thread);
            thread.start();
        }

        for (int k = 0; k < arrThreads.size(); k++)
        {
            LogHandler.info("Joining for thread " + k);
            arrThreads.get(k).join();
        }
    }
}
