package com.newsintags.twitter.login;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.BasicAuthorization;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;



public class GetUserLoggedIn {
	public static void main(String[] args) throws TwitterException {
		/*ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();

	     configurationBuilder.setOAuthConsumerKey("iGb0FmzNNv4rYbZSu12H67KFf");
	     configurationBuilder.setOAuthConsumerSecret("hr8BNxFi1YjEP3NcKawIfgZTr5EkyVnjX7H7WxRSpCI92A4eAN");
	     Configuration configuration = configurationBuilder.build();
	  
	     Twitter twitter = new TwitterFactory(configuration).getInstance(new BasicAuthorization("591020695", "saibaba2387saibaba"));
	     

	     AccessToken token = twitter.getOAuthAccessToken();
	     System.out.println("Access Token " +token );

	     String name = token.getScreenName();
	     System.out.println("Screen Name" +name);

	     System.out.println("Twitter: "+token);*/
		
			String base = "https://api.twitter.com/";
			ConfigurationBuilder cb = new ConfigurationBuilder();
//    cb.setSearchBaseURL(HTTP_IDENTI_CA_API);
    /*cb.setOAuthAccessTokenURL(base + "oauth/access_token");
    cb.setOAuthAuthorizationURL(base + "oauth/authorize");
    cb.setOAuthRequestTokenURL(base + "oauth/request_token");*/
    cb.setOAuthConsumerKey("iGb0FmzNNv4rYbZSu12H67KFf");
    cb.setOAuthConsumerSecret("hr8BNxFi1YjEP3NcKawIfgZTr5EkyVnjX7H7WxRSpCI92A4eAN");
    cb.setIncludeEntitiesEnabled(true);
    cb.setJSONStoreEnabled(true);
    
    Configuration conf = cb.build() ;

    BasicAuthorization auth = new BasicAuthorization("@sapz811","saibaba2387saibaba");
    Twitter twitterInstance = new TwitterFactory(conf).getInstance(auth);
    System.out.println("Print"+twitterInstance.getHomeTimeline());
    
	      
	}
}
