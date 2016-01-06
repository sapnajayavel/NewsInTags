package com.newsintags.trending.news;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetSentimentNews
 */
@WebServlet("/GetSentimentNews")
public class GetSentimentNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSentimentNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String callback = request.getParameter("callback");
		String sentiment = request.getParameter("sentiment");
		DBCursor cursor = MongoDbUtil.getCursorObject("sentiment", sentiment, "NewsCollection");
		JSONObject sentimentNewsObject = new JSONObject();
		if(cursor!=null)
		{
			JSONArray combinedArray = new JSONArray();
			
			JSONObject jsonObject ;
			JSONObject jsonSiteObject ;
			JSONObject combinedObject ;
			while(cursor.hasNext()){
				combinedObject = new JSONObject();
				BasicDBObject newsObj = (BasicDBObject) cursor.next();
				System.out.println("Sentiment news : "+newsObj.toString());
				String siteId = newsObj.getString("siteId");
				BasicDBObject siteObj = (BasicDBObject) MongoDbUtil.getDBObject(siteId, "SiteCollection");
				jsonObject = new JSONObject(newsObj.toString());
				jsonSiteObject = new JSONObject(siteObj.toString());
				combinedObject.put("newsDetails", jsonObject);
				combinedObject.put("siteDetails", jsonSiteObject);
				combinedArray.put(combinedObject);
			}
			sentimentNewsObject.put("status", "success");
			sentimentNewsObject.put("sentimentBasedNews", combinedArray);
			System.out.println("Sentiment based news success"+sentimentNewsObject.toString());
		}
		else
		{
			sentimentNewsObject.put("status", "error");
		}
		response.setContentType("application/json");
		response.getWriter().write(callback+"("+sentimentNewsObject.toString()+")");
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
