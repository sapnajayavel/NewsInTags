package com.newsintags.trending.news;

import java.io.IOException;
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
 * Servlet implementation class GetTredningNews
 */
@WebServlet("/GetTrendingNews")
public class GetTrendingNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    
    public GetTrendingNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String callback = request.getParameter("callback");
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
			 combined.put("sentiment", jsonnewsObj.get("sentiment"));
			 combined.put("conceptDetails", jsonconceptObj);
			 combined.put("siteDetails", jsonsiteObj);
			 JSONArray conceptArray = MongoDbUtil.getAllConceptsForNews(entry.getKey(),"NewsConceptCollection");
			 combined.put("conceptList", conceptArray);
			 combined.put("conceptCount", MongoDbUtil.getCountFromNewsConceptCollection(entry.getKey(),entry.getValue(),"NewsConceptCollection"));
			 combinedArray.put(combined);
			}
		trendingNewsObject.put("status", "success");
		trendingNewsObject.put("trendingNews", combinedArray);
		/*RefreshCollections.GetTrendingNews();
		JSONObject trendingNewsObject = new JSONObject();
		DB db = DbHelper.getDbConnection();
		 DBCollection siteDoc = db.getCollection("TrendingNewsCollection");
		 DBCursor cursor = siteDoc.find();
		 JSONObject obj = new JSONObject() ;
		 JSONArray combined = new JSONArray();
		 BasicDBObject dbObj = new BasicDBObject();
		 while(cursor.hasNext()){
				 obj = new JSONObject(cursor.next().toString()); 
		}
		String data = obj.toString();
		String newData = "\""+data+"\"".replaceAll("\"", "\"");*/
		response.setContentType("application/json");
		response.getWriter().write(callback+"("+trendingNewsObject.toString()+")");
		
	}

	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	}

}
