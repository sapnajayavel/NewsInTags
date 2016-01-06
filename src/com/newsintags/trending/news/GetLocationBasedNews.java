package com.newsintags.trending.news;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.newsintags.twitter.login.GetHomelineTweets;
import com.newsintags.util.MongoDbUtil;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * Servlet implementation class GetLocationBasedNews
 */
@WebServlet("/GetLocationBasedNews")
public class GetLocationBasedNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLocationBasedNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String callback = request.getParameter("callback");
		String location= request.getParameter("location");
		URL url  = new URL("http://www.thehindu.com/news/cities/"+location.toLowerCase()+"/?service=rss");
	    XmlReader reader = null;
	    try {
	    	
	      reader = new XmlReader(url);
	      SyndFeed feed = new SyndFeedInput().build(reader);
	      int newsCount = 0;
	      JSONObject newsObj ;
	      JSONObject collectionObj = new JSONObject();
	      JSONArray array = new JSONArray();
	     for (Iterator i = feed.getEntries().iterator(); i.hasNext();) {
	    	if(newsCount++ > 10){
	    		System.out.println("Location "+location +" news count"+newsCount);
					break;
			}
	    	newsObj = new JSONObject();
	        SyndEntry entry = (SyndEntry) i.next();
	        String description = removeTags(entry.getDescription().getValue().toString());
	        String textToAlchemy = entry.getTitle()+" "+description;
			Date newSDate = entry.getPublishedDate();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			df.format(newSDate);
			String dateDiff = timeDifference(newSDate);
			newsObj.put("title", entry.getTitle());
			newsObj.put("time", dateDiff);
			newsObj.put("description", description);
			newsObj.put("url",  entry.getLink());
			newsObj.put("location", location);
			newsObj.put("imageUrl","http://a515.phobos.apple.com/us/r30/Purple/v4/49/c2/b5/49c2b56e-5fc5-0bad-d3cf-210e60d34431/mzl.pturateb.png");
			array.put(newsObj);
	        
	     }
	     collectionObj.put("locationnews", array);
	     response.setContentType("application/json");
			response.getWriter().write(callback+"("+collectionObj.toString()+")");
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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

}
