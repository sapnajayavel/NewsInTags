package com.newsintags.trending.news;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.newsintags.util.DbHelper;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetSimilarNews
 */
@WebServlet("/GetSimilarNews")
public class GetSimilarNews extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetSimilarNews() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String newsId = request.getParameter("newsId");
		String callback = request.getParameter("callback");
		JSONObject finalObj = new JSONObject();
		ArrayList<String> conceptArrarNews = MongoDbUtil.getAllConceptsForNewsList(newsId,"NewsConceptCollection"); 
		finalObj.put("conceptDetails",MongoDbUtil.getAllConceptsForNews(newsId,"NewsConceptCollection"));
		DB db = DbHelper.getDbConnection();
		DBCollection collection = db.getCollection("NewsCollection");
		DBCursor cursor = collection.find();
		JSONObject newsObject = new JSONObject();
		JSONArray matchingNewArray = new JSONArray();
		String id;
		int count = 0;
		while (cursor.hasNext()) {
			DBObject dbObj = cursor.next();
			id = dbObj.get("_id").toString();
			ArrayList<String> conceptArr = MongoDbUtil.getAllConceptsForNewsList(id,"NewsConceptCollection");
			count = 0;
			if(!newsId.equals(id)){
				for(String indConcept: conceptArr)
				{
					for(String indConceptNew : conceptArrarNews)
					{
						if(indConcept.contains(indConceptNew) || indConceptNew.contains(indConcept)){
							count++;
						}
						
					}	
				
				}
				if(count >= 1){
					newsObject = new JSONObject(MongoDbUtil.getDBObject(id,"NewsCollection").toString());
					matchingNewArray.put(newsObject);
				}
		 }
			
		}
		finalObj.put("matchingNews", matchingNewArray);
		finalObj.put("newsDetails", new JSONObject(MongoDbUtil.getDBObject(newsId,"NewsCollection").toString()));
		finalObj.put("status", "success");
		response.getWriter().write(callback+"("+finalObj.toString()+")");
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
