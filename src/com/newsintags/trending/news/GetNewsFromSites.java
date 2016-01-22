package com.newsintags.trending.news;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
 







import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.newsintags.alchemy.api.AlchemyAPI;
import com.newsintags.twitter.login.GetHomelineTweets;
import com.newsintags.util.MongoDbUtil;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

public class GetNewsFromSites {
	
	public static void getNews(String siteName, String siteUrl, String siteImageUrl, String siteId) throws MalformedURLException, XPathExpressionException, SAXException, ParserConfigurationException
	{
		URL url  = new URL(siteUrl);
	    XmlReader reader = null;
	    try {
	    	
	      reader = new XmlReader(url);
	      SyndFeed feed = new SyndFeedInput().build(reader);
	      int newsCount = 0;
	     for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
	    	if(newsCount++ > 10){
	    		System.out.println(siteName+" news count"+newsCount);
					break;
			}
	        SyndEntry entry = (SyndEntry) i.next();
	        String description = removeTags(entry.getDescription().getValue().toString());
	        String textToAlchemy = entry.getTitle()+" "+description;
			if(textToAlchemy.trim().isEmpty())
			{
				continue;
			}
			System.out.println("Feed data"+entry.toString());
			String sentiment = GetHomelineTweets.getSentiment(textToAlchemy);
			Date newSDate = entry.getPublishedDate();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			df.format(newSDate);
			String dateDiff = timeDifference(newSDate);
			String newsId = MongoDbUtil.insertNewsFromSites(entry.getTitle(), entry.getLink(),dateDiff, sentiment,siteName,siteImageUrl,description,siteId);
			GetAlchemyAPIEntity.getEntityFromNews(textToAlchemy, newsId,newSDate);
	        
	     }
	        } catch (IOException | IllegalArgumentException | FeedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
	            if (reader != null)
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
	        }
	}
	public static String removeTags(String news)
	{
	    String replacement = "";
	    String newRegex = "<[^>]*>";
	    return news.replaceAll(newRegex, replacement);
	}
	
	public static String timeDifference(Date dt1)
	{
		Date dt2 = new Date();

        long diff = dt2.getTime() - dt1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        
        int diffInDays = (int) ((dt2.getTime() - dt1.getTime()) / (1000 * 60 * 60 * 24));

        if (diffInDays > 1) {
            return diffInDays+"d";
        } else if (diffHours > 0) {
            return diffHours+"h";
        } else if (diffMinutes > 0) {
        	return diffMinutes+"m";
        } else
        	return diffSeconds+"s";
	}
	
	public static void main(String[] args) throws MalformedURLException, XPathExpressionException, SAXException, ParserConfigurationException {
		//getNews("thehindu", "http://www.thehindu.com/news/?service=rss");
		//getNews("ndtvnews","http://feeds.feedburner.com/NDTV-Trending");
		//getNews("techcrunch","http://techcrunch.com/feed","","");
		
	}
	
	
}
