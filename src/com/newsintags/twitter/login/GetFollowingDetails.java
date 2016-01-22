package com.newsintags.twitter.login;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;




import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.newsintags.util.DbHelper;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetFollowingDetails
 */
@WebServlet("/GetFollowingDetails")
public class GetFollowingDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetFollowingDetails() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String userName = request.getParameter("userName");
		String callback = request.getParameter("callback");
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("U189kdERceDEhP1oYJyKBCKKi")
		   .setOAuthConsumerSecret("dXOkzWQuuWvIO3oQa2R3ozTmov71AAbsanLUgfDfnz5BPMJ8DG")
		   .setOAuthAccessToken("3258808382-k7rWkpA7l3OVmbXPocizZI60yuvbv9iCrUeRCSg")
		   .setOAuthAccessTokenSecret("XMGnR4uUhnHCdnTy08hEUvy3IT5uKsvxt8XeNwsA9qrvM");
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		JSONObject finalObject = new JSONObject();
		JSONArray finalArray = new JSONArray();
		String following;
		DB db = DbHelper.getDbConnection();
		 DBCollection siteDoc = db.getCollection("ConceptCollection");
		 DBCollection userWishlistDoc = db.getCollection("UserWishlist");
		 DBCursor cursor = null;
		 BasicDBObject conceptObj = null;
		 String conceptId;
		 String conceptName;
		 JSONObject conceptNewsObj = new JSONObject();
		 JSONArray finalNewsArray = new JSONArray();
		try {
			long cursorNew = -1;
			List<User> users=twitter.getFriendsList("nebulatechies7", cursorNew);
			BasicDBObject whereQuery = new BasicDBObject();
		    whereQuery.put("userId", userName);
		    DBCursor cur ;
		   // System.out.println("Count cur"+cur.count());
		    String concept;
		    ArrayList<String> concArr = new ArrayList<String>();
		    cur =  userWishlistDoc.find(whereQuery);
		    while(cur.hasNext())
			 {
		    	concArr.add(cur.next().get("userLike").toString());
			 }
			for(User u: users){
				following=u.getName();
				cursor = siteDoc.find();
				while(cursor.hasNext())
				{
					conceptObj = (BasicDBObject) cursor.next();
					conceptId = conceptObj.get("_id").toString();
					conceptNewsObj = new JSONObject();
					conceptName = (String) conceptObj.get("concept");
					//finalArray.put(conceptName+" ___ "+following);
					
					if(following.toLowerCase().contains(conceptName.toLowerCase()) || conceptName.toLowerCase().contains(following.toLowerCase())){
						conceptNewsObj.put("following", following);
						finalNewsArray = MongoDbUtil.getNewsForConcepts(conceptId);
						conceptNewsObj.put("newsArray", finalNewsArray);
						finalArray.put(conceptNewsObj);
					}	
					//cur =  userWishlistDoc.find(whereQuery);
					 
					 for (int i = 0; i < concArr.size(); i++) {
						 concept = concArr.get(i);
						 System.out.println("User concepts" + concept + ":"+conceptName);
					    	if(concept.toLowerCase().contains(conceptName.toLowerCase()) || conceptName.toLowerCase().contains(concept.toLowerCase())){
								conceptNewsObj.put("following", following);
								finalNewsArray = MongoDbUtil.getNewsForConcepts(conceptId);
								conceptNewsObj.put("newsArray", finalNewsArray);
								finalArray.put(conceptNewsObj);
							}
						}
				}
			}
			
		  
		   
			finalObject.put("interestFeeds", finalArray);
			finalObject.put("status", "success");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+finalObject.toString()+")");
		} catch (TwitterException e) {
			
			finalObject.put("status", "failure");
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+finalObject.toString()+")");
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
