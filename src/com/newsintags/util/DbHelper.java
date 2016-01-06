package com.newsintags.util;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class DbHelper {
	static DB db= null;
	public static DB getDbConnection(){
	
		if(db==null){
		        MongoClient mongo1 = new MongoClient(new MongoClientURI("mongodb://test:test@ds041380.mongolab.com:41380/appdatabase"));
				db = mongo1.getDB("appdatabase");       	
		}
		System.out.println(db);
         return db;
	}
	
	public void dropCollection(String collectionName,DB db)
	{
        DBCollection table = db.getCollection(collectionName);
        table.drop();
	}
}
