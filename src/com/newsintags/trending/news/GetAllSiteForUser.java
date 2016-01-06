package com.newsintags.trending.news;

import java.io.IOException;

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
 * Servlet implementation class GetAllSiteForUser
 */
@WebServlet("/GetAllSiteForUser")
public class GetAllSiteForUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllSiteForUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String screenName = request.getParameter("screenName");
		String callback = request.getParameter("callback");
		JSONObject finalReturnObject = new JSONObject();
		try{
		JSONObject userSites = MongoDbUtil.getAllUserSites(screenName);
		finalReturnObject.put("UserFollowedSites", userSites);
		DB db = DbHelper.getDbConnection();
		 DBCollection siteDoc = db.getCollection("SiteCollection");
		 DBCursor cursor = siteDoc.find();
		 JSONObject sitetobj ;
		 JSONArray combined = new JSONArray();
			 while(cursor.hasNext()){
				 sitetobj = new JSONObject();
				 DBObject dbobj = cursor.next();
				 sitetobj.put("id",dbobj.get("_id").toString());
				 sitetobj.put("name",(String)dbobj.get("name"));
				 sitetobj.put("picUrl",(String)dbobj.get("picUrl"));
				 combined.put(sitetobj);
			 }
		finalReturnObject.put("ListOfAllSites", combined);
		finalReturnObject.put("status", "success");
		response.setContentType("application/json");
		response.getWriter().write(callback+"("+finalReturnObject.toString()+")");
		}
	
		catch(Exception e){
			e.printStackTrace();
			finalReturnObject.put("status", "failure");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+finalReturnObject.toString()+")");
		}
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
