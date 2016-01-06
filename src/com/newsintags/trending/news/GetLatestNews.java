package com.newsintags.trending.news;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.newsintags.twitter.login.GetHomelineTweets;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetLatestNews
 */
@WebServlet("/GetLatestNews")
public class GetLatestNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetLatestNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String callback = request.getParameter("callback");
		HashMap<String,String> finalList = MongoDbUtil.getLatestNews();
		JSONArray combinedArray = new JSONArray();
		JSONObject latestNewsObject = new JSONObject();
		for (Entry<String, String> entry : finalList.entrySet()) {
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
			 combined.put("dateValue", MongoDbUtil.getDateFromNewsConceptCollection(entry.getKey(),entry.getValue(),"NewsConceptCollection"));
			 combined.put("latestTime", GetHomelineTweets.timeDifference(MongoDbUtil.getDateFromNewsConceptCollection(entry.getKey(),entry.getValue(),"NewsConceptCollection")));
			 combined.put("conceptList", MongoDbUtil.getAllConceptsForNews(entry.getKey(),"NewsConceptCollection"));
			 combinedArray.put(combined);
			}
		JSONObject tempObj = new JSONObject();
		for (int i = 0; i < combinedArray.length(); i++) {
			
			for (int j = i+1; j < combinedArray.length(); j++) {
				Date newsDate1 = (Date) combinedArray.getJSONObject(i).get("dateValue");
					Date newsDate2 = (Date) combinedArray.getJSONObject(j).get("dateValue");
					
					if(newsDate1.before(newsDate2)){
						tempObj = combinedArray.getJSONObject(j);
						combinedArray.put(j, combinedArray.getJSONObject(i));
						combinedArray.put(i,tempObj);
						
					}
			}
		}
		latestNewsObject.put("status", "success");
		latestNewsObject.put("latestNews", combinedArray);
		response.setContentType("application/json");
		response.getWriter().write(callback+"("+latestNewsObject.toString()+")");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
