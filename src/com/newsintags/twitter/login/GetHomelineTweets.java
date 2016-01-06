package com.newsintags.twitter.login;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.json.XML;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.newsintags.alchemy.api.AlchemyAPI;
import com.newsintags.trending.news.GetAlchemyAPIEntity;
import com.newsintags.util.MongoDbUtil;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class GetHomelineTweets {
	public static void getAllTweets(String siteId){
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
		   .setOAuthConsumerKey("U189kdERceDEhP1oYJyKBCKKi")
		   .setOAuthConsumerSecret("dXOkzWQuuWvIO3oQa2R3ozTmov71AAbsanLUgfDfnz5BPMJ8DG")
		   .setOAuthAccessToken("3258808382-k7rWkpA7l3OVmbXPocizZI60yuvbv9iCrUeRCSg")
		   .setOAuthAccessTokenSecret("XMGnR4uUhnHCdnTy08hEUvy3IT5uKsvxt8XeNwsA9qrvM");
		 TwitterFactory tf = new TwitterFactory(cb.build());
		 Twitter twitter = tf.getInstance();
		 ResponseList<Status> tweetsList;
		 int i = 0;
		 try {
				tweetsList = twitter.getHomeTimeline();
				for(Status tweet:tweetsList){
					i += 1;
					if(i > 10){
						System.out.println("Tweets count: "+i);
						break;
					}
					//System.out.println("Tweet to be entered: "+getTweet(tweet.getText())+"----");
					//System.out.println("--"+getTweet(tweet.getText()).trim()+"--");
					if((getTweet(tweet.getText()).trim()).isEmpty())
					{
						continue;
					}
					String url = getTweetURL(tweet.getText());
					String sentiment = getSentiment(getTweet(tweet.getText()));
					Date tweetDate = tweet.getCreatedAt();
					DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
					df.format(tweetDate);
					String dateDiff = timeDifference(tweetDate);
					String newsId = MongoDbUtil.insertNews(getTweet(tweet.getText()), url,dateDiff, sentiment,tweet.getUser().getScreenName(),tweet.getUser().getBiggerProfileImageURL(),siteId);
					GetAlchemyAPIEntity.getEntityFromNews(getTweet(tweet.getText()), newsId,tweetDate);
				}
				
			} catch (TwitterException e) {
				e.printStackTrace();
			}
		 
	}
	
	public static String getTweetURL(String tweet){
		int n = tweet.lastIndexOf("http");
		if( n != -1){
			return "http"+tweet.split("http", 2)[1];	
		}
		return "";
	}
	
	public static String getTweet(String tweet)
	{
		int n = tweet.lastIndexOf("http");
		if( n != -1){
			return tweet.split("http", 2)[0];	
		}
		return tweet;
	}
	public static String getSentiment(String text) {
		try{
		AlchemyAPI alchemyObj = AlchemyAPI.GetInstanceFromFile("api_key.txt");
		System.out.println("sentiment text:"+text);
		Document doc = alchemyObj.TextGetTextSentiment(text);
		String xmlResult = getStringFromDocument(doc);
		org.json.JSONObject soapDatainJsonObject = XML.toJSONObject(xmlResult);
		String sentiment = (String)soapDatainJsonObject.getJSONObject("results").getJSONObject("docSentiment").get("type");
		if(sentiment != null)
			return sentiment;
		else 
			return "";
		}
		catch(IOException e){
			e.printStackTrace();
		}
		catch(XPathExpressionException e){
			e.printStackTrace();
		}
		catch(SAXException e){
			e.printStackTrace();
		}
		catch(ParserConfigurationException e){
			e.printStackTrace();
		}
		return "";
			
	}
	private static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
	
	public static void main(String[] args) {
		//getAllTweets();
	}
	
	public static String timeDifference(Date dt1)
	{
		Date dt2 = new Date();

        long diff = dt2.getTime() - dt1.getTime();
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        
        int diffInDays = (int) ((dt2.getTime() - dt1.getTime()) / (1000 * 60 * 60 * 24));

        if (diffInDays > 1) {
            return diffInDays+"d";
        } else if (diffHours > 0) {
            return diffHours+"h";
        } else if (diffMinutes > 0) {
        	return diffMinutes+"m";
        } else
        	return diffSeconds+"s";
	}

}
