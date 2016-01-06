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

/**
 * Servlet implementation class FollowNewsSite
 */
@WebServlet("/FollowNewsSite")
public class FollowNewsSite extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public FollowNewsSite() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String callback = request.getParameter("callback");
		 DB db = DbHelper.getDbConnection();
		 DBCollection siteDoc = db.getCollection("SiteCollection");
		 DBCursor cursor = siteDoc.find();
		 JSONObject sitetobj ;
		 JSONArray combined = new JSONArray();
		 JSONObject siteCollectionObj = new JSONObject();
		 while(cursor.hasNext()){
			 sitetobj = new JSONObject();
			 DBObject dbobj = cursor.next();
			 sitetobj.put("id",dbobj.get("_id").toString());
			 sitetobj.put("name",(String)dbobj.get("name"));
			 sitetobj.put("picUrl",(String)dbobj.get("picUrl"));
			 combined.put(sitetobj);
		 }
		 siteCollectionObj.put("status", "success");
		 siteCollectionObj.put("siteDetails", combined);
		 response.setContentType("application/json");
		 response.getWriter().write(callback+"("+siteCollectionObj.toString()+")");
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
