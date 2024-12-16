package utils;

import jakarta.inject.Singleton;
import model.Book;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.ByteBuffersDirectory;
import org.apache.lucene.store.Directory;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Singleton
public class SearchUtils {
	private static final StandardAnalyzer analyzer = new StandardAnalyzer();
	private static final Directory index = new ByteBuffersDirectory();

	private static final Logger LOG = Logger.getLogger(SearchUtils.class);

	private SearchUtils() {}

	public static void index(List<Book> records) {
		var config = new IndexWriterConfig(analyzer);

		try (var w = new IndexWriter(index, config)) {
			w.deleteAll();
			w.commit();

			for (var r : records) {
				var doc = new Document();
				doc.add(new TextField("title", r.title(), Field.Store.YES));
				doc.add(new StringField("isbn", r.isbn(), Field.Store.YES));
				w.addDocument(doc);
			}
		} catch (IOException e) {
			LOG.errorf("Error indexing records: %s", e.getMessage(), e);
		}
	}

	public static List<Book> search(String queryString) {
		var documents = new ArrayList<Book>();
		try {
			var q = new QueryParser("title", analyzer).parse(queryString);
			var reader = DirectoryReader.open(index);
			var searcher = new IndexSearcher(reader);
			var docs = searcher.search(q, 10);
			var hits = docs.scoreDocs;
			var storeFields = searcher.storedFields();

			LOG.infof("Found %d hits", hits.length);
			for (org.apache.lucene.search.ScoreDoc hit : hits) {
				int docId = hit.doc;
				var d = storeFields.document(docId);
				documents.add(new Book(d.get("title"), d.get("isbn")));
			}
		} catch (IOException | ParseException e) {
			LOG.errorf("Error searching for query \"%s\": %s", queryString, e.getMessage(), e);
		}

		return documents;
	}
}
