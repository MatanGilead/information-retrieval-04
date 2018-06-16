package App.Modules;

import org.apache.lucene.analysis.Analyzer;
import App.Model.ParsedDocument;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.similarities.ClassicSimilarity;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.index.IndexWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DBModule {
    private RAMDirectory _index;
    private Analyzer _analyzer;
    private IndexWriterConfig _config;

    public DBModule() {
        _index = new RAMDirectory();
        _analyzer = new EnglishAnalyzer(CharArraySet.EMPTY_SET);
        _config = new IndexWriterConfig(_analyzer);
        _config.setSimilarity(new ClassicSimilarity());
    }


    public void indexDocs(List<ParsedDocument> parsed_docs) throws  IOException{
        List<Document> documents = new ArrayList<>();
        for (ParsedDocument parsed_doc: parsed_docs) {
            Document doc = new Document();
            doc.add(new TextField("doc_id", parsed_doc.getDocId() , Field.Store.YES));
            doc.add(new TextField("body", parsed_doc.getBody(), Field.Store.YES));
            documents.add(doc);
        }
        IndexWriter indexWriter = new IndexWriter(_index, _config);
        indexWriter.addDocuments(documents);
        indexWriter.close();
    }

    public ScoreDoc[] queryDocument(ParsedDocument parsed_doc, Integer k_value) throws ParseException, IOException {
        QueryParser queryParser = new QueryParser("body", _analyzer);
        queryParser.setSplitOnWhitespace(true);
        Query q = queryParser.parse(QueryParser.escape(parsed_doc.getBody()));
        IndexReader reader = DirectoryReader.open(_index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new ClassicSimilarity());
        TopScoreDocCollector collector = TopScoreDocCollector.create(k_value);
        searcher.search(q, collector);
        ScoreDoc[] hits = collector.topDocs().scoreDocs;
        reader.close();
        return hits;
    }

    public IndexSearcher getSearcher() throws IOException {
        IndexReader reader = DirectoryReader.open(_index);
        IndexSearcher searcher = new IndexSearcher(reader);
        searcher.setSimilarity(new ClassicSimilarity());
        return searcher;
    }
}
