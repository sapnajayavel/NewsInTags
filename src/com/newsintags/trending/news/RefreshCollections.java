package com.newsintags.trending.news;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

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

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.newsintags.twitter.login.GetHomelineTweets;
import com.newsintags.util.DbHelper;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class RefreshCollections
 */
@WebServlet("/RefreshCollections")
public class RefreshCollections extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RefreshCollections() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		JSONObject resultObj = new JSONObject();
		String callback = request.getParameter("callback");
		String screenName = request.getParameter("screenName");
		screenName= "nebulatechies7";
		
		Date timeStamp = new Date();
		DB db = DbHelper.getDbConnection();
		DBCursor newCursor = MongoDbUtil.getCursorObject("screenName", screenName, "UserCollection");
		if(newCursor.hasNext()){
			timeStamp = (Date)newCursor.next().get("hitTime");
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			df.format(timeStamp);
		}
		if(timeDifference(timeStamp))
		{
			System.out.println("Time to refresh");
			DBCollection userDoc = db.getCollection("UserCollection");
			BasicDBObject newDocument = new BasicDBObject();
			BasicDBObject searchQuery = new BasicDBObject().append("screenName",screenName);
			newDocument.append("$set", new BasicDBObject().append("hitTime", new Date()));
			userDoc.update(searchQuery, newDocument);
			try{
			DBCollection newsDoc = db.getCollection("NewsCollection");
			newsDoc.drop();
			DBCollection newsConceptDoc = db.getCollection("NewsConceptCollection");
			newsConceptDoc.drop();
			DBCollection conceptDoc = db.getCollection("ConceptCollection");
			conceptDoc.drop();
			DBCollection siteDoc = db.getCollection("SiteCollection");
			DBCursor cursor = siteDoc.find();
			while(cursor.hasNext()) {
				DBObject siteObj = cursor.next();
			    String siteName = (String)siteObj.get("name");
			    String siteId = (String)siteObj.get("_id").toString();
			    if(siteName.equalsIgnoreCase("twitter"))
			    {
			    	GetHomelineTweets.getAllTweets(siteObj.get("_id").toString());
			    }
			    else 
			    {
			    	String siteUrl = (String)siteObj.get("url");
			    	String siteimageUrl = (String)siteObj.get("picUrl");
			    	try {
						GetNewsFromSites.getNews(siteName, siteUrl,siteimageUrl,siteId);
					} catch (XPathExpressionException | SAXException
							| ParserConfigurationException e) {
						e.printStackTrace();
					}
			    }
			}
			
			
			resultObj.put("status", "success");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+resultObj.toString()+")");
			
			}
			
			catch(Exception e){
				e.printStackTrace();
				resultObj.put("status", "failure");
				response.setContentType("application/json");
				response.getWriter().write(callback+"("+resultObj.toString()+")");
			}
		}
		else
		{
			System.out.println("No refresh needed!!");
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	public static Boolean timeDifference(Date dt1)
	{
		Date dt2 = new Date();
		DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
		df.format(dt2);
		System.out.println("current time"+dt2+"hit time"+dt1);
        long diff = dt2.getTime() - dt1.getTime();
        long diffHours = diff / (60 * 60 * 1000);
        System.out.println("Differnce in hours"+diffHours);
        if (diffHours > 1) {
            return true;
        } else {
        	return false;
        }
	}
	
	public static void GetTrendingNews()
	{
		HashMap<String,String> finalListNews = MongoDbUtil.getTrendingNews();
		JSONArray combinedArray = new JSONArray();
		JSONObject trendingNewsObject = new JSONObject();
		for (Entry<String, String> entry : finalListNews.entrySet()) {
			 String key = entry.getKey();
			 String value = entry.getValue();
			 BasicDBObject newsObj = (BasicDBObject) MongoDbUtil.getDBObject(entry.getKey(), "NewsCollection");
			 BasicDBObject conceptObj = (BasicDBObject) MongoDbUtil.getDBObject(entry.getValue(), "ConceptCollection");
			 BasicDBObject siteObj = (BasicDBObject) MongoDbUtil.getDBObject((String)newsObj.get("siteId"), "SiteCollection");
			 JSONObject jsonnewsObj = new JSONObject(newsObj.toString());
			 JSONObject jsonconceptObj = new JSONObject(conceptObj.	toString());
			 JSONObject jsonsiteObj = new JSONObject(siteObj.toString());
			 JSONObject combined = new JSONObject();
			 combined.put("newsDetails", jsonnewsObj);
			 combined.put("conceptDetails", jsonconceptObj);
			 combined.put("siteDetails", jsonsiteObj);
			 JSONArray conceptArray = MongoDbUtil.getAllConceptsForNews(entry.getKey(),"NewsConceptCollection");
			 combined.put("conceptList", conceptArray);
			 combined.put("conceptCount", MongoDbUtil.getCountFromNewsConceptCollection(entry.getKey(),entry.getValue(),"NewsConceptCollection"));
			 combinedArray.put(combined);
			}
		trendingNewsObject.put("status", "success");
		trendingNewsObject.put("trendingNews", combinedArray);
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("TrendingNewsCollection");
		BasicDBObject document = new BasicDBObject();
		document.put("trendingnews1", combinedArray.toString());
		table.insert(document);
	}
	
	
}
