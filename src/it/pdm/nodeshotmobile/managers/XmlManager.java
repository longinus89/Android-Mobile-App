package it.pdm.nodeshotmobile.managers;

import it.pdm.nodeshotmobile.entities.News;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import com.msi.androidrss.RSSFeed;
import com.msi.androidrss.RSSHandler;
import com.msi.androidrss.RSSItem;

import android.util.Log;

public class XmlManager {
    
	static void vDebug(String debugString){         //metodi di convenienza
            Log.v("DomParsing", debugString+"\n");
    }
    static void eDebug(String debugString){
            Log.e("DomParsing", debugString+"\n");
    }
   
    private ArrayList parsedData=new ArrayList(); //struttura dati che immagazzinerà i dati letti
    
    private RSSFeed getFeed(String urlToRssFeed){
    	try
    	{
    		// setup the url
    	   URL url = new URL(urlToRssFeed);

           // create the factory
           SAXParserFactory factory = SAXParserFactory.newInstance();
           // create a parser
           SAXParser parser = factory.newSAXParser();

           // create the reader (scanner)
           XMLReader xmlreader = parser.getXMLReader();
           // instantiate our handler
           RSSHandler theRssHandler = new RSSHandler();
           // assign our handler
           xmlreader.setContentHandler(theRssHandler);
           // get our data via the url class
           InputSource is = new InputSource(url.openStream());
           // perform the synchronous parse           
           xmlreader.parse(is);
           // get the results - should be a fully populated RSSFeed instance, or null on error
           return theRssHandler.getFeed();
    	}
    	catch (Exception ee)
    	{
    		// if we have a problem, simply return null
    		return null;
    	}
    }
    
    /**
     * This method obtains all news from json file using json parsing JSON Parser. 
     * @param	jString string who contains a JSON Document
     * @throws XmlPullParserException 
     * @throws IOException 
     */
	public void parseRSSNews(String url) throws XmlPullParserException, IOException{
		
		int id = 1;
    	RSSFeed rssfeed = getFeed(url);
    	List<RSSItem> list = rssfeed.getAllItems();
    	for(RSSItem item : list){
    		
    		String description = item.getDescription();
    		description = description.replaceAll("&lt;", "<");
    		description = description.replaceAll("&gt;", ">");
    		description = description.replaceAll("&amp;rsquo;", "\'");
    		description = description.replaceAll("&amp;lsquo;", "\'");
    		description = description.replaceAll("&amp;quot;", "\"");
    		description = description.replaceAll("&quot;", "\"");
    		description = description.replaceAll("&amp;agrave;", "à");
    		description = description.replaceAll("&amp;igrave;", "i");
    		description = description.replaceAll("&amp;egrave;", "è");
    		description = description.replaceAll("&amp;nbsp;", " ");
    		description = description.replaceAll("&#039;", "'");
    		
    		
    		News current = new News();
    		current.setId(id);
    		current.setCategory(item.getCategory());
    		current.setTitle(item.getTitle());
    		
    		
    		
    		current.setContent(description);
    		current.setDate(item.getPubDate());
    		current.setLink(item.getLink());
    		id++;
    		Log.v("parseRSSNews", current.toString());
    		
    		parsedData.add(current);
    	}
    	
	}
		        
	
	
	
    
    public ArrayList getParsedData() {  //metodo di accesso alla struttura dati
            return parsedData;
    }

}
