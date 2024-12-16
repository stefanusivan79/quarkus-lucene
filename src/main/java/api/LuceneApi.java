package api;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import model.Book;
import utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;

@Path("/lucene")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LuceneApi {

	@POST
	@Path("/index")
	public void index() {
		var records = new ArrayList<Book>();
		records.add(new Book("Baby Elephant Runs Away", "978-1-933624-44-0"));
		records.add(new Book("Baby Elephant Runs Away Lap Book", "978-1-933624-51-8"));
		records.add(new Book("Bats in Danny’s House", "978-1-933624-95-2"));
		records.add(new Book("Birds Around the Pond", "978-1-62544-115-7"));
		SearchUtils.index(records);
	}

	@GET
	@Path("/search")
	public List<Book> search(@QueryParam("q") String query) {
		return SearchUtils.search(query);
	}
}
