package com.newsintags.util;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.bson.types.ObjectId;
import org.json.JSONArray;
import org.json.JSONObject;
import org.xml.sax.SAXException;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.newsintags.trending.news.GetNewsFromSites;
import com.newsintags.twitter.login.GetHomelineTweets;

public class MongoDbUtil {
	public static String insertNews(String title,String url,String time, String sentiment, String userScreenName, String userImageUrl, String siteId){
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("NewsCollection");
		BasicDBObject document = new BasicDBObject();
        document.put("url", url);
        document.put("title", title);
        document.put("time", time);
        document.put("sentiment", sentiment);
        document.put("userScreenName", userScreenName);
        document.put("userImageUrl", userImageUrl);
        document.put("siteId", siteId);
        table.insert(document);
        return document.get("_id").toString();
	}
	public static String insertNewsFromSites(String title,String url,String time, String sentiment, String userScreenName, String userImageUrl, String description, String siteId){
		DB db = DbHelper.getDbConnection();
		DBCollection table = db.getCollection("NewsCollection");
		BasicDBObject document = new BasicDBObject();
        document.put("url", url);
        document.put("title", title);
        document.put("time", time);
        document.put("sentiment", sentiment);
        document.put("userScreenName", userScreenName);
        document.put("userImageUrl", userImageUrl);
        document.put("description", description);
        document.put("siteId", siteId);
        table.insert(document);
        return document.get("_id").toString();
	} 
	public static void insertConcepts(BasicDBObject conceptObject,String newsId, Date createdDate){
		//insert into conceptcollection
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection("ConceptCollection");
		conceptDoc.insert(conceptObject);
		
		//Insert into newsconceptcollection
		DBCollection newsConceptsDoc = db.getCollection("NewsConceptCollection");
		BasicDBObject document = new BasicDBObject();
        document.put("conceptId", conceptObject.get("_id").toString());
        BasicDBObject newsDoc = new BasicDBObject();
        newsDoc.put("id", newsId);
        newsDoc.put("date", createdDate);
        BasicDBList news = new BasicDBList();
        news.add(newsDoc);
        document.put("newsId", news);
        document.put("count", 1);
        newsConceptsDoc.insert(document);
        
	}
	
	public static void updateNewsConcept(String concept, String newsId, Date createdDate)
	{
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection("ConceptCollection");
		BasicDBObject whereQuery = new BasicDBObject();
    	whereQuery.put("concept", concept );
    	DBCursor cursor = conceptDoc.find(whereQuery);
    	String conceptId = null;
    	String uniqueId = null;
    	if(cursor.hasNext()) {
    	    conceptId = cursor.next().get("_id").toString();
    	}
    	DBCollection newsconceptDoc = db.getCollection("NewsConceptCollection");
    	BasicDBObject newsObj = new BasicDBObject();
        newsObj.put("news", newsId);
		BasicDBObject updateQuery  = new BasicDBObject();
		updateQuery .put("conceptId", conceptId );
		DBCursor newCursor = newsconceptDoc.find(updateQuery);
		int countValue = 0;
		if(newCursor.count()!=0)
		{
			countValue = (int)newCursor.next().get("count");
		}
		//Append to an array
		BasicDBObject newsDoc = new BasicDBObject();
        newsDoc.put("id", newsId);
        newsDoc.put("date", createdDate);
		BasicDBObject updateCommand = new BasicDBObject("$push",new BasicDBObject("newsId", newsDoc));
		BasicDBObject updateCount = new BasicDBObject("$set", new BasicDBObject().append("count", countValue+1));
		newsconceptDoc.update(updateQuery, updateCommand);
		newsconceptDoc.update(updateQuery, updateCount);
		
	}
	
	public static boolean checkIfConceptExists(String concept){
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection("ConceptCollection");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("concept", Pattern.compile(".*"+concept+".*" , Pattern.CASE_INSENSITIVE));
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()==0)
			return false;
		else 
			return true;
	}
	
	public static void insertSites()
	{
		DB db = DbHelper.getDbConnection();
		DBCollection siteDoc = db.getCollection("SiteCollection");
		/*BasicDBObject document1 = new BasicDBObject();
		document1.put("name", "thehindu");
		document1.put("url", "http://www.thehindu.com/news/?service=rss");
		document1.put("picUrl", "http://a515.phobos.apple.com/us/r30/Purple/v4/49/c2/b5/49c2b56e-5fc5-0bad-d3cf-210e60d34431/mzl.pturateb.png");
		BasicDBObject document2 = new BasicDBObject();
		document2.put("name", "ndtvnews");
		document2.put("url", "http://feeds.feedburner.com/NDTV-Trending");
		document2.put("picUrl", "http://static.dnaindia.com/sites/default/files/2015/06/05/343482-ndtv.png");
		BasicDBObject document3 = new BasicDBObject();
		document3.put("name", "twitter");
		siteDoc.insert(document1);
		siteDoc.insert(document2);
		siteDoc.insert(document3);*/
		BasicDBObject document4 = new BasicDBObject();
		document4.put("name", "ibnlive");
		document4.put("url", "http://www.ibnlive.com/rss/buzz.xml");
		document4.put("picUrl", "https://yt3.ggpht.com/-p4ZegXXtbqo/AAAAAAAAAAI/AAAAAAAAAAA/e5ImTV0oa1o/s900-c-k-no/photo.jpg");
		BasicDBObject document5 = new BasicDBObject();
		document5.put("name", "timesofindia");
		document5.put("url", "http://dynamic.feedsportal.com/pf/555218/http://toi.timesofindia.indiatimes.com/rssfeedstopstories.cms");
		document5.put("picUrl", "http://b.thumbs.redditmedia.com/P-YCfnOoryYcg8sPduNsyf-zCdZBDcKL3vQtv2icXKM.jpg");
		siteDoc.insert(document4);
		siteDoc.insert(document5);
		
	}
	
	
	public static HashMap<String,String> getTrendingNews()
	{
		/*DB db = DbHelper.getDbConnection();
		DBCollection siteDoc = db.getCollection("NewsConceptCollection");
		DBCursor cursor = siteDoc.find();
		HashMap<String,String> trendingNewsMap=new HashMap<String,String>(); 
		Iterator iter;
		ArrayList<String> aListFromMap = new ArrayList();
		ArrayList<String> aList = new ArrayList();
		while(cursor.hasNext()) {
			String trendingNewsId = null;
			Date trendingNewsDate = null;
			DBObject newsConceptObj = cursor.next();
			ArrayList<BasicDBObject> newsArray= (ArrayList<BasicDBObject>) newsConceptObj.get("newsId");String conceptId = newsConceptObj.get("_id").toString();
			//BasicDBList newsList = (BasicDBList) newsConceptObj.get("newsId");
			int newsCount = 0;
			for(BasicDBObject newObj : newsArray){
				String newsId = (String) newObj.get("id");
				if(trendingNewsMap.get(newsId) != null)
				{
					continue;
				}
				aList = getAllConceptsForNewsList(newsId,"NewsConceptCollection");
				//iterate map
				iter = trendingNewsMap.entrySet().iterator();
				int count = 0;
			    while (trendingNewsMap.size()>0 && iter.hasNext() ) {
			    	Map.Entry pair = (Map.Entry)iter.next();
			    	aListFromMap = getAllConceptsForNewsList((String)pair.getKey(),"NewsConceptCollection");
			    	for(String conceptIdFromAListFromMap : aListFromMap){
			    		if(aList.contains(conceptIdFromAListFromMap))
			    			count++;
			    	}
			    	if(count >= 2){
			    		break;
			    	}
			    }
			    if(count >= 2 && trendingNewsMap.size() > 1)
			    {
			    	continue;
			    }
			  //check for the 
				Date newsDate = (Date) newObj.get("date");
				trendingNewsDate = newsDate;
				trendingNewsId = newsId;
				if(trendingNewsId != null && (String)newsConceptObj.get("conceptId")!= null)
				{
					trendingNewsMap.put(trendingNewsId, (String)newsConceptObj.get("conceptId"));
				}
				if(newsCount++ < 10)
				{
					break;
				}
			}
			
		   
		}
		return trendingNewsMap;
*/		
		DB db = DbHelper.getDbConnection();
		DBCollection siteDoc = db.getCollection("NewsConceptCollection");
		DBCursor cursor = siteDoc.find();
		HashMap<String,String> trendingNewsMap=new HashMap<String,String>(); 
		Iterator iter;
		while(cursor.hasNext()) {
			String trendingNewsId = null;
			Date trendingNewsDate = null;
			DBObject newsConceptObj = cursor.next();
			ArrayList<BasicDBObject> newsArray= (ArrayList<BasicDBObject>) newsConceptObj.get("newsId");
			//BasicDBList newsList = (BasicDBList) newsConceptObj.get("newsId");
			for(BasicDBObject newObj : newsArray){
				String newsId = (String) newObj.get("id");
				if(trendingNewsMap.get(newsId) != null)
				{
					continue;
				}
				//check for the 
				Date newsDate = (Date) newObj.get("date");
				trendingNewsDate = newsDate;
				trendingNewsId = newsId;
				
				break;
				 
			}
			if(trendingNewsId != null && (String)newsConceptObj.get("conceptId")!= null)
			{
				trendingNewsMap.put(trendingNewsId, (String)newsConceptObj.get("conceptId"));
			}
		   
		}
		return trendingNewsMap;
	}
	
	public static HashMap<String,String> getLatestNews()
	{
		DB db = DbHelper.getDbConnection();
		DBCollection siteDoc = db.getCollection("NewsConceptCollection");
		DBCursor cursor = siteDoc.find();
		HashMap<String,String> latestNewsMap=new HashMap<String,String>();
		HashMap<String,String> finalNewsMap=new HashMap<String,String>();
		Map<Date, String> map = new TreeMap<Date, String>();
		
		while(cursor.hasNext()) {
			String latestNewsId = null;
			Date latestNewsDate = null;
			DBObject newsConceptObj = cursor.next();
			ArrayList<BasicDBObject> newsArray= (ArrayList<BasicDBObject>) newsConceptObj.get("newsId");
			//BasicDBList newsList = (BasicDBList) newsConceptObj.get("newsId");
			for(BasicDBObject newObj : newsArray){
				String newsId = (String) newObj.get("id");
				if(latestNewsMap.get(newsId) != null)
				{
					continue;
				}
				Date newsDate = (Date) newObj.get("date");
				System.out.println(latestNewsDate+"---"+newsDate);
				if (latestNewsDate == null || newsDate.after(latestNewsDate)){
					latestNewsDate = newsDate;
					latestNewsId = newsId;
				 }
			}
			
			if(latestNewsId != null && (String)newsConceptObj.get("conceptId")!=null)
			{
				map.put(latestNewsDate, latestNewsId);
				latestNewsMap.put(latestNewsId, (String)newsConceptObj.get("conceptId"));
			}
			//System.out.println("Latest date: "+latestNewsDate+"Latest id: "+latestNewsId+"Concept: "+(String)newsConceptObj.get("conceptId"));
			
		}
		for (Entry<Date, String> entry : map.entrySet()) {
			String value = entry.getValue();
			finalNewsMap.put(value, latestNewsMap.get(value));
		}
		return finalNewsMap;
		
	}
	public static DBObject getDBObject(String uniqueId,String collectionName){
		System.out.println("uniqueId"+uniqueId);
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("_id",new ObjectId(uniqueId));
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()>0){
			return cursor.next();
		}
		return null;
	}
	public static JSONArray getNewsForSite(String uniqueId,String collectionName){
		
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("siteId",uniqueId);
		DBCursor cursor = conceptDoc.find(whereQuery);
		JSONArray newsListForSite = new JSONArray();
		while(cursor.hasNext()){
			newsListForSite.put( new JSONObject(cursor.next().toString()));
		}
		return newsListForSite;
	}
	public static Date getDateFromNewsConceptCollection(String newsId,String conceptId,String collectionName){
		System.out.println("conceptId"+conceptId+"&&& newsId"+newsId);
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("conceptId",conceptId);
		whereQuery.put("newsId.id",newsId);
		/*BasicDBObject fields=new BasicDBObject("Notification.$", 1);

		DBCursor f = con.coll.find(query, fields);*/
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()>0){
			BasicDBList newsArray = (BasicDBList) cursor.next().get("newsId"); 
			BasicDBObject newsObject = (BasicDBObject) newsArray.get(0);
			Date newsDate = (Date) newsObject.get("date");
			return newsDate;
		}
		return null;
	}
	public static int getCountFromNewsConceptCollection(String newsId,String conceptId,String collectionName){
		System.out.println("conceptId"+conceptId+"&&& newsId"+newsId);
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("conceptId",conceptId);
		whereQuery.put("newsId.id",newsId);
		/*BasicDBObject fields=new BasicDBObject("Notification.$", 1);

		DBCursor f = con.coll.find(query, fields);*/
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()>0){
			int count = (int) cursor.next().get("count"); 
			
			return count;
		}
		return 0;
	}
	public static JSONArray getAllConceptsForNews(String newsId,String collectionName){
		System.out.println("newsId"+newsId);
		JSONArray conceptArray = new JSONArray();
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("newsId.id",newsId);
		DBCursor cursor = conceptDoc.find(whereQuery);
		
		if(cursor.count()>0){
			while(cursor.hasNext()){
				String conceptId = (String) cursor.next().get("conceptId"); 
				BasicDBObject conceptObj =  (BasicDBObject) getDBObject(conceptId,"ConceptCollection");
				JSONObject jsonconceptObj = new JSONObject(conceptObj.toString());
				conceptArray.put(jsonconceptObj);
			}
			return conceptArray;
		}
		else
			return null;
	}
	
	public static ArrayList<String> getAllConceptsForNewsList(String newsId,String collectionName){
		System.out.println("newsId"+newsId);
		ArrayList<String> conceptArray = new ArrayList(); 
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collectionName);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("newsId.id",newsId);
		DBCursor cursor = conceptDoc.find(whereQuery);
		DBObject conceptObj = new BasicDBObject();
		if(cursor.count()>0){
			while(cursor.hasNext()){
				String conceptId = (String) cursor.next().get("conceptId"); 
				conceptObj = getDBObject(conceptId,"ConceptCollection");
				String conceptValue =conceptObj.get("_id").toString();
				conceptArray.add(conceptValue);
			}
			return conceptArray;
		}
		else
			return conceptArray;
	}
	public static DBCursor getCursorObject(String key,String value, String collection){
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection(collection);
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put(key,value);
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()>0){
			return cursor;
		}
		return null;
	}
	
	public static boolean checkIfUserExists(String screenName){
		DB db = DbHelper.getDbConnection();
		DBCollection conceptDoc = db.getCollection("UserCollection");
		BasicDBObject whereQuery = new BasicDBObject();
		whereQuery.put("screenName", Pattern.compile(".*"+screenName+".*" , Pattern.CASE_INSENSITIVE));
		DBCursor cursor = conceptDoc.find(whereQuery);
		if(cursor.count()==0)
			return false;
		else 
			return true;
	}
	
	public static void insertUser(String profileName,String profilePhoto, String screenName, Date date){
		  if(checkIfUserExists(screenName))
		  {
			  return;
		  }
		  DB db = DbHelper.getDbConnection();
		  DBCollection table = db.getCollection("UserCollection");
		  BasicDBObject query = new BasicDBObject();
		  query.put("screenName", screenName);
		  BasicDBObject document = new BasicDBObject();
		  document.put("profileName", profileName);
		  document.put("profilePhoto", profilePhoto);
		  document.put("screenName", screenName);
		  document.put("hitTime", date);
		  BasicDBList concept = new BasicDBList();
		  document.put("conceptId", concept);
		  BasicDBList newsSite = new BasicDBList();
		  document.put("siteId", newsSite);
		  table.insert(document);
	}
	
	
	 public static void insertUserConcept(String conceptId, String screenName)
	 {
	  DB db = DbHelper.getDbConnection();
	  DBCollection userDoc = db.getCollection("UserCollection");
	  BasicDBObject updateQuery  = new BasicDBObject();
	  updateQuery.put("screenName", screenName );
	  
	  //Append to an array
	  BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("conceptId", conceptId));
	  userDoc.update(updateQuery, updateCommand);
	  
	 }
	 
	 public static void insertUserNewsSite(String siteId, String screenName)
	 {
	  DB db = DbHelper.getDbConnection();
	  DBCollection userDoc = db.getCollection("UserCollection");
	  BasicDBObject updateQuery  = new BasicDBObject();
	  updateQuery.put("screenName", screenName );
	  
	  //Append to an array
	  BasicDBObject updateCommand = new BasicDBObject("$push", new BasicDBObject("siteId", siteId));
	  userDoc.update(updateQuery, updateCommand);
	  
	 }
	 
	 public static JSONObject getAllUserConcepts(String screenName)
	 {
	  DBCursor dbcursor = getCursorObject("screenName",screenName,"UserCollection");
	  JSONObject firstObj ;
	  JSONArray array = new JSONArray();
	  JSONObject combinedObj = new JSONObject() ;
	  while(dbcursor.hasNext())
	  {
		  BasicDBObject userConceptObj  = (BasicDBObject) dbcursor.next();
		  BasicDBList conceptArray= (BasicDBList) userConceptObj.get("conceptId");
		  DB db = DbHelper.getDbConnection();
		  DBCollection conceptDoc ;
		  DBCollection newsConceptDoc ;
		  DBCursor cursorNew;
		  DBCursor cursor;
		  if(conceptArray.size() > 0)
		  {
			  for(int i=0; i<conceptArray.size();i++){
				  firstObj = new JSONObject();
				  String conceptId =(String) conceptArray.get(i);
				  conceptDoc = db.getCollection("ConceptCollection");
				  newsConceptDoc = db.getCollection("NewsConceptCollection");
				  BasicDBObject whereQuery = new BasicDBObject();
			      whereQuery.put("_id", new ObjectId(conceptId ));
			      firstObj.put("conceptId", conceptId);
			      cursor = conceptDoc.find(whereQuery);
			      if(cursor.hasNext()){
			    	String conceptName = (String) cursor.next().get("concept");
			    	System.out.println("conceptName" + conceptName + "conceptId" + conceptId);
			    	BasicDBObject whereQue = new BasicDBObject();
			    		whereQue.put("conceptId", conceptId );
			    		cursorNew = newsConceptDoc.find(whereQue);
			    		firstObj.put("conceptName", conceptName);
			    		if(cursorNew.count() > 0)
			    			firstObj.put("newsCount", cursorNew.next().get("count"));
			    	}
			    	array.put(firstObj);
			  } 
		  }
	  }
	  combinedObj.put("conceptDetails", array);
	  return combinedObj;
	  
	 }
	 
	 public static JSONObject getAllUserSites(String screenName)
	 {
	  DBCursor dbcursor = getCursorObject("screenName",screenName,"UserCollection");
	  JSONObject firstObj ;
	  JSONArray array = new JSONArray();
	  JSONObject combinedObj = new JSONObject() ;
	  while(dbcursor.hasNext())
	  {
		  BasicDBObject userSiteObj  = (BasicDBObject) dbcursor.next();
		  BasicDBList siteArray= (BasicDBList) userSiteObj.get("siteId");
		  if(siteArray.size() > 0)
		  {
			  for(int i=0; i<siteArray.size();i++){
				  firstObj = new JSONObject();
				  String siteId =(String) siteArray.get(i);
				  DB db = DbHelper.getDbConnection();
					DBCollection siteDoc = db.getCollection("SiteCollection");
					BasicDBObject whereQuery = new BasicDBObject();
			    	whereQuery.put("_id", new ObjectId(siteId ));
			    	firstObj.put("siteId", siteId);
			    	DBCursor cursor = siteDoc.find(whereQuery);
			    	if(cursor.hasNext()){
			    		DBObject dbobj = cursor.next();
			    		String siteName = (String) dbobj.get("name");
			    		String picUrl = (String) dbobj.get("picUrl");
			    		firstObj.put("siteName", siteName);
			    		firstObj.put("picUrl", picUrl);
			    	}
			    	array.put(firstObj);
			  } 
		  }
	  }
	  combinedObj.put("siteDetails", array);
	  return combinedObj;
	  
	 }
	 public static JSONArray getNewsForConcepts(String conceptId)
	 {
		 DB db = DbHelper.getDbConnection();
			DBCollection newsConceptDoc = db.getCollection("NewsConceptCollection");
			DBCollection newsDoc = db.getCollection("NewsCollection");
			 JSONObject firstObj ;
			  JSONArray array = new JSONArray();
			  JSONObject combinedObj = new JSONObject() ;
			BasicDBObject searchQuery  ;
			BasicDBObject updateQuery  = new BasicDBObject();
			updateQuery .put("conceptId", conceptId );
			DBCursor cursor = newsConceptDoc.find(updateQuery);
			while(cursor.hasNext()) {
				DBObject newsConceptObj = cursor.next();
				System.out.println("Cursor"+newsConceptObj.toString());
				
				
				ArrayList<BasicDBObject> newsArray= (ArrayList<BasicDBObject>) newsConceptObj.get("newsId");
				System.out.println("Array"+ newsArray.toString());
				//BasicDBList newsList = (BasicDBList) newsConceptObj.get("newsId");
				for(BasicDBObject newObj : newsArray){
					String newsId = (String) newObj.get("id");
					searchQuery = new BasicDBObject();
					searchQuery.put("_id", new ObjectId(newsId));
					DBCursor newCursor = newsDoc.find(searchQuery);
					if(newCursor.hasNext())
					{
						 JSONObject jsonnewsObj = new JSONObject(newCursor.next().toString());
						 array.put(jsonnewsObj);
					}
				}
				
			   
			}
			
			
		 return array;
		 
	 }
	
	 public static void insertUsersWishlist(String userName, String userLikes, DBCollection table)
	 {
			BasicDBObject document = new BasicDBObject();
				if(checkIfConceptExists(userLikes,userName)){
			        document.put("userId", userName);
			        document.put("userLike", userLikes);
			        table.insert(document);
				}
		        
			
			
	 }

	 public static void updateUsersWishlist(String userName, String likedConcept)
	 {
		 DB db = DbHelper.getDbConnection();
		 DBCollection userWishlistDoc = db.getCollection("UserWishlist");
		 BasicDBObject document ;
		 System.out.println("LikedConcept" + likedConcept);
		 for (String concept: likedConcept.split(",")){
			 System.out.println("Splitting concepts"+concept);
	    	if(checkIfConceptExists(concept,userName)){
	    		document = new BasicDBObject();
	    		document.put("userId", userName);
		        document.put("userLike", concept);
		        userWishlistDoc.insert(document);
	    	}
		 }
		 
	 }
	 
	 public static boolean checkIfConceptExists(String concept,String userId)
	 {
		 DB db = DbHelper.getDbConnection();
		 DBCollection userWishlistDoc = db.getCollection("UserWishlist");
		 BasicDBObject whereQuery = new BasicDBObject();
	     whereQuery.put("userLike", concept);
	     whereQuery.put("userId", userId);
	    	DBCursor cursor = userWishlistDoc.find(whereQuery);
	    	if(cursor.count() == 0){
	    		return true;
	    	}
	    return false;
	 }
	 
	 
	 
}
