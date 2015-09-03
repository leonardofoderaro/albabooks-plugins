package examples.alba.books;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.solr.client.solrj.beans.Field;


@XmlRootElement(name="book")
@XmlAccessorType(XmlAccessType.PUBLIC_MEMBER)
public class Book {

	@XmlAttribute(name="id")
	@Field
	public long id;
	
	@Field
	public String title;
	
	@Field
	public String author;
	
	@Field
	public String image;
	
	@Field
	public boolean eligibleForDiscount;
	
	@Field
	public float price;
	
	@Field
	public float discountedPrice;
	
	@Field
	public int year;
	
	
}
