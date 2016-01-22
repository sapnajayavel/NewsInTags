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
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.newsintags.util.DbHelper;

/**
 * Servlet implementation class GetAllConcepts
 */
@WebServlet("/GetAllConcepts")
public class GetAllConcepts extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetAllConcepts() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 String callback = request.getParameter("callback");
		 DB db = DbHelper.getDbConnection();
		 DBCollection conceptDoc = db.getCollection("ConceptCollection");
		 DBCollection newsConceptDoc = db.getCollection("NewsConceptCollection");
		 DBCursor cursor = conceptDoc.find();
		 JSONObject conceptobj ;
		 JSONArray combined = new JSONArray();
		 JSONObject conceptCollectionObj = new JSONObject();
		 BasicDBObject whereQue;
		  DBCursor cursorNew;
		 while(cursor.hasNext()){
			 conceptobj = new JSONObject();
			 DBObject dbobj = cursor.next();
			 conceptobj.put("id",dbobj.get("_id").toString());
			 whereQue = new BasicDBObject();
	    	 whereQue.put("conceptId", dbobj.get("_id").toString() );
	    	 cursorNew = newsConceptDoc.find(whereQue);
			 conceptobj.put("concept",(String)dbobj.get("concept"));
			 conceptobj.put("type",(String)dbobj.get("type"));
			 if(cursorNew.count() > 0)
				 conceptobj.put("newsCount",cursorNew.next().get("count"));
			 combined.put(conceptobj);
		 }
		 conceptCollectionObj.put("status", "success");
		 conceptCollectionObj.put("conceptDetails", combined);
		 response.setContentType("application/json");
		 response.getWriter().write(callback+"("+conceptCollectionObj.toString()+")");
		 
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
