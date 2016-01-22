package com.newsintags.twitter.login;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.conf.ConfigurationBuilder;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.newsintags.util.DbHelper;
import com.newsintags.util.MongoDbUtil;

/**
 * Servlet implementation class GetTwitterUserDetails
 */
@WebServlet("/GetTwitterUserDetails")
public class GetTwitterUserDetails extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTwitterUserDetails() {
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
		User user;
		List<User> users;
		org.json.JSONObject userDetails = new org.json.JSONObject();
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("UserWishlist");
		try {
			user = twitter.showUser("nebulatechies7");
			userDetails.put("id", user.getId());
			userDetails.put("profileUrl", user.getBiggerProfileImageURL());
			userDetails.put("screenName", user.getScreenName());
			userDetails.put("profileName", user.getName());
			userDetails.put("followersCount", user.getFollowersCount());
			userDetails.put("friendsCount", user.getFriendsCount());
			Date hitTime = new Date();
			DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
			df.format(hitTime);
			MongoDbUtil.insertUser(user.getName(),user.getBiggerProfileImageURL(),user.getScreenName(),hitTime);
			long cursorNew = -1;
			users=twitter.getFriendsList("nebulatechies7", cursorNew);
			userDetails.put("userWistList", users.size());
			for(User u: users){
				MongoDbUtil.insertUsersWishlist(user.getScreenName()+"", u.getName(), table);
			}
			response.setContentType("application/json");
			response.getWriter().write(callback+"("+userDetails.toString()+")");
		} catch (TwitterException e) {
			
			e.printStackTrace();
		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
