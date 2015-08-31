package examples.alba.books;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name = "books")
@XmlAccessorType (XmlAccessType.FIELD)
public class Books {
	
	@XmlElement(name="book")
	private List<Book> books = null;
	
	public Books() {
		books = new ArrayList<Book>();
	}
	
	public void setBooks(List<Book> books) {
		this.books = books;
	}

	public List<Book> getBooks() {
		return this.books;
	}
}
