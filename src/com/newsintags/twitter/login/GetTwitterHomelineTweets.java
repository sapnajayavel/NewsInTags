package com.newsintags.twitter.login;

import java.io.IOException;




import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

import org.json.*;

/**
 * Servlet implementation class GetTwitterUserDetails
 */
@WebServlet("/GetTwitterUserDetailsTest")
public class GetTwitterHomelineTweets extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetTwitterHomelineTweets() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("U189kdERceDEhP1oYJyKBCKKi")
		   .setOAuthConsumerSecret("dXOkzWQuuWvIO3oQa2R3ozTmov71AAbsanLUgfDfnz5BPMJ8DG")
		   .setOAuthAccessToken("3258808382-k7rWkpA7l3OVmbXPocizZI60yuvbv9iCrUeRCSg")
		   .setOAuthAccessTokenSecret("XMGnR4uUhnHCdnTy08hEUvy3IT5uKsvxt8XeNwsA9qrvM");
		 TwitterFactory tf = new TwitterFactory(cb.build());
		 Twitter twitter = tf.getInstance();
		 JSONObject tweets ;
		 JSONObject homelineTweets = new JSONObject();
		 JSONArray homelineTweetsArray = new JSONArray();
		 ResponseList<Status> tweetsList;
		try {
			tweetsList = twitter.getHomeTimeline();
			for(Status tweet:tweetsList){
				tweets = new JSONObject();
				tweets.put("id", tweet.getId());
				tweets.put("text", tweet.getText());
				tweets.put("dateValue", tweet.getCreatedAt().getDate());
				tweets.put("source", tweet.getSource());
				tweets.put("favoriteCount", tweet.getFavoriteCount());
				
				homelineTweetsArray.put(tweets);
				
			}
			homelineTweets.put("homelineTweets", homelineTweetsArray);
			response.setContentType("application/json");
			response.getWriter().write(homelineTweets.toString());
			
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
