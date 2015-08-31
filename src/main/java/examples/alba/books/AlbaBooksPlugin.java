package examples.alba.books;

import java.util.Calendar;

import org.apache.commons.lang.WordUtils;
import org.apache.solr.common.SolrDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import alba.solr.annotations.DocTransformer;
import alba.solr.annotations.FunctionQuery;
import alba.solr.annotations.Param;
import alba.solr.annotations.AlbaPlugin;

/*
 * IMPORTANT:
 * in SolrConfig.xml must be present this snippet:
 
       <searchComponent name="loader" class="alba.solr.core.Loader">
          <lst name="packagesToScan">
            <str>examples.alba.books</str>
          </lst>
        </searchComponent>
 * 
 */


@AlbaPlugin
public class AlbaBooksPlugin {

	Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/*
	 * http://localhost:8983/solr/albabooks/select?q=*:*&rows=10&fl=[alba%20name=imagepath],author,title,image,isin
	 * the field isin must be inclued in the fieldset, otherwise we'll not be able to use it inside our transformer
	 */
	@DocTransformer(name = "imagepath")
	public void addAmazonImagePath(SolrDocument doc) {
		String isin = doc.getFieldValue("isin").toString();
		
		boolean isEligibleForDiscount = false;
	
		Object discountField = doc.getFieldValue("eligibleForDiscount");
		
		if (discountField != null) {
		  isEligibleForDiscount = Boolean.valueOf(discountField.toString());
		}
		
		String imagePath =  "";
		
		if (isEligibleForDiscount) {
		  imagePath = "http://images.amazon.com/images/P/" + isin + ".01._PE10_.jpg";		
		} else {
			imagePath = "http://images.amazon.com/images/P/" + isin + ".01._PB__.jpg";
		}
		
		doc.setField("image", imagePath);
	}
	
	
	
	/*
	 * Converts all-capitals occurrences of fields author and publisher into their capitalized corresponding versions.
	 * e.g.: "NAME SURNAME" becomes "Name Surname" 
	 */
	
	@DocTransformer(name = "normalizeFields")
	public void normalizeFields(SolrDocument doc) {
		String author = doc.getFirstValue("author").toString();
		
		int uppercaseCounter = 0;
		
		for (int i=0; i < author.length(); i++) {
			if (Character.isUpperCase(author.charAt(i))) {
				uppercaseCounter++;
			}
		}
		
		if (((float)uppercaseCounter / (float)author.length()) > 0.5) {
			String parts[] = author.split(" ");
			StringBuffer sb = new StringBuffer();
			for (String s : parts) {
				s = WordUtils.capitalize(s.toLowerCase());
				sb.append(s);
				sb.append(" ");
			}
			author = sb.toString().trim();
			doc.setField("author", author);
		}
	}
	
	
	@FunctionQuery(name="euro", description="converts a price field into Euros. input values are assumed to be in dollars")
	public Float euro(@Param(name="field", description="") float value) {
		return (float) (value * 0.895);
	}
	
	
	/*
	 * fields must be passed explicitly
	 */
	@FunctionQuery(name="discount", description="applies 10% discount on books older than X years")
	public Float discount(@Param(name="age", description="the book's age on which apply the discount") int age,
			              @Param(name="price", description="the price field") float price,
			              @Param(name="year", description="the year field") int year) {
		if (isEligibleForDiscount(year, age)) {
			return (float) (price * 0.9);
		} else {
			return price;
		}
	}
	
	@FunctionQuery(name="eligibleForDiscount", description="true if the book is older than X years")
	public Boolean eligibleForDiscount(@Param(name="year", description="the book's publishing year") int year,
		                               @Param(name="treshold", description="the treshold age for discount eligibility") int treshold) {
		return isEligibleForDiscount(year, treshold);
	}
	
	
	@FunctionQuery(name="strlen", description="returns the len of a string")
	public Integer len(@Param(name="string", description="the string to analyze") String string) {
		return string.length();
	}
	         
	
	private boolean isEligibleForDiscount(int year, int age) {
		int currentYear = Calendar.getInstance().get(Calendar.YEAR);
		return ((currentYear - year) >= age);
	}
	
	
}




